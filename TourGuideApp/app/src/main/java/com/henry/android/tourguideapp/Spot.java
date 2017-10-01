package com.henry.android.tourguideapp;

/**
 * Created by Henry on 2017/8/16.
 */

public class Spot {
    private String spotName;
    private String spotType;
    private String spotInfo;
    private int imageResource = NO_IMAGE_PROVIDED;

    private static final int NO_IMAGE_PROVIDED = -1;

    public Spot(String spotName, String spotType, String spotInfo) {
        this.spotName = spotName;
        this.spotType = spotType;
        this.spotInfo = spotInfo;
    }

    public Spot(String spotName, String spotType, String spotInfo, int imageResource) {
        this.spotName = spotName;
        this.spotType = spotType;
        this.spotInfo = spotInfo;
        this.imageResource = imageResource;
    }

    public String getSpotName() {
        return spotName;
    }

    public String getSpotType() {
        return spotType;
    }

    public String getSpotInfo() {
        return spotInfo;
    }

    public int getImageResource() {
        return imageResource;
    }


    public boolean hasImage() {
        return imageResource != NO_IMAGE_PROVIDED;
    }

}
