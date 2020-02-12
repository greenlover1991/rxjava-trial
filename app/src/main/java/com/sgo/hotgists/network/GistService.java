package com.sgo.hotgists.network;

import com.sgo.hotgists.models.Gist;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Contract for our Gists API end points
 */
public interface GistService {

    @GET("gists/public?since")
    Single<List<Gist>> listGistData();

    @GET("users/{username}/gists?since")
    Single<List<Gist>> listUserGistData(@Path("username") String username);
}
