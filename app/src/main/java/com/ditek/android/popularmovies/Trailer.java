package com.ditek.android.popularmovies;

/**
 * Created by diaa on 3/30/2017.
 */

public class Trailer {
    String key;
    String name;
    String type;
    String thumbnailPath;

    public String getKey() {
        return key;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getType() {
        return type;
    }
}
