package com.Saalai.Salaitvapp.Models;

import java.util.List;

public class PlaylistSection {
    private String sectionName;
    private List<ArtistCategory> artists;

    public PlaylistSection(String sectionName, List<ArtistCategory> artists) {
        this.sectionName = sectionName;
        this.artists = artists;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<ArtistCategory> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistCategory> artists) {
        this.artists = artists;
    }
}