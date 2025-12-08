package com.testing.utils;

import com.POJOClasses.config.ApiConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.POJOClasses.models.Board;
import org.testng.Assert;

import static org.assertj.core.api.Assertions.assertThat;

public class TestSteps {
    private RequestSpecification request;
    private Response response;
    private Board actualResponse;
    private Board expectedResponse;

    public void buildRequest() {
        RestAssured.baseURI = ApiConfig.getBaseUrl();
        request = RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("key", ApiConfig.getApiKey())
                .queryParam("token", ApiConfig.getApiToken());
    }

    public void buildRequestWithBody(Object body) {
        buildRequest();
        request.body(body);
    }

    public void sendGetRequest(String endpoint) {
        response = request.get(endpoint);
    }

    public void sendPostRequest(String endpoint) {
        response = request.post(endpoint);
    }

    public void sendPutRequest(String endpoint) {
        response = request.put(endpoint);
    }

    public void sendDeleteRequest(String endpoint) {
        response = request.delete(endpoint);
    }

    public void checkResponseIsValid() {
        Assert.assertNotNull(response, "Response should not be null");
        Assert.assertNotNull(response.getBody(), "Response body should not be null");
    }

    public void prepareActualResponse() {
        actualResponse = response.getBody().as(Board.class);
    }

    public void prepareExpectedResponse(Board expected) {
        this.expectedResponse = expected;
    }

    public void checkActualVsExpectedResponses() {
        // TestNG assertions
        Assert.assertEquals(actualResponse.getName(), expectedResponse.getName(), "Board name should match");
        Assert.assertEquals(actualResponse.getDesc(), expectedResponse.getDesc(), "Board description should match");
        Assert.assertEquals(actualResponse.isClosed(), expectedResponse.isClosed(), "Board closed status should match");

        // AssertJ assertions
        assertThat(actualResponse.getName())
                .as("Board name should match expected")
                .isEqualTo(expectedResponse.getName());

        assertThat(actualResponse.getDesc())
                .as("Board description should match expected")
                .isEqualTo(expectedResponse.getDesc());

        assertThat(actualResponse.isClosed())
                .as("Board closed status should match expected")
                .isEqualTo(expectedResponse.isClosed());
    }

    public void checkStatusCode(int expectedStatusCode) {
        // TestNG assertion
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Status code should be " + expectedStatusCode);

        // AssertJ assertion
        assertThat(response.getStatusCode())
                .as("HTTP status code should be " + expectedStatusCode)
                .isEqualTo(expectedStatusCode);
    }

    public Response getResponse() {
        return response;
    }

    public Board getActualResponse() {
        return actualResponse;
    }
}
