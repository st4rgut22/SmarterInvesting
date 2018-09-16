package com.iscool.edward.stockmarkettwitter;

public class Asset {
    Asset(String company,int shares,String ticker){
        this.company = company;
        this.shares = shares;
        this.ticker = ticker;
    }
    String company;
    int shares;
    String ticker;
}
