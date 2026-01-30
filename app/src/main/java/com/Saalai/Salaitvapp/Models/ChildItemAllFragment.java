package com.Saalai.Salaitvapp.Models;

public class ChildItemAllFragment {

    private String imageUrl;
    private String name;
    private String type;
    private String title;
    private String Url;
    private int channelId; // <-- new field
    private String playedDuration;
    private String channelDuration;// Add this field

    public ChildItemAllFragment(String imageUrl, String name, String type, String title, String url, int channelId, String playedDuration, String channelDuration) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.type = type;
        this.title = title;
        Url = url;
        this.channelId = channelId;
        this.playedDuration = playedDuration;
        this.channelDuration = channelDuration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getPlayedDuration() {
        return playedDuration;
    }

    public void setPlayedDuration(String playedDuration) {
        this.playedDuration = playedDuration;
    }

    public String getChannelDuration() {
        return channelDuration;
    }

    public void setChannelDuration(String channelDuration) {
        this.channelDuration = channelDuration;
    }
}
