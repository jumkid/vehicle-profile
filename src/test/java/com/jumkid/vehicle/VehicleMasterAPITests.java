package com.jumkid.vehicle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumkid.share.security.AccessScope;
import com.jumkid.share.service.dto.PagingResults;
import com.jumkid.share.util.DateTimeUtils;
import com.jumkid.vehicle.enums.KeywordMode;
import com.jumkid.vehicle.enums.VehicleField;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.model.VehicleSearch;
import com.jumkid.vehicle.repository.VehicleSearchRepository;
import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.dto.VehicleFieldValuePair;
import com.jumkid.vehicle.service.mapper.VehicleMapper;
import com.jumkid.vehicle.service.mapper.VehicleSearchMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:10092", "port=10092" })
@AutoConfigureMockMvc
@EnableTestContainers
@TestPropertySource("/application.share.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VehicleMasterAPITests {

    @LocalServerPort
    private int port;

    @Value("${com.jumkid.jwt.test.user-token}")
    private String testUserToken;
    @Value("${com.jumkid.jwt.test.user-id}")
    private String testUserId;
    @Autowired
    private VehicleMapper vehicleMapper;
    @Autowired
    private VehicleSearchMapper vehicleSearchMapper;
    @MockBean
    private VehicleSearchRepository vehicleSearchRepository;

    private Vehicle vehicle;
    private List<Vehicle> vehicleList;

    private final String keyword = "test";
    private final Integer size = 10;
    private final Integer page = 1;

    @BeforeAll
    void setup() {
        try {
            RestAssured.defaultParser = Parser.JSON;

            vehicle = TestObjectsBuilder.buildVehicle();
            vehicle.setCreatedBy(testUserId);

            //make sure to use the vehicle id from the testing data file data.sql
            vehicleList  = List.of(TestObjectsBuilder.buildVehicle("abc-123"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Save vehicle")
    void whenGivenVehicle_shouldSaveVehicleEntity() throws Exception{
        when(vehicleSearchRepository.save(any(VehicleSearch.class)))
                .thenReturn(vehicleSearchMapper.entityToSearchMeta(vehicleMapper.dtoToEntity(vehicle)));

        String newVehicleId =
        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsBytes(vehicle))
                .when()
                    .post("/vehicles")
                .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue(),
                            "name", equalTo(vehicle.getName()),
                            "make", equalTo(vehicle.getMake()),
                            "model", equalTo(vehicle.getModel()),
                            "modelYear", equalTo(vehicle.getModelYear()))
                .extract()
                    .path("id");

        vehicle.setId(newVehicleId);
    }

    @Test
    @Order(2)
    @DisplayName("Save null vehicle - Bad Request")
    void whenGivenNull_shouldGetBadRequest() throws Exception{
        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsBytes(null))
                .when()
                    .post("/vehicles")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(3)
    @DisplayName("Save vehicle with null props - Bad Request")
    void whenGivenNullProperties_shouldGetBadRequest() throws Exception{
        Vehicle vehicleWithNullProperties = Vehicle.builder().build();

        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsBytes(vehicleWithNullProperties))
                .when()
                    .post("/vehicles")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(3)
    @DisplayName("Get vehicle by id")
    void whenGivenId_shouldGetVehicle() throws Exception{
        String datetimeStr =
        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                .when()
                    .get("/vehicles/" + vehicle.getId())
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("name", equalTo(vehicle.getName()))
                .extract()
                    .path("modificationDate");

        vehicle.setModificationDate(DateTimeUtils.stringToLocalDatetime(datetimeStr));
    }

    @Test
    @Order(4)
    @DisplayName("Update the newly added vehicle")
    void whenGivenVehicleWithId_shouldUpdateVehicleEntity() throws Exception{
        Vehicle updateVehicle = Vehicle.builder()
                .id(vehicle.getId())
                .make("honda")
                .modificationDate(vehicle.getModificationDate())
                .build();
        VehicleMasterEntity entity = vehicleMapper.dtoToEntity(vehicle);
        entity.setMake(updateVehicle.getMake());

        when(vehicleSearchRepository.update(vehicle.getId(), vehicleSearchMapper.entityToSearchMeta(entity)))
                .thenReturn(1);

        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsBytes(updateVehicle))
                .when()
                    .put("/vehicles/" + vehicle.getId())
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .body("id", equalTo(vehicle.getId()),
                            "name", equalTo(vehicle.getName()),
                            "make", equalTo(updateVehicle.getMake()),
                            "model", equalTo(vehicle.getModel()),
                            "modelYear", equalTo(vehicle.getModelYear()));
    }

    @Test
    @Order(5)
    void whenGivenVehicleId_shouldDeleteVehicleEntity() throws Exception {
        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                .when()
                    .delete("/vehicles/" + vehicle.getId())
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Order(6)
    @DisplayName("Search by keyword as user")
    void whenGivenKeywordAndSizeSearch_shouldGetSearchResultAsUser() throws Exception {
        PagingResults<VehicleSearch> pagingResults = PagingResults.<VehicleSearch>builder()
                .total(10L)
                .page(page)
                .size(size)
                .resultSet(vehicleSearchMapper.dtoListToSearches(vehicleList))
                .build();

        when(vehicleSearchRepository.searchByUser(keyword, size, page, testUserId)).thenReturn(pagingResults);
        when(vehicleSearchRepository.searchByAccessScope(keyword, size, page, AccessScope.PUBLIC)).thenReturn(pagingResults);

        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                    .param("keyword", keyword)
                    .param("keywordMode", KeywordMode.KEYWORD.value())
                    .param("size", size.toString())
                    .param("page", page.toString())
                .when()
                    .get("/vehicles/search")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(Boolean.TRUE),
                            "total", equalTo(size),
                            "page", equalTo(page),
                            "data", hasSize(vehicleList.size()));
    }

    @Test
    @Order(7)
    @DisplayName("Search by keyword as guest")
    void whenGivenKeywordAndSizeSearch_shouldGetSearchResultAsGuest() throws Exception {
        PagingResults<VehicleSearch> pagingResults = PagingResults.<VehicleSearch>builder()
                .total(10L)
                .page(page)
                .size(size)
                .resultSet(vehicleSearchMapper.dtoListToSearches(vehicleList))
                .build();

        when(vehicleSearchRepository.searchByUser(keyword, size, page, testUserId)).thenReturn(pagingResults);
        when(vehicleSearchRepository.searchByAccessScope(keyword, size, page, AccessScope.PUBLIC)).thenReturn(pagingResults);

        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                    .param("keyword", keyword)
                    .param("keywordMode", KeywordMode.KEYWORD.value())
                    .param("size", size.toString())
                    .param("page", page.toString())
                .when()
                    .get("/vehicles/search-public")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(Boolean.TRUE),
                            "total", equalTo(size),
                            "page", equalTo(page),
                            "data", hasSize(vehicleList.size()));
    }

    @Test
    @Order(8)
    void whenGivenFieldsSearchAggregation_shouldGetSearchResult() throws Exception {
        VehicleField field = VehicleField.MAKE;
        List<VehicleFieldValuePair<String>> emptyPairs = Collections.emptyList();
        List<VehicleFieldValuePair<String>> fieldValuePairsPairs =
                List.of(new VehicleFieldValuePair<String>(VehicleField.MAKE, "value"));
        List<String> resultKeys = List.of("key 1", "key 2");

        when(vehicleSearchRepository.searchForAggregation(field, emptyPairs, null)).thenReturn(resultKeys);
        when(vehicleSearchRepository.searchForAggregation(field, fieldValuePairsPairs, null)).thenReturn(resultKeys);

        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                    .queryParam("field", field.value())
                .when()
                    .post("/vehicles/search-aggregation")
                .then()
                    .statusCode(HttpStatus.OK.value());

        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                    .queryParam("field", field.value())
                    .body(new ObjectMapper().writeValueAsBytes(fieldValuePairsPairs))
                .when()
                    .post("/vehicles/search-aggregation")
                .then()
                    .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Search aggregation - Bad Request")
    @Order(9)
    void whenMissFieldsSearchAggregation_shouldGetError() throws Exception {
        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                .when()
                    .post("/vehicles/search-aggregation")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(10)
    @DisplayName("Search matchers")
    void whenGivenFieldsSearchMatchers_shouldGetSearchResult() throws Exception {
        PagingResults<VehicleSearch> pagingResults = PagingResults.<VehicleSearch>builder()
                .total(10L)
                .page(page)
                .size(size)
                .resultSet(vehicleSearchMapper.dtoListToSearches(vehicleList))
                .build();

        List<VehicleFieldValuePair<String>> fieldValuePairsPairs =
                List.of(new VehicleFieldValuePair<String>(VehicleField.MAKE, "value"));

        when(vehicleSearchRepository.searchByMatchFields(size, page, fieldValuePairsPairs, AccessScope.PUBLIC))
                .thenReturn(pagingResults);

        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .contentType(ContentType.JSON)
                    .queryParam("size", size)
                    .queryParam("page", page)
                    .body(new ObjectMapper().writeValueAsBytes(fieldValuePairsPairs))
                .when()
                    .post("/vehicles/search-matchers")
                .then()
                    .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Search matchers - Bad Request")
    @Order(11)
    void whenMissParamFieldsSearchMatchers_shouldGetError() throws Exception {
        Integer size = 20, page = 1;
        List<VehicleFieldValuePair<String>> fieldValuePairsPairs =
                List.of(new VehicleFieldValuePair<String>(VehicleField.MAKE, "value"));

        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .contentType(ContentType.JSON)
                .when()
                .post("/vehicles/search-matchers")
                    .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .contentType(ContentType.JSON)
                    .queryParam("size", size)
                    .queryParam("page", page)
                .when()
                    .post("/vehicles/search-matchers")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());

        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .contentType(ContentType.JSON)
                    .body(new ObjectMapper().writeValueAsBytes(fieldValuePairsPairs))
                .when()
                    .post("/vehicles/search-matchers")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
