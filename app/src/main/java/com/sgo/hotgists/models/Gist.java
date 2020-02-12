package com.sgo.hotgists.models;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

/**
 * GSON data model for a Gist record
 * We will only parse specific relevant keys
 */
public class Gist implements Serializable {

    private static final long serialVersionUID = 5981319539243466631L;

    @SerializedName("id")
    private String id;
    @SerializedName("url")
    private String url;

    @SerializedName("files")
    private Map<String, Map<String, String>> files;

    private String gistRawUrl;

    @SerializedName("owner")
    private Owner owner;

    // non-JSON fields
    @Expose(deserialize = false, serialize = false)
    private boolean isFavorite;

    @Expose(deserialize = false, serialize = false)
    private int count = 0;

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    /** CSV of filenames*/
    public String getFilenamesAsCsv() {
        return TextUtils.join(", ", files.keySet());
    }

    public String getGistRawUrl() {
        return gistRawUrl;
    }

    public String getOwnerLogin() {
        return owner.login;
    }

    public String getOwnerAvatarUrl() {
        return owner.avatarUrl;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

// FIXME add GSON type adapter
//    private static class Filename {
//        @SerializedName("files")
//        private String filename;
//    }

    private static class Owner implements Serializable {
        private static final long serialVersionUID = -2759281947799108739L;

        @SerializedName("login")
        private String login;
        @SerializedName("avatar_url")
        private String avatarUrl;
    }
}
