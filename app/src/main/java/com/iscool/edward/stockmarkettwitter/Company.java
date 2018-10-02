package com.iscool.edward.stockmarkettwitter;

import java.io.Serializable;

public class Company implements Serializable{
    String name;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getTicker() {
        return ticker;
    }

    String id;

    public Company(String name, String id, String ticker) {
        this.name = name;
        this.id = id;
        this.ticker = ticker;
    }

    String ticker;

}
