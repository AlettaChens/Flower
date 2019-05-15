package com.example.wordchen.bean;

import java.io.Serializable;



public class FlowerEntity implements Serializable{
    private String flowerId;
    private String flowerName;
    private String flowerInfo;
    private String flowerAvatarUrl;

    public String getFlowerId() {
        return flowerId;
    }

    public void setFlowerId(String flowerId) {
        this.flowerId = flowerId;
    }

    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    public String getFlowerInfo() {
        return flowerInfo;
    }

    public void setFlowerInfo(String flowerInfo) {
        this.flowerInfo = flowerInfo;
    }

    public String getFlowerAvatarUrl() {
        return flowerAvatarUrl;
    }

    public void setFlowerAvatarUrl(String flowerAvatarUrl) {
        this.flowerAvatarUrl = flowerAvatarUrl;
    }
}
