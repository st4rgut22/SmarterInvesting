package com.iscool.edward.stockmarkettwitter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iscool.edward.stockmarkettwitter.database.PlayerSchema;
import com.iscool.edward.stockmarkettwitter.database.ReadingSchema;

public class StockShopFragment extends Fragment {
    SqlLite mSqlLite;
    String readUUID;
    String ticker;
    String title;
    String reading;
    double price=0;
    int money;
    Context context;
    String saveReadUUID = "com.iscool.edward.stockmarkettwitter.readId";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSqlLite = new SqlLite(getActivity());
        mSqlLite.getWritableDatabase();
        context = getActivity();
        if (savedInstanceState!=null){
            readUUID = savedInstanceState.getString(saveReadUUID);
        }
        else {
            readUUID = ((ChartActivity) getActivity()).readUUID;
        }        //EXECUTE AS RUNNABLE
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(saveReadUUID,readUUID);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.stock_shop_fragment,container,false);
        TextView stockName = v.findViewById(R.id.stockName);
        TextView stockPrice = v.findViewById(R.id.stockPrice);
        TextView available = v.findViewById(R.id.availableFund);
        TextView bankFunds = v.findViewById(R.id.bankFunds);
        Button buyStock = v.findViewById(R.id.buyStock);
        EditText enterQuantity = v.findViewById(R.id.quantity);
        Cursor c = mSqlLite.queryReading(ReadingSchema.ReadingTable.Cols.UUID + "=?",new String[]{readUUID});
        Cursor bank = mSqlLite.allRows(PlayerSchema.PlayerTable.NAME);
        bank.moveToFirst();
        int money = bank.getInt(bank.getColumnIndex(PlayerSchema.PlayerTable.Cols.MONEY));
        bankFunds.setText("Cash: $" + money);
        if (c.moveToFirst()) {
            reading = c.getString(c.getColumnIndex(ReadingSchema.ReadingTable.Cols.TITLE));
            ticker = c.getString(c.getColumnIndex(ReadingSchema.ReadingTable.Cols.TICKER));
            stockName.setText(reading);
            System.out.println(reading);
            new Thread(new Runnable() {
                public void run() {
                    price = getStockPrice();
                    stockPrice.post(new Runnable() {
                        //executed on the main thread
                        public void run() {

                            stockPrice.setText("Stock Price: $" + Double.toString(price));
                        }
                    });
                }
            }).start();
        buyStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //what happen if left empty?
                int quantity = Integer.parseInt((enterQuantity.getText()).toString());
                double total = price*quantity;
                if (money<total){
                    available.setVisibility(View.VISIBLE);
                }
                else {
                    ContentValues cv = mSqlLite.setPlayerContentValues(money-(int)total);
                    mSqlLite.updateRow(PlayerSchema.PlayerTable.NAME,cv,null,null);
                    Intent i = AssetActivity.newIntent(context, quantity, reading, ticker);
                    startActivity(i);
                }
            }
        });
        }
        else {
            System.out.println("no reading found with uuid " + readUUID);
        }
        return v;
    }

    double getStockPrice(){
        price = Chart.currentPrice(ticker);
        return price;
    }

}
