package com.POJOClasses.clients;

import com.POJOClasses.config.ApiConfig;
import com.POJOClasses.models.Board;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public interface TrelloApiClient {

    @POST("boards")
    Call<Board> createBoard(@Body BoardCreateRequest request);

    @GET("boards/{id}")
    Call<Board> getBoard(@Path("id") String id);

    @PUT("boards/{id}")
    Call<Board> updateBoard(@Path("id") String id, @Body BoardUpdateRequest request);

    @DELETE("boards/{id}")
    Call<Board> deleteBoard(@Path("id") String id);

    class BoardCreateRequest {
        public String name;
        public String desc;

        public BoardCreateRequest(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }
    }

    class BoardUpdateRequest {
        public String name;
        public String desc;
        public boolean closed;

        public BoardUpdateRequest(String name, String desc, boolean closed) {
            this.name = name;
            this.desc = desc;
            this.closed = closed;
        }
    }

    static TrelloApiClient create() {
        //Interceptor created to add query parameters
        Interceptor authInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("key", ApiConfig.getApiKey())
                        .addQueryParameter("token", ApiConfig.getApiToken())
                        .build();

                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(authInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.getBaseUrl())
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(TrelloApiClient.class);
    }
}