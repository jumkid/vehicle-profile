package com.jumkid.vehicle.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.jumkid.share.service.dto.PagingResults;
import com.jumkid.vehicle.enums.VehicleField;
import com.jumkid.vehicle.exception.VehicleImportException;
import com.jumkid.vehicle.exception.VehicleSearchException;
import com.jumkid.vehicle.model.VehicleSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class VehicleSearchRepositoryRepositoryImpl implements VehicleSearchRepository {

    private static final String ES_IDX_ENDPOINT = "vehicle";

    private final ElasticsearchClient esClient;

    public VehicleSearchRepositoryRepositoryImpl(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    @Override
    public PagingResults<VehicleSearch> search(String keyword, Integer size, Integer page, String userId) {
        // Search by user id
        Query byUser = MatchQuery.of(m -> m
                .field(VehicleField.CREATEDBY.value())
                .query(userId)
        )._toQuery();

        final int _size = ( size == null ) ? 20 : size;
        final int _page = ( page == null ) ? 0 : page;
        Integer from = _size * (_page - 1);

        try {
            SearchResponse<VehicleSearch> response = esClient.search(builder -> builder
                            .index(ES_IDX_ENDPOINT)
                            .size(_size)
                            .from(from)
                            .q(keyword)
                            .postFilter(byUser),
                    VehicleSearch.class);

            assert response.hits().total() != null;
            final long total = response.hits().total().value();
            log.info("found total {} vehicle profiles for keyword search", response.hits().total().value());

            List<VehicleSearch> results = response.hits().hits().stream().map(Hit::source).collect(Collectors.toList());

            return PagingResults.<VehicleSearch>builder()
                    .total(total)
                    .page(page)
                    .size(size)
                    .resultSet(results)
                    .build();

        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("failed to search vehicle profiles {}", ioe.getMessage());
            throw new VehicleSearchException("failed to search vehicle profiles");
        }
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
            log.error("failed to create index {} ", ioe.getMessage());
            throw new VehicleSearchException("Not able to save vehicle search into Elasticsearch, please contact system administrator.");
        }
    }

    @Override
    public Integer saveAll(List<VehicleSearch> vehicleSearchList) throws VehicleImportException{
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
                        .collect(Collectors.toList()));
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("failed to create bulk indexes {}", ioe.getMessage());
            throw new VehicleSearchException("Not able to save a list of vehicle search, please contact system administrator.");
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
