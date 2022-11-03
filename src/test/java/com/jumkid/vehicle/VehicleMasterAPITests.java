package com.jumkid.vehicle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumkid.share.security.AccessScope;
import com.jumkid.share.service.dto.PagingResults;
import com.jumkid.vehicle.enums.VehicleField;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.model.VehicleSearch;
import com.jumkid.vehicle.repository.VehicleMasterRepository;
import com.jumkid.vehicle.repository.VehicleSearchRepository;
import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.dto.VehicleFieldValuePair;
import com.jumkid.vehicle.service.mapper.VehicleMapper;
import com.jumkid.vehicle.service.mapper.VehicleSearchMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = { "jwt.token.enable = false" })
@AutoConfigureMockMvc
public class VehicleMasterAPITests {

    @Autowired
    private MockMvc mockMvc;

    private Vehicle vehicle;

    private VehicleMasterEntity entity;

    @Autowired
    private VehicleMapper vehicleMapper;
    @Autowired
    private VehicleSearchMapper vehicleSearchMapper;

    @MockBean
    private VehicleMasterRepository vehicleMasterRepository;

    @MockBean
    private VehicleSearchRepository vehicleSearchRepository;

    @Before
    public void setup() {
        try {
            vehicle = APITestsSetup.buildVehicle();

            entity = vehicleMapper.dtoToEntity(vehicle);

            when(vehicleMasterRepository.save(any(VehicleMasterEntity.class))).thenReturn(entity);

            when(vehicleSearchRepository.save(any(VehicleSearch.class)))
                    .thenReturn(vehicleSearchMapper.entityToSearchMeta(entity));

        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @WithMockUser(username="test", password="test", authorities="USER_ROLE")
    public void whenGivenVehicle_shouldSaveVehicleEntity() throws Exception{
        mockMvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(vehicle)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(vehicle.getId()))
                .andExpect(jsonPath("$.name").value(vehicle.getName()))
                .andExpect(jsonPath("$.make").value(vehicle.getMake()))
                .andExpect(jsonPath("$.model").value(vehicle.getModel()))
                .andExpect(jsonPath("$.modelYear").value(vehicle.getModelYear()));
    }

    @Test
    @WithMockUser(username="test", password="test", authorities="USER_ROLE")
    public void whenGivenNull_shouldGetBadRequest() throws Exception{
        mockMvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="test", password="test", authorities="USER_ROLE")
    public void whenGivenNullProperties_shouldGetBadRequest() throws Exception{
        Vehicle vehicleWithNullProperties = Vehicle.builder().build();
        mockMvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(vehicleWithNullProperties)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.property").isArray());
    }

    @Test
    @WithMockUser(username="test", password="test", authorities="USER_ROLE")
    public void whenGivenVehicleWithId_shouldUpdateVehicleEntity() throws Exception{
        Vehicle updateVehicle = Vehicle.builder()
                .id(vehicle.getId())
                .make("honda")
                .modificationDate(vehicle.getModificationDate())
                .build();
        VehicleMasterEntity entity = vehicleMapper.dtoToEntity(vehicle);
        entity.setMake(updateVehicle.getMake());

        when(vehicleMasterRepository.findById(vehicle.getId()))
                .thenReturn(Optional.of(entity));
        when(vehicleMasterRepository.save(any(VehicleMasterEntity.class))).thenReturn(entity);
        when(vehicleSearchRepository.update(vehicle.getId(), vehicleSearchMapper.entityToSearchMeta(entity)))
                .thenReturn(1);

        mockMvc.perform(put("/vehicles/" + vehicle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(updateVehicle)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(vehicle.getId()))
                .andExpect(jsonPath("$.name").value(vehicle.getName()))
                .andExpect(jsonPath("$.make").value(updateVehicle.getMake()))
                .andExpect(jsonPath("$.model").value(vehicle.getModel()))
                .andExpect(jsonPath("$.modelYear").value(vehicle.getModelYear()));
    }

    @Test
    @WithMockUser(username="test", password="test", authorities="USER_ROLE")
    public void whenGivenVehicleId_shouldDeleteVehicleEntity() throws Exception {
        when(vehicleMasterRepository.findById(vehicle.getId())).thenReturn(Optional.of(entity));

        mockMvc.perform(delete("/vehicles/" + vehicle.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username="test", password="test", authorities="USER_ROLE")
    public void whenGivenKeywordAndSizeSearch_shouldGetSearchResult() throws Exception {
        String keyword = "keyword";
        Integer size = 10;
        Integer page = 1;

        List<Vehicle> vehicleList = APITestsSetup.buildVehicles();
        PagingResults<VehicleSearch> pagingResults = PagingResults.<VehicleSearch>builder()
                .total(10L)
                .page(page)
                .size(size)
                .resultSet(vehicleSearchMapper.dtoListToSearches(vehicleList))
                .build();

        when(vehicleSearchRepository.searchByUser(keyword, size, page, "test")).thenReturn(pagingResults);
        when(vehicleSearchRepository.searchByAccessScope(keyword, size, page, AccessScope.PUBLIC)).thenReturn(pagingResults);

        for (Vehicle vehicle : vehicleList) {
            when(vehicleMasterRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicleMapper.dtoToEntity(vehicle)));
        }

        mockMvc.perform(get("/vehicles/search")
                .param("keyword", keyword)
                .param("size", size.toString())
                .param("page", page.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(Boolean.TRUE))
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.data", hasSize(10)));

        mockMvc.perform(get("/vehicles/search-public")
                .param("keyword", keyword)
                .param("size", size.toString())
                .param("page", page.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(Boolean.TRUE))
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.data", hasSize(10)));
    }

    @Test
    @WithMockUser(username="test", password="test", authorities="USER_ROLE")
    public void whenGivenFieldsSearchAggregation_shouldGetSearchResult() throws Exception {
        VehicleField field = VehicleField.MAKE;
        List<VehicleFieldValuePair<String>> emptyPairs = Collections.emptyList();
        List<VehicleFieldValuePair<String>> fieldValuePairsPairs =
                List.of(new VehicleFieldValuePair<String>(VehicleField.MAKE, "value"));
        List<String> resultKeys = List.of("key 1", "key 2");

        when(vehicleSearchRepository.searchForAggregation(field, emptyPairs)).thenReturn(resultKeys);
        when(vehicleSearchRepository.searchForAggregation(field, fieldValuePairsPairs)).thenReturn(resultKeys);

        mockMvc.perform(get("/vehicles/search-aggregation")
                .param("field", field.value())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/vehicles/search-aggregation")
                .param("field", field.value())
                .content(new ObjectMapper().writeValueAsBytes(fieldValuePairsPairs))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="test", password="test", authorities="USER_ROLE")
    public void whenMissFieldsSearchAggregation_shouldGetError() throws Exception {
        mockMvc.perform(get("/vehicles/search-aggregation")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
