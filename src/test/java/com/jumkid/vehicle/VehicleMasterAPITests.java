package com.jumkid.vehicle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.repository.VehicleMasterRepository;
import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.mapper.VehicleEngineMapper;
import com.jumkid.vehicle.service.mapper.VehicleMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = { "jwt.token.enable = false" })
@AutoConfigureMockMvc
public class VehicleMasterAPITests {

    @Autowired
    private MockMvc mockMvc;

    private Vehicle vehicle;

    @Autowired
    private VehicleMapper vehicleMapper;

    @MockBean
    private VehicleMasterRepository vehicleRepository;

    @Before
    public void setup() {
        try {
            vehicle = APITestsSetup.buildVehicle();
            VehicleMasterEntity entity = vehicleMapper.dtoToEntity(vehicle);
            when(vehicleRepository.save(any(VehicleMasterEntity.class))).thenReturn(entity);

            when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicleMapper.dtoToEntity(vehicle)));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void whenGivenVehicle_shouldSaveVehicleEntity() throws Exception{
        mockMvc.perform(post("/vehicle")
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
    public void whenGivenNull_shouldGetBadRequest() throws Exception{
        mockMvc.perform(post("/vehicle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenGivenNullProperties_shouldGetBadRequest() throws Exception{
        Vehicle vehicleWithNullProperties = Vehicle.builder().build();
        mockMvc.perform(post("/vehicle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(vehicleWithNullProperties)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.property").isArray());
    }

    @Test
    public void whenGivenVehicleWithId_shouldUpdateVehicleEntity() throws Exception{
        mockMvc.perform(post("/vehicle/" + vehicle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(vehicle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicle.getId()))
                .andExpect(jsonPath("$.name").value(vehicle.getName()))
                .andExpect(jsonPath("$.make").value(vehicle.getMake()))
                .andExpect(jsonPath("$.model").value(vehicle.getModel()))
                .andExpect(jsonPath("$.modelYear").value(vehicle.getModelYear()));
    }

    @Test
    public void whenGivenVehicleId_shouldDeleteVehicleEntity() throws Exception {
        mockMvc.perform(delete("/vehicle/" + vehicle.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
