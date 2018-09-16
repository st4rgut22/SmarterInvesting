package com.iscool.edward.stockmarkettwitter;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.iscool.edward.stockmarkettwitter.database.ReadingSchema;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChartFragment extends Fragment {
    LineChart chart;
    TextView mTextView;
    Chart stockChart = new Chart();
    String symbol;
    String readUUID;
    SqlLite mSqlLite;
    String ticker;
    String saveReadUUID = "com.iscool.edward.stockmarkettwitter.readId";

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

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(saveReadUUID,readUUID);
    }

    class Run implements Runnable{
        public void run(){
            //WHERE IS THE SYMBOL OF LIFE?
            ArrayList<StockPrice> stockPrice = stockChart.intraDayPrice(ticker); //returns prices
            List<Entry> entries = new ArrayList<Entry>();
            int x = 0;
            for (StockPrice datum: stockPrice){
                entries.add(new Entry(x++,(float)datum.getPrice()));
            }
            if (stockPrice.size()==0){
                //it is the weekend
                mTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setVisibility(View.VISIBLE);
                    }
                });
            }
            else {
                int lastTime = stockPrice.get(stockPrice.size() - 1).getTime();
                LineDataSet lineDataSet = new LineDataSet(entries, "label");
                lineDataSet.setColor(100);
                LineData lineData = new LineData(lineDataSet);
                chart.setData(lineData);
                chart.invalidate();
                //add entries if the market hasn't closed yet
                //390 minutes (6 hr 30 min) in a trading day
                int prevTime = 0;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.chart_fragment,container,false);
        mTextView = (TextView) v.findViewById(R.id.marketClosed);
        chart = (LineChart) v.findViewById(R.id.chart);
        Run run = new Run();
        Thread thread = new Thread(run);
        thread.start();
        return v;
    }
}
