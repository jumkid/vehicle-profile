package com.jumkid.vehicle.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.jumkid.share.security.AccessScope;
import com.jumkid.share.service.dto.PagingResults;
import com.jumkid.vehicle.enums.VehicleField;
import com.jumkid.vehicle.exception.VehicleImportException;
import com.jumkid.vehicle.exception.VehicleSearchException;
import com.jumkid.vehicle.model.VehicleSearch;
import com.jumkid.vehicle.service.dto.VehicleFieldValuePair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Slf4j
@Repository
public class VehicleSearchRepositoryImpl implements VehicleSearchRepository {

    private static final String ES_IDX_ENDPOINT = "vehicle";
    private static final String AGG_NAME = "vehicle-agg";

    @Value("#{${elasticsearch.aggregation.fields.mapping}}")
    private Map<String, String> aggregationFieldsMapping;

    private final ElasticsearchClient esClient;

    public VehicleSearchRepositoryImpl(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    @Override
    public PagingResults<VehicleSearch> searchByUser(String keyword, Integer size, Integer page, String userId) {
        Query byUser = MatchQuery.of(m -> m
                .field(VehicleField.CREATEDBY.value())
                .query(userId)
        )._toQuery();

        return searchWithKeywordAndFilter(keyword, byUser, size, page);
    }

    @Override
    public PagingResults<VehicleSearch> searchByAccessScope(String keyword, Integer size, Integer page,
                                                            AccessScope accessScope) throws VehicleSearchException {
        Query byAccessScope = MatchQuery.of(m -> m
                .field(VehicleField.ACCESSSCOPE.value())
                .query(accessScope.value())
        )._toQuery();

        return searchWithKeywordAndFilter(keyword, byAccessScope, size, page);
    }

    @Override
    public PagingResults<VehicleSearch> searchByMatchFields(Integer size, Integer page,
                                                            List<VehicleFieldValuePair<String>> matchFields,
                                                            AccessScope accessScope) throws VehicleSearchException {
        Query boolQuery = buildBoolQuery(matchFields, accessScope);

        Integer from = getFrom(size, page);

        try {
            SearchResponse<VehicleSearch> response = esClient.search(builder -> builder
                            .index(ES_IDX_ENDPOINT)
                            .query(boolQuery)
                            .from(from)
                            .size(size)
                            .sort(getSortOption())
                            .fields(f -> f.field(VehicleField.ID.value())),
                    VehicleSearch.class);

            return buildPagingResults(size, page, response);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("failed to search by match fields {}", ioe.getMessage());
            throw new VehicleSearchException("failed to search by match fields");
        }
    }

    @Override
    public List<String> searchForAggregation(VehicleField field, List<VehicleFieldValuePair<String>> matchFields, Integer size)
            throws VehicleSearchException {
        Query boolQuery = buildBoolQuery(matchFields, AccessScope.PUBLIC);

        try {
            SearchResponse<VehicleSearch> response = esClient.search(builder -> builder
                    .index(ES_IDX_ENDPOINT)
                    .size(0) // do not return hits
                    .query(boolQuery)
                    .aggregations(AGG_NAME, aggBuilder -> aggBuilder.terms(t -> t.field(getAggFieldName(field)).size(size))),
                    VehicleSearch.class);

            Aggregate aggregate = response.aggregations().get(AGG_NAME);
            if (aggregate.isSterms()) {
                List<StringTermsBucket> termBuckets = aggregate.sterms().buckets().array();
                return termBuckets.stream().map(stringTermsBucket -> stringTermsBucket.key().stringValue()).toList();
            } else
            if (aggregate.isLterms()) {
                List<LongTermsBucket> termBuckets = aggregate.lterms().buckets().array();
                return termBuckets.stream().map(termsBucket -> Long.toString(termsBucket.key())).toList();
            } else
            if (aggregate.isDterms()) {
                List<DoubleTermsBucket> termBuckets = aggregate.dterms().buckets().array();
                return termBuckets.stream().map(termsBucket -> Double.toString(termsBucket.key())).toList();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("failed to search vehicle profiles {}", ioe.getMessage());
            throw new VehicleSearchException("failed to search vehicle profiles");
        }
        return Collections.emptyList();
    }

    private String getAggFieldName(VehicleField field) {
        String key = field.value();
        return aggregationFieldsMapping.getOrDefault(key, key);
    }

    private PagingResults<VehicleSearch> searchWithKeywordAndFilter(String keyword,
                                                                    Query queryFilter,
                                                                    Integer size,
                                                                    Integer page) throws VehicleSearchException {
        Integer from = getFrom(size, page);

        try {
            SearchResponse<VehicleSearch> response = esClient.search(builder -> builder
                            .index(ES_IDX_ENDPOINT)
                            .size(size)
                            .from(from)
                            .q(keyword)
                            .sort(getSortOption())
                            .postFilter(queryFilter)
                            .fields(f -> f.field(VehicleField.ID.value())),
                    VehicleSearch.class);

            return buildPagingResults(size, page, response);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("failed to aggregate search {}", ioe.getMessage());
            throw new VehicleSearchException("failed to do aggregate search");
        }
    }

    private PagingResults<VehicleSearch> buildPagingResults(Integer size, Integer page,
                                                            SearchResponse<VehicleSearch> response) {
        assert response.hits().total() != null;
        final long total = response.hits().total().value();
        log.info("found total {} vehicle profiles for keyword search", response.hits().total().value());

        List<VehicleSearch> results = response.hits().hits().stream().map(Hit::source).toList();

        return PagingResults.<VehicleSearch>builder()
                .total(total)
                .page(page)
                .size(size)
                .resultSet(results)
                .build();
    }

    private Query buildBoolQuery(List<VehicleFieldValuePair<String>> matchFields, AccessScope accessScope) {
        List<Query> matchQueries = new ArrayList<>();

        matchQueries.add(getAccessScopeQuery(accessScope));

        Query query;
        for (VehicleFieldValuePair<String> fieldValuePair : matchFields) {
            String matchFieldName = getAggFieldName(fieldValuePair.getField());
            query = MatchQuery
                    .of(m -> m.field(matchFieldName).query(fieldValuePair.getValue()))
                    ._toQuery();
            matchQueries.add(query);
        }
        return BoolQuery.of(b -> b.must(matchQueries))._toQuery();
    }

    private Query getAccessScopeQuery(AccessScope accessScope) {
        return MatchQuery
                .of(m -> m.field(VehicleField.ACCESSSCOPE.value()).query(accessScope.value()))
                ._toQuery();
    }

    private SortOptions getSortOption() {
        //sort by model year
        return SortOptions
                .of(soBuilder -> soBuilder.field(FieldSort
                        .of(fs->fs.field(VehicleField.CREATIONDATE.value()).order(SortOrder.Desc))));
    }

    private Integer getFrom(Integer size, Integer page){
        final int _size = ( size == null ) ? 20 : size;
        final int _page = ( page == null ) ? 0 : page;
        return _size * (_page - 1);
    }

    @Override
    public VehicleSearch save(VehicleSearch vehicleSearch) {
        try {
            IndexRequest<VehicleSearch> request = new IndexRequest.Builder<VehicleSearch>()
                                                                    .index(ES_IDX_ENDPOINT)
                                                                    .document(vehicleSearch)
                                                                    .id(vehicleSearch.getId())
                                                                    .build();
            //Synchronous execution
            IndexResponse response = esClient.index(request);
            log.debug("created new index for doc with id {}", response.id());

            return vehicleSearch;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("failed to create index for vehicle {} ", ioe.getMessage());
            throw new VehicleSearchException("Not able to save vehicle search, please contact system administrator.");
        }
    }

    @Override
    public Integer saveAll(List<? extends VehicleSearch> vehicleSearchList) throws VehicleImportException{
        try {
            BulkRequest.Builder reqBuilder = new BulkRequest.Builder();

            for (VehicleSearch vehicleSearch : vehicleSearchList) {
                reqBuilder.operations(op -> op.index(idx -> idx.index(ES_IDX_ENDPOINT)
                                                            .document(vehicleSearch)
                                                            .id(vehicleSearch.getId())
                ));
            }
            BulkResponse result = esClient.bulk(reqBuilder.build());

            if (result.errors()) {
                throw new VehicleImportException(result.items().stream()
                        .map(item -> item.error() != null ? item.error().reason() : null)
                        .toList());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("failed to create bulk indexes {}", ioe.getMessage());
            throw new VehicleSearchException("Not able to save vehicle search, please contact system administrator.");
        }
        return vehicleSearchList.size();
    }

    @Override
    public Integer update(String vehicleId, VehicleSearch partialVehicleSearch) {
        UpdateRequest<VehicleSearch, VehicleSearch> updateRequest =
                new UpdateRequest.Builder<VehicleSearch, VehicleSearch>()
                        .index(ES_IDX_ENDPOINT)
                        .id(vehicleId)
                        .doc(partialVehicleSearch)
                        .build();

        try {
            UpdateResponse<VehicleSearch> response = esClient.update(updateRequest, VehicleSearch.class);
            log.debug("updated doc with id {}", response.id());

            if (response.result() != null
                    && response.result().name().toUpperCase(Locale.ROOT).equals("UPDATED")) {
                return 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("failed to delete doc {} ", e.getMessage());
            throw new VehicleSearchException("Not able to delete vehicle search, please contact system administrator.");
        }

        return 0;
    }

    @Override
    public void delete(String vehicleId) {
        DeleteRequest request = new DeleteRequest.Builder()
                .index(ES_IDX_ENDPOINT)
                .id(vehicleId)
                .build();
        try {
            DeleteResponse response = esClient.delete(request);
            log.debug("deleted doc with id {}", response.id());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("failed to delete doc {} ", e.getMessage());
            throw new VehicleSearchException("Not able to delete vehicle search, please contact system administrator.");
        }
    }

}
