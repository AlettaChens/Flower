package com.example.wordchen.uitls;



public class PageUtils {

    public static int getTotalPage(int total,int pageSize){
        int totalPage = (total / pageSize);
        if (total % pageSize != 0) {
            totalPage += 1;
        }
        return totalPage;
    }
}
