package com.example.lenovo.myapplication;

/**
 * Created by Lenovo on 2016/10/1.
 */

public class Music {
    private long id;
    private long albumId;
    private String album;
    private String displayName;
    private String name;
    private String singer;
    private long size;
    private String url;
    private long duration;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDisplayName() {return displayName;}

    public void setDisplayName(String displayName) {this.displayName = displayName;}

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public long getSize() {return this.size;}

    public void setSize(long size) {this.size = size;}

    public String getUrl() {return url;}

    public void setUrl(String url) {this.url = url;}

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
