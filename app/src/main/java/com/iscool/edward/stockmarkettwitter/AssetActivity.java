package com.iscool.edward.stockmarkettwitter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.iscool.edward.stockmarkettwitter.database.AssetSchema;

import java.util.ArrayList;

public class AssetActivity extends AppCompatActivity {
    final static String shareCountTag = "com.iscool.edward.stockmarkettwitter.shareCount";
    final static String companyName = "com.iscool.edward.stockmarkettwitter.companyName";
    final static String tickerName = "com.iscool.edward.stockmarkettwitter.tickerName";
    final static String arrayName = "com.iscool.edward.stockmarkettwitter.arrayName";


    SqlLite mSqlLite;
    FragmentManager fm = getSupportFragmentManager();

    public static Intent newIntent(Context context,int shareCount,String company,String ticker){
        Intent intent = new Intent(context,AssetActivity.class);
        //optional, put null if you're not buying anything
        intent.putExtra(shareCountTag,shareCount);
        intent.putExtra(companyName,company);
        intent.putExtra(tickerName,ticker);
        return intent;
    }

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        ArrayList<Asset>assetArrayList;
        mSqlLite = new SqlLite(this);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        String company;
        int shares;
        String ticker;
        setContentView(R.layout.activity_asset);
        //EXPERIMENT: initialize asset arraylist inside last if statement, see if fm can recreate recyclerview on rotate
        if (b!=null){
            //buy share button was clicked
            company = i.getStringExtra(companyName);
            shares = i.getIntExtra(shareCountTag,0);
            ticker = i.getStringExtra(tickerName);
            add2Assets(company,shares,ticker);
        }
        assetArrayList = retrieveAssets();
        //inflate the layout
        Fragment fragment = fm.findFragmentById(R.id.assetContainer);
        //if activity has been recreated after rotation fragment manager will save the fragment contents
        //so there's no need to add a new one
        if (fragment==null) {
            AssetFragment assetFragment = new AssetFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(arrayName,assetArrayList);
            assetFragment.setArguments(bundle);
            fm.beginTransaction()
                    .add(R.id.assetContainer, assetFragment, "asset")
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menuAsset:
                return true;
            case R.id.menuRead:
                Intent j = QuizActivity.newIntent("read","quiz",this);
                startActivity(j);
                return true;
            case R.id.menuBuyStock:
                Intent k = QuizActivity.newIntent("read","buy",this);
                startActivity(k);
                return true;
            case R.id.menuTopic:
                Intent l = QuizActivity.newIntent("topic","read",this);
                startActivity(l);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void add2Assets(String company,int shares,String ticker){
        Cursor c = mSqlLite.queryAsset(AssetSchema.AssetTable.Cols.COMPANY + "=?",new String[]{company});
        c.moveToFirst();
        int rowCount = c.getCount();
        if (rowCount==0){
            //insert asset if it doesn't exist
            ContentValues cv = mSqlLite.setAssetContentValues(shares,company,ticker);
            mSqlLite.insertRow(AssetSchema.AssetTable.NAME,cv);
        }
        else
        {
            //update asset
            int currentShareCount = c.getInt(c.getColumnIndex(AssetSchema.AssetTable.Cols.SHARES));
            currentShareCount += shares;
            ContentValues cv = mSqlLite.setAssetContentValues(currentShareCount, company,ticker);
            mSqlLite.updateRow(AssetSchema.AssetTable.NAME, cv, AssetSchema.AssetTable.Cols.COMPANY + "=?", new String[]{company});
        }
    }

    public ArrayList<Asset> retrieveAssets(){
        ArrayList<Asset>aList = new ArrayList<>();
        Cursor allRows = mSqlLite.allRows(AssetSchema.AssetTable.NAME);
        if (allRows.moveToFirst()) {
            do {
                String company = allRows.getString(allRows.getColumnIndex(AssetSchema.AssetTable.Cols.COMPANY));
                int shares = allRows.getInt(allRows.getColumnIndex(AssetSchema.AssetTable.Cols.SHARES));
                String ticker = allRows.getString(allRows.getColumnIndex(AssetSchema.AssetTable.Cols.TICKER));
                aList.add(new Asset(company, shares,ticker));
            }
            while (allRows.moveToNext());
        }
        //i am hobo for now
        return aList;
    }

}
