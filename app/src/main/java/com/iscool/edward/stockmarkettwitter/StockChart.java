package com.iscool.edward.stockmarkettwitter;

import android.os.Build;
import android.util.Log;

import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.Interval;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.timeseries.IntraDay;
import org.patriques.output.timeseries.data.StockData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StockChart {
    private static String TAG = "com.iscool.edward";
    private static String apiKey = "B1TV3DJKTJMVUHOC";

    private ArrayList<StockPrice>mStockPrices = new ArrayList<StockPrice>();

    public StockPrice lastMinute(String stockSymbol){
        int timeout = 3000;
        double lastPrice=0;
        int lastTime = 0;
        AlphaVantageConnector apiConnector = new AlphaVantageConnector(apiKey, timeout);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);

        try {
            IntraDay response = stockTimeSeries.intraDay(stockSymbol, Interval.ONE_MIN, OutputSize.COMPACT);
            Map<String, String> metaData = response.getMetaData();
            List<StockData> stockData = response.getStockData();
            lastPrice = stockData.get(stockData.size()-1).getLow();
            lastTime = getTime(stockData.get(stockData.size()-1).getDateTime());
        }
        catch (AlphaVantageException e) {
            System.out.println("something went wrong");
        }
        StockPrice lastStockPrice = new StockPrice(lastPrice,lastTime);
        return lastStockPrice;
    }

    public static double currentPrice(String stockSymbol){
        int timeout = 3000;
        double current=0;
        int dayOfMonth;
        AlphaVantageConnector apiConnector = new AlphaVantageConnector(apiKey, timeout);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);
        try {
            IntraDay response = stockTimeSeries.intraDay(stockSymbol, Interval.ONE_MIN, OutputSize.COMPACT);
            Map<String, String> metaData = response.getMetaData();
            System.out.println("retrieving stock info");
            List<StockData> stockData = response.getStockData();
            current = stockData.get(0).getLow();
        }
        catch (AlphaVantageException e) {
            System.out.println("something went wrong");
        }
        return current;
    }

    public ArrayList<StockPrice> intraDayPrice(String stockSymbol) {
        int timeout = 3000;
        int dayOfMonth;
        AlphaVantageConnector apiConnector = new AlphaVantageConnector(apiKey, timeout);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);

        try {
            IntraDay response = stockTimeSeries.intraDay(stockSymbol, Interval.ONE_MIN, OutputSize.FULL);
            Map<String, String> metaData = response.getMetaData();
            System.out.println("retrieving stock info");
            List<StockData> stockData = response.getStockData();
            dayOfMonth = LocalDateTime.now().getDayOfMonth();
            for (StockData stock:stockData){
                //convert from localDateTime to integer (minutes
                // if )
                if (dayOfMonth!=stock.getDateTime().getDayOfMonth()){
                    break;
                }
                int minutes = getTime(stock.getDateTime());
                double price = stock.getLow();
                StockPrice stockPrice = new StockPrice(price,minutes);
                mStockPrices.add(0,stockPrice);
            }
        } catch (AlphaVantageException e) {
            e.printStackTrace();
        }
        return mStockPrices;
    }

    public static int getTime(LocalDateTime localDateTime){
        //returns the time of the day in minutes
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        return hour*60+minute;

    }
}
