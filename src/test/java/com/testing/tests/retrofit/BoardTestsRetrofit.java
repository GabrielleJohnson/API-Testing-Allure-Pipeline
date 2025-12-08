package com.testing.tests.retrofit;

import com.POJOClasses.clients.TrelloApiClient;
import com.testing.base.BaseTest;
import com.POJOClasses.models.Board;
import org.testng.annotations.Test;
import retrofit2.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardTestsRetrofit extends BaseTest {
    private TrelloApiClient client;
    private String boardId;

    @Test
    public void testCreateBoard() {
        client = TrelloApiClient.create();

        TrelloApiClient.BoardCreateRequest request =
                new TrelloApiClient.BoardCreateRequest("Test Board Retrofit", "");

        try {
            Response<Board> response = client.createBoard(request).execute();

            // TestNG assertions
            assert response.body() != null;
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().getName()).isEqualTo("Test Board Retrofit");

            // Store board ID
            boardId = response.body().getId();

            // Additional AssertJ assertions
            assertThat(boardId).isNotNull().isNotEmpty();
            assertThat(response.body().isClosed()).isFalse();

        } catch (Exception e) {
            throw new RuntimeException("API call failed", e);
        }
    }

    @Test(dependsOnMethods = "testCreateBoard")
    public void testGetBoard() {
        try {
            Response<Board> response = client.getBoard(boardId).execute();

            // TestNG assertion
            assert response.body() != null;
            assert response.code() == 200;

            // AssertJ assertions
            assertThat(response.body().getId()).isEqualTo(boardId);
            assertThat(response.body().getName()).isEqualTo("Test Board Retrofit");

        } catch (Exception e) {
            throw new RuntimeException("API call failed", e);
        }
    }


    @Test(dependsOnMethods = "testGetBoard")
    public void testUpdateBoard() {
        TrelloApiClient.BoardUpdateRequest request =
                new TrelloApiClient.BoardUpdateRequest("Updated Board Retrofit",
                        "Updated description", false);

        try {
            Response<Board> response = client.updateBoard(boardId, request).execute();

            assert response.body() != null;
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().getName()).isEqualTo("Updated Board Retrofit");
            assertThat(response.body().getDesc()).isEqualTo("Updated description");

        } catch (Exception e) {
            throw new RuntimeException("API call failed", e);
        }
    }

    @Test(dependsOnMethods = "testUpdateBoard")
    public void testDeleteBoard() {
        try {
            Response<Board> response = client.deleteBoard(boardId).execute();

            assertThat(response.code()).isEqualTo(200);

           // System.out.println("DELETE operation successful!");

        } catch (Exception e) {
            throw new RuntimeException("API call failed", e);
        }
    }
}
