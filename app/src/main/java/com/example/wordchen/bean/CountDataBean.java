package com.example.wordchen.bean;



public class CountDataBean {
     private String flowername;
     private int  flowercount;

    public CountDataBean(String flowername, int flowercount) {
        this.flowername = flowername;
        this.flowercount = flowercount;
    }

    public int getFlowercount() {
        return flowercount;
    }

    public void setFlowercount(int flowercount) {
        this.flowercount = flowercount;
    }

    public String getFlowername() {
        return flowername;
    }

    public void setFlowername(String flowername) {
        this.flowername = flowername;
    }
}
