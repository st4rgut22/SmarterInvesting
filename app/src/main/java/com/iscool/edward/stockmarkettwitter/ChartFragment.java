package com.iscool.edward.stockmarkettwitter;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.iscool.edward.stockmarkettwitter.database.ReadingSchema;

import java.util.ArrayList;
import java.util.List;

public class ChartFragment extends Fragment {
    TextView marketClosed;
    LineChart chart;
    String symbol;
    String readUUID;
    SqlLite mSqlLite;
    String ticker;
    String saveReadUUID = "com.iscool.edward.stockmarkettwitter.readId";
    StockChart stockChart = new StockChart();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSqlLite = new SqlLite(getActivity());
        mSqlLite.getWritableDatabase();
        //screen is rotated and activity is destroyed
        if (savedInstanceState!=null){
            readUUID = savedInstanceState.getString(saveReadUUID);
        }
        else {
            readUUID = ((ChartActivity) getActivity()).readUUID;
        }
        Cursor c = mSqlLite.queryReading(ReadingSchema.ReadingTable.Cols.UUID + "=?",new String[]{readUUID});
        if (c.moveToFirst()) {
            ticker = c.getString(c.getColumnIndex(ReadingSchema.ReadingTable.Cols.TICKER));
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.chart_fragment,container,false);
        marketClosed = (TextView) v.findViewById(R.id.marketClosed);
        chart = (LineChart) v.findViewById(R.id.chart);
        Run run = new Run();
        Thread thread = new Thread(run);
        thread.start();
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(saveReadUUID,readUUID);
    }

    class Run implements Runnable{
        public void run(){
            ArrayList<StockPrice> stockPrice = stockChart.intraDayPrice(ticker); //returns prices
            List<Entry> entries = new ArrayList<Entry>();
            int x = 0;
            for (StockPrice datum: stockPrice){
                entries.add(new Entry(x++,(float)datum.getPrice()));
            }
            if (stockPrice.size()==0){
                //it is the weekend
                marketClosed.post(new Runnable() {
                    @Override
                    public void run() {
                        marketClosed.setVisibility(View.VISIBLE);
                    }
                });
            }
            else {
                int lastTime = stockPrice.get(stockPrice.size() - 1).getTime();
                LineDataSet lineDataSet = new LineDataSet(entries, "Time (Eastern)");
                LineData lineData = new LineData(lineDataSet);
                chart.setData(lineData);
                chart.invalidate();
                chart.getDescription().setEnabled(false);
                MyXAxisValueFormatter timeX = new MyXAxisValueFormatter();
                XAxis xaxis = chart.getXAxis();
                xaxis.setValueFormatter(timeX);
                //add entries if the market hasn't closed yet
                //390 minutes (6 hr 30 min) in a trading day
                int prevTime = 0;
                //supposed to update graph every minute
                while (lastTime < 959) {
                    //wait for a minute before querying again
                    try {
                        Thread.sleep(60000);
                        StockPrice stock = stockChart.lastMinute(ticker);
                        lastTime = stock.getTime();
                        //if last min hasn't changed, then quit. the stock market finished early
                        if (lastTime == prevTime) {
                            break;
                        }
                        prevTime = lastTime;
                        lineDataSet.addEntry(new Entry(x++, (float) stock.getPrice()));
                        lineData.notifyDataChanged();
                        chart.notifyDataSetChanged();
                        chart.invalidate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public class MyXAxisValueFormatter implements IAxisValueFormatter {
        int hour;
        int minute;
        String time;
        int toTime;
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            //value is in minutes conver to eastern time
            //market opens at 9:30 am
            toTime = (int)value;
            toTime += 570;
            hour = toTime/60;
            minute = toTime%60;
            time = hour + ":" + minute;
            return time;
        }
    }

}
