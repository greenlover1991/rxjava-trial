package com.sgo.hotgists.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton utility class for initializing configs for the API
 */
public final class GitApiClient {
  private static final String BASE_API_URL = "https://api.github.com/";
  private static final GitApiClient INSTANCE = new GitApiClient();
  private final GistService service;

  private GitApiClient() {
    // setup custom GSON type adapters
    Gson gson = new GsonBuilder()
        // FIXME adapter for list of filenames

//        .registerTypeAdapter(type, new Gson().getAdapter(TypeToken.get(type)))
        .create();

    // setup retrofit client
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_API_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();

    this.service = retrofit.create(GistService.class);
  }

  public static GistService getGistService() {
    return INSTANCE.service;
  }
}
