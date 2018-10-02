package com.iscool.edward.stockmarkettwitter;

import java.io.Serializable;

public class Asset implements Serializable{
    Asset(String company,int shares,String ticker){
        this.company = company;
        this.shares = shares;
        this.ticker = ticker;
    }
    String company;
    int shares;
    String ticker;
}
