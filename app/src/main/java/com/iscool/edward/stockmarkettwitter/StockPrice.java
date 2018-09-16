package com.iscool.edward.stockmarkettwitter;

public class StockPrice {
    public StockPrice(double price, int time) {
        this.price = price;
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public int getTime() {
        return time;
    }

    private double price;
    private int time;

}
