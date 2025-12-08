package com.testing.tests.restassured;

import com.testing.base.BaseTest;
import com.POJOClasses.models.Board;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class boardTestsRestAssured extends BaseTest {
    private String boardId;

    @Test
    public void testCreateBoard() {
        // Build request
        testSteps.buildRequestWithBody("{\"name\":\"Test Board REST Assured\"}");

        // Send request
        testSteps.sendPostRequest("/boards");

        // Validate response
        testSteps.checkResponseIsValid();
        testSteps.checkStatusCode(200);

        // Prepare and check responses
        testSteps.prepareActualResponse();

        Board expected = new Board();
        expected.setName("Test Board REST Assured");
        expected.setDesc("");
        expected.setClosed(false);
        testSteps.prepareExpectedResponse(expected);
        testSteps.checkActualVsExpectedResponses();

        // Store board ID for subsequent tests
        boardId = testSteps.getActualResponse().getId();

        // Essential assertions only
        assertThat(boardId).isNotNull().isNotEmpty();

    }

    @Test(dependsOnMethods = "testCreateBoard")
    public void testGetBoard() {
        testSteps.buildRequest();
        testSteps.sendGetRequest("/boards/" + boardId);

        testSteps.checkResponseIsValid();
        testSteps.checkStatusCode(200);

        testSteps.prepareActualResponse();

        // Verify board details
        assertThat(testSteps.getActualResponse().getId())
                .isEqualTo(boardId);
        assertThat(testSteps.getActualResponse().getName())
                .isEqualTo("Test Board REST Assured");
    }

    @Test(dependsOnMethods = "testGetBoard")
    public void testUpdateBoard() {
        String updatedName = "Updated Board REST Assured";
        String updatedDesc = "Updated description";

        testSteps.buildRequestWithBody(
                String.format("{\"name\":\"%s\",\"desc\":\"%s\",\"closed\":false}",
                        updatedName, updatedDesc));

        testSteps.sendPutRequest("/boards/" + boardId);

        testSteps.checkResponseIsValid();
        testSteps.checkStatusCode(200);

        testSteps.prepareActualResponse();

        Board expected = new Board();
        expected.setName(updatedName);
        expected.setDesc(updatedDesc);
        expected.setClosed(false);
        testSteps.prepareExpectedResponse(expected);
        testSteps.checkActualVsExpectedResponses();
    }

    @Test(dependsOnMethods = "testUpdateBoard")
    public void testDeleteBoard() {
        testSteps.buildRequest();
        testSteps.sendDeleteRequest("/boards/" + boardId);

        testSteps.checkResponseIsValid();
        testSteps.checkStatusCode(200);

        // Instead of checking the response body, it would verify the board is actually deleted
        verifyBoardIsDeleted();
    }

    private void verifyBoardIsDeleted() {
        testSteps.buildRequest();
        testSteps.sendGetRequest("/boards/" + boardId);

        // After deletion, getting the board should return 404 (Not Found)
        testSteps.checkStatusCode(404);
    }
}
