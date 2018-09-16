package com.iscool.edward.stockmarkettwitter;

import java.util.ArrayList;

public class Topic {
    public Topic(String category, int imgurl, ArrayList<Reading> list) {
        this.category = category;
        this.imgurl = imgurl;
        this.list = list;
    }
    ArrayList<Reading> list;
    String category;
    int imgurl;

}
