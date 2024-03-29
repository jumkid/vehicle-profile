package com.jumkid.vehicle.service;

import com.jumkid.share.security.AccessScope;
import com.jumkid.share.security.exception.InternalRestApiException;
import com.jumkid.share.security.exception.UserProfileNotFoundException;
import com.jumkid.share.service.InternalRestApiClient;
import com.jumkid.share.service.dto.PagingResults;
import com.jumkid.share.user.UserProfile;
import com.jumkid.share.user.UserProfileManager;
import com.jumkid.vehicle.enums.KeywordMode;
import com.jumkid.vehicle.enums.VehicleField;
import com.jumkid.vehicle.exception.VehicleGalleryNoEmptyException;
import com.jumkid.vehicle.exception.VehicleImportException;
import com.jumkid.vehicle.exception.VehicleNotFoundException;
import com.jumkid.vehicle.exception.VehicleSearchException;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.model.VehicleSearch;
import com.jumkid.vehicle.repository.VehicleMasterRepository;
import com.jumkid.vehicle.repository.VehicleSearchRepository;
import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.dto.VehicleFieldValuePair;
import com.jumkid.vehicle.service.dto.external.MediaFile;
import com.jumkid.vehicle.service.handler.DTOHandler;
import com.jumkid.vehicle.service.handler.SmartKeywordHandler;
import com.jumkid.vehicle.service.handler.VehicleMasterImportHandler;
import com.jumkid.vehicle.service.mapper.VehicleMapper;
import com.jumkid.vehicle.service.mapper.VehicleSearchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class VehicleMasterServiceImpl implements VehicleMasterService{

    @Value("${com.jumkid.events.topic.vehicle-create}")
    private String kafkaTopicVehicleCreate;

    @Value("${com.jumkid.events.topic.vehicle-delete}")
    private String kafkaTopicVehicleDelete;

    @Value("${internal.api.content-vault}")
    private String internalApiContentVault;

    @Value("${internal.api.content-vault.clone}")
    private String internalApiContentVaultClone;

    private final VehicleMasterRepository vehicleMasterRepository;

    private final VehicleSearchRepository vehicleSearchRepository;

    private final UserProfileManager userProfileManager;

    private final VehicleMasterImportHandler vehicleMasterImportHandler;
    private final DTOHandler dtoHandler;
    private final SmartKeywordHandler smartKeywordHandler;

    private final KafkaTemplate<String, Vehicle> kafkaTemplate;

    private final VehicleMapper vehicleMapper;
    private final VehicleSearchMapper vehicleSearchMapper;

    private final InternalRestApiClient internalRestApiClient;

    public VehicleMasterServiceImpl(VehicleMasterRepository vehicleMasterRepository,
                                    VehicleSearchRepository vehicleSearchRepository,
                                    UserProfileManager userProfileManager,
                                    VehicleMasterImportHandler vehicleMasterImportHandler,
                                    DTOHandler dtoHandler,
                                    SmartKeywordHandler smartKeywordHandler,
                                    KafkaTemplate<String, Vehicle> kafkaTemplate,
                                    VehicleMapper vehicleMapper,
                                    VehicleSearchMapper vehicleSearchMapper,
                                    InternalRestApiClient internalRestApiClient) {
        this.vehicleMasterRepository = vehicleMasterRepository;
        this.vehicleSearchRepository = vehicleSearchRepository;
        this.userProfileManager = userProfileManager;
        this.vehicleMasterImportHandler = vehicleMasterImportHandler;
        this.dtoHandler = dtoHandler;
        this.smartKeywordHandler = smartKeywordHandler;
        this.kafkaTemplate = kafkaTemplate;
        this.vehicleMapper = vehicleMapper;
        this.vehicleSearchMapper = vehicleSearchMapper;
        this.internalRestApiClient = internalRestApiClient;
    }

    @Override
    public List<Vehicle> getUserVehicles() {
        String userId = getCurrentUserId();

        List<VehicleMasterEntity> vehicleMasterEntities = vehicleMasterRepository.findByCreatedByOrderByCreatedOn(userId);
        log.debug("Found {} vehicles for user {}", vehicleMasterEntities.size(), userId);

        return vehicleMapper.entitiesToDTOs(vehicleMasterEntities);
    }

    @Override
    public PagingResults<Vehicle> searchUserVehicles(final String keyword, final KeywordMode keywordMode, final Integer size, final Integer page)
            throws VehicleSearchException{
        String userId = getCurrentUserId();
        return searchByCriteria(keyword, size, page, (String k, Integer s, Integer p) ->
                vehicleSearchRepository.searchByUser(parseKeyword(k, keywordMode), s, p, userId)
        );
    }

    @Override
    public PagingResults<Vehicle> searchPublicVehicles(final String keyword, final KeywordMode keywordMode, final Integer size, final Integer page)
            throws VehicleSearchException {
        return searchByCriteria(keyword, size, page, (String k, Integer s, Integer p) ->
                vehicleSearchRepository
                        .searchByAccessScope(parseKeyword(k, keywordMode), s, p, AccessScope.PUBLIC)
        );
    }

    @Override
    public PagingResults<Vehicle> searchByCriteria(final String keyword,
                                                   final Integer size,
                                                   final Integer page,
                                                   final SearchByCriteria searchFunction) {
        try {
            PagingResults<VehicleSearch> searchResult = searchFunction.search(keyword, size, page);
            return buildPagingResults(size, page, searchResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new VehicleSearchException(e.getMessage());
        }
    }

    @Override
    public PagingResults<Vehicle> searchByMatchFields(Integer size, Integer page,
                                                      List<VehicleFieldValuePair<String>> matchFields)
            throws VehicleSearchException {
        PagingResults<VehicleSearch> searchResult = vehicleSearchRepository.searchByMatchFields(size, page, matchFields, AccessScope.PUBLIC);
        return buildPagingResults(size, page, searchResult);
    }

    @Override
    public List<String> searchForAggregation(VehicleField field, List<VehicleFieldValuePair<String>> matchFields, Integer size)
            throws VehicleSearchException {
        return vehicleSearchRepository.searchForAggregation(field, matchFields, size);
    }

    private PagingResults<Vehicle> buildPagingResults(Integer size, Integer page,
                                                      PagingResults<VehicleSearch> searchResult) {
        PagingResults<Vehicle> pagingResults = PagingResults.<Vehicle>builder()
                .page(page)
                .size(size)
                .build();

        if (!searchResult.getResultSet().isEmpty()) {
            List<Vehicle> results = searchResult.getResultSet().stream()
                    .map(vehicleSearch -> this.get(vehicleSearch.getId()))
                    .toList();

            pagingResults.setTotal(searchResult.getTotal());
            pagingResults.setResultSet(results);

        } else {
            pagingResults.setTotal(0L);
            pagingResults.setResultSet(Collections.emptyList());

        }
        return pagingResults;
    }

    @Override
    public Vehicle get(String vehicleId) throws VehicleNotFoundException {
        VehicleMasterEntity entity = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> { throw new VehicleNotFoundException(vehicleId); });
        log.debug("Found vehicle entity by id {}", vehicleId);

        return vehicleMapper.entityToDto(entity);
    }

    @Override
    @Transactional
    public Vehicle save(Vehicle vehicle) throws UserProfileNotFoundException{
        dtoHandler.normalizeDTO(null, vehicle, null);

        VehicleMasterEntity entity = vehicleMapper.dtoToEntity(vehicle);

        entity = vehicleMasterRepository.save(entity);
        log.info("new user vehicle data saved with id {}", entity.getId());

        vehicleSearchRepository.save(vehicleSearchMapper.entityToSearchMeta(entity));
        log.debug("new user vehicle search is saved");

        Vehicle newVehicle = vehicleMapper.entityToDto(entity);

        kafkaTemplate.send(kafkaTopicVehicleCreate, newVehicle);

        return newVehicle;
    }

    @Override
    @Transactional
    public Vehicle saveAsNew(Vehicle vehicle) {
        vehicle.setId(null);
        if (vehicle.getVehicleEngine() != null) {
            vehicle.getVehicleEngine().setId(null);
        }
        if (vehicle.getVehicleTransmission() != null) {
            vehicle.getVehicleTransmission().setId(null);
        }
        if (vehicle.getVehiclePricing() != null) {
            vehicle.getVehiclePricing().setId(null);
        }

        Vehicle newVehicle = save(vehicle);

        try {
            // call content service to clone media gallery
            if (newVehicle.getMediaGalleryId() != null && !newVehicle.getMediaGalleryId().isBlank()) {
                MediaFile newGallery = cloneMediaGallery(vehicle.getMediaGalleryId(), null, newVehicle.getName());

                if (newGallery.getUuid() != null) {
                    return update(newVehicle.getId(),
                            Vehicle.builder()
                                    .mediaGalleryId(newGallery.getUuid())
                                    .modificationDate(newVehicle.getModificationDate())
                                    .build());
                }
            }
        } catch (InternalRestApiException e) {
            e.printStackTrace();
            log.error("Failed to call internal api to clone gallery {}", e.getMessage());
            return update(newVehicle.getId(), Vehicle.builder().mediaGalleryId(null).build());
        }

        return newVehicle;
    }

    @Override
    @Transactional
    public Vehicle update(String vehicleId, Vehicle vehicle) {
        VehicleMasterEntity updateEntity = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> { throw new VehicleNotFoundException(vehicleId); });

        dtoHandler.normalizeDTO(vehicleId, vehicle, updateEntity);

        vehicleMapper.updateEntityFromDto(vehicle, updateEntity);

        updateEntity = vehicleMasterRepository.save(updateEntity);
        log.info("updated user vehicle master data");

        Integer count = vehicleSearchRepository.update(vehicleId, vehicleSearchMapper.dtoToSearch(vehicle));
        log.debug("updated user vehicle search index rec {}", count);

        return vehicleMapper.entityToDto(updateEntity);
    }

    @Override
    public String cloneMediaGalleryToVehicle(String vehicleId, String sourceMediaGalleryId) throws VehicleNotFoundException {
        VehicleMasterEntity updateEntity = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> { throw new VehicleNotFoundException(vehicleId); });
        MediaFile targetGallery;
        String toMediaGalleryId = updateEntity.getMediaGalleryId();
        if (toMediaGalleryId != null && !toMediaGalleryId.isBlank()) {
            targetGallery = cloneMediaGallery(sourceMediaGalleryId, toMediaGalleryId, updateEntity.getName());
        } else {
            targetGallery = cloneMediaGallery(sourceMediaGalleryId, null, updateEntity.getName());
            String newGalleryId = targetGallery.getUuid();

            if (newGalleryId != null) {
                update(vehicleId,
                        Vehicle.builder()
                                .mediaGalleryId(newGalleryId)
                                .modificationDate(updateEntity.getModifiedOn())
                                .build());
            }
        }

        return targetGallery.getUuid();
    }

    @Override
    @Transactional
    public void delete(String vehicleId) {
        VehicleMasterEntity existingEntity = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> { throw new VehicleNotFoundException(vehicleId); });

        try {
            vehicleMasterRepository.delete(existingEntity);
            log.info("Vehicle with id {} is removed.", vehicleId);

            vehicleSearchRepository.delete(vehicleId);
            log.debug("Vehicle search metadata is removed");

            kafkaTemplate.send(kafkaTopicVehicleDelete, vehicleMapper.entityToDto(existingEntity));

        } catch (Exception e) {
            e.printStackTrace();
            log.error("failed to delete user vehicle by id {}", vehicleId);
        }

    }

    @Override
    public Integer importVehicleMaster(InputStream is) throws VehicleImportException {
        log.info("importing vehicle master data from input stream");
        return vehicleMasterImportHandler.batchImport(is);
    }

    @Override
    public Integer importVehicleMaster(List<Vehicle> vehicleList) throws VehicleImportException {
        log.info("importing vehicle master data");
        return vehicleMasterImportHandler.batchImport(vehicleList);
    }

    private String getCurrentUserId() {
        UserProfile userProfile = userProfileManager.fetchUserProfile();
        return userProfile.getId();
    }

    private String parseKeyword(String keyword, KeywordMode keywordMode) {
        if (keywordMode == null || keywordMode.equals(KeywordMode.KEYWORD)) {
            return smartKeywordHandler.andSplit(keyword);
        } else {
            return keyword;
        }
    }

    private MediaFile cloneMediaGallery(String sourceMediaGalleryId, String toMediaGalleryId, String title)
            throws VehicleGalleryNoEmptyException{
        URI uri = URI.create(internalApiContentVault + String.format(internalApiContentVaultClone, sourceMediaGalleryId));
        log.debug("calling internal api {}", uri);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", title);
        if (toMediaGalleryId != null) {
            params.add("toMediaGalleryId", toMediaGalleryId);
        }
        try {
            MediaFile targetGallery = internalRestApiClient.post(uri, params, MediaFile.class);
            log.debug("clone to target gallery with uuid {}", targetGallery.getUuid());

            return targetGallery;
        } catch (InternalRestApiException ex) {
            if (ex.getStatusCode().value() == HttpStatus.CONFLICT.value()) {
                throw new VehicleGalleryNoEmptyException();
            }
            log.error("Failed to call internal api to clone gallery {}", ex.getMessage());
            return MediaFile.builder().content(ex.getMessage()).build();
        }
    }

}
