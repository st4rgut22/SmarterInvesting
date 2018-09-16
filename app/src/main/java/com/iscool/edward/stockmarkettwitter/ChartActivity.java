package com.iscool.edward.stockmarkettwitter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


public class ChartActivity extends FragmentActivity {
    FragmentManager fm;
    Fragment firstFrag;
    Fragment secondFrag;
    String first;
    String showSec;
    String readUUID;
    protected static String chartOrBuy = "com.iscool.edward.stockmarkettwitter.chartOrBuy";
    protected static String uuid2 = "com.iscool.edward.stockmarkettwitter.uuid2";

    public static Intent newIntent(String first, String uuid, Context context){
        Intent i = new Intent(context,ChartActivity.class);
        i.putExtra(chartOrBuy,first);
        i.putExtra(uuid2,uuid);
        return i;
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.chartFragment);
        //instead of using fragment view, use framelayout to allow you to easily swap details
        first = chartOrBuy();
        if (fragment == null) {
            String showFirst = chartOrBuy();
            if (showFirst.equals("chart")){
                firstFrag = new ChartFragment();
                secondFrag = new StockShopFragment();
                showSec = "buy";
            }
            else {
                firstFrag = new StockShopFragment();
                secondFrag = new ChartFragment();
                showSec = "chart";
            }
            fm.beginTransaction()
                    //put fragment in frame layout
                    //add both fragments to the fragment manager
                    .add(R.id.chartFragment,secondFrag,showSec)
                    .detach(secondFrag)
                    .add(R.id.chartFragment, firstFrag,showFirst)
                    .commit();
        }

    }

    protected String chartOrBuy(){
        Intent i = getIntent();
        String fragName = i.getStringExtra(chartOrBuy);
        readUUID = i.getStringExtra(uuid2);
        return fragName;
    }

    public void switchChartShop(String oldTag,String newTag){
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction
                .detach(fm.findFragmentByTag(oldTag))
                .attach(fm.findFragmentByTag(newTag))
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

}