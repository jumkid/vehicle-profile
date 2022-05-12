package com.jumkid.vehicle.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import com.jumkid.vehicle.exception.VehicleSearchException;
import com.jumkid.vehicle.model.VehicleSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Slf4j
@Repository
public class VehicleSearchRepositoryRepositoryImpl implements VehicleSearchRepository {

    private static final String ES_IDX_ENDPOINT = "vehicle";

    private final ElasticsearchClient esClient;

    public VehicleSearchRepositoryRepositoryImpl(ElasticsearchClient esClient) {
        this.esClient = esClient;
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
            log.info("created new index for doc with id {}", response.id());

            return vehicleSearch;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("failed to create index {} ", ioe.getMessage());
            throw new VehicleSearchException("Not able to save vehicle search into Elasticsearch, please contact system administrator.");
        }
    }

    @Override
    public VehicleSearch update(String vehicleId, VehicleSearch partialVehicleSearch) {
        UpdateRequest<VehicleSearch, VehicleSearch> updateRequest =
                new UpdateRequest.Builder<VehicleSearch, VehicleSearch>()
                        .index(ES_IDX_ENDPOINT)
                        .id(vehicleId)
                        .doc(partialVehicleSearch)
                        .build();

        try {
            UpdateResponse<VehicleSearch> response = esClient.update(updateRequest, VehicleSearch.class);
            log.info("updated doc with id {}", response.id());

            if (response.get() != null) return response.get().source();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("failed to delete doc {} ", e.getMessage());
            throw new VehicleSearchException("Not able to delete vehicle search, please contact system administrator.");
        }

        return null;
    }

    @Override
    public void delete(String vehicleId) {
        DeleteRequest request = new DeleteRequest.Builder()
                .index(ES_IDX_ENDPOINT)
                .id(vehicleId)
                .build();
        try {
            DeleteResponse response = esClient.delete(request);
            log.info("deleted doc with id {}", response.id());

        } catch (IOException e) {
            e.printStackTrace();
            log.error("failed to delete doc {} ", e.getMessage());
            throw new VehicleSearchException("Not able to delete vehicle search, please contact system administrator.");
        }
    }

}
