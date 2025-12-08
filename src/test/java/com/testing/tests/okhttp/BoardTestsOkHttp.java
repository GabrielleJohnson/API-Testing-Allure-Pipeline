package com.testing.tests.okhttp;

import com.testing.base.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.POJOClasses.config.ApiConfig;
import com.POJOClasses.models.Board;
import okhttp3.*;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardTestsOkHttp extends BaseTest {
    private OkHttpClient client;
    private ObjectMapper objectMapper;
    private String boardId;

        public BoardTestsOkHttp() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateBoard() throws IOException {
        String json = String.format(
                "{\"name\":\"Test Board OkHttp\",\"key\":\"%s\",\"token\":\"%s\"}",
                ApiConfig.getApiKey(), ApiConfig.getApiToken());

        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(ApiConfig.getBaseUrl() + "/boards")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // TestNG assertion
            assert response.body() != null;
            assert response.code() == 200;

            Board board = objectMapper.readValue(response.body().string(), Board.class);

            // AssertJ assertions
            assertThat(board.getName()).isEqualTo("Test Board OkHttp");
            assertThat(board.isClosed()).isFalse();

            boardId = board.getId();
            assertThat(boardId).isNotNull().isNotEmpty();
        }
    }

    @Test(dependsOnMethods = "testCreateBoard")
    public void testGetBoard() throws IOException {
        String url = String.format("%s/boards/%s?key=%s&token=%s",
                ApiConfig.getBaseUrl(), boardId, ApiConfig.getApiKey(), ApiConfig.getApiToken());

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            assertThat(response.code()).isEqualTo(200);

            Board board = objectMapper.readValue(response.body().string(), Board.class);
            assertThat(board.getId()).isEqualTo(boardId);
            assertThat(board.getName()).isEqualTo("Test Board OkHttp");
        }
    }

    @Test(dependsOnMethods = "testGetBoard")
    public void testUpdateBoard() throws IOException {
        String json = String.format(
                "{\"name\":\"Updated Board OkHttp\",\"desc\":\"OkHttp updated\",\"closed\":false,\"key\":\"%s\",\"token\":\"%s\"}",
                ApiConfig.getApiKey(), ApiConfig.getApiToken());

        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(ApiConfig.getBaseUrl() + "/boards/" + boardId)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            assertThat(response.code()).isEqualTo(200);

            Board board = objectMapper.readValue(response.body().string(), Board.class);
            assertThat(board.getName()).isEqualTo("Updated Board OkHttp");
            assertThat(board.getDesc()).isEqualTo("OkHttp updated");
        }
    }

    @Test(dependsOnMethods = "testUpdateBoard")
    public void testDeleteBoard() throws IOException {
        String url = String.format("%s/boards/%s?key=%s&token=%s",
                ApiConfig.getBaseUrl(), boardId, ApiConfig.getApiKey(), ApiConfig.getApiToken());

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            // First, verify the delete request was successful
            assertThat(response.code()).isEqualTo(200);

            // Then verify the board is actually deleted by trying to get it
            verifyBoardIsDeleted();
        }
    }

    private void verifyBoardIsDeleted() throws IOException {
        String getUrl = String.format("%s/boards/%s?key=%s&token=%s",
                ApiConfig.getBaseUrl(), boardId, ApiConfig.getApiKey(), ApiConfig.getApiToken());

        Request getRequest = new Request.Builder()
                .url(getUrl)
                .get()
                .build();

        try (Response getResponse = client.newCall(getRequest).execute()) {
            // After deletion, getting the board should return 404 or 401
            assertThat(getResponse.code()).isIn(404, 401);
        }
    }
}
