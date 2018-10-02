package com.iscool.edward.stockmarkettwitter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.iscool.edward.stockmarkettwitter.database.PlayerSchema;
import com.iscool.edward.stockmarkettwitter.database.TopicNameSchema;

import java.util.ArrayList;

public class CompanyActivity extends AppCompatActivity{
    //add fragment to framelayout
    //fragment manager, add CompanyFragment to framelayout
    //get list of readings from the constructor
    SqlLite mSqlLite;
    FragmentManager fm = getSupportFragmentManager();
    String foreignId;
    ArrayList<Company> companyList = new ArrayList<>();
    private static String fID = "com.iscool.edward.stockmarkettwitter.foreignId";

    public static Intent newIntent(Context context,String id){
        Intent intent = new Intent(context,CompanyActivity.class);
        //put stuff in here if necessary
        intent.putExtra(fID,id);
        return intent;
    }

    public int getTopicCount(){
        int topicCount=0;
        ContentValues cv = new ContentValues();
        Cursor cc = mSqlLite.allRows(PlayerSchema.PlayerTable.NAME);
        if (cc.moveToFirst()){
            topicCount = cc.getInt(cc.getColumnIndex(PlayerSchema.PlayerTable.Cols.TOPIC));
        }
        return topicCount;
    }

//
//    //added a topic
//    topicCount++;
//            System.out.println("CompanyActivity.java after adding " + topicCount);
//
//            cv.put("topic",topicCount);
//            mSqlLite.updateRow(PlayerSchema.PlayerTable.NAME,cv,null,null);


    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.company_activity);
        Intent i = getIntent();
        foreignId = i.getStringExtra(fID);
        mSqlLite = new SqlLite(this);

        //query the topicName table for COMPANIES whose FOREIGNID equals the topic id
        Cursor c = mSqlLite.queryTopicName(TopicNameSchema.TopicNameTable.Cols.FOREIGNID + " = ? ", new String[]{foreignId});
        //returns to topics if 5 topics or more
        int topicCount = getTopicCount();
        if (topicCount>4){
            Toast.makeText(this,"Please complete your readings before adding more",Toast.LENGTH_SHORT).show();
            finish();
        }
        while (c.moveToNext()) {
                //add company names to the array list
                String company = c.getString(c.getColumnIndex(TopicNameSchema.TopicNameTable.Cols.COMPANY));
                String _id = c.getString(c.getColumnIndex(TopicNameSchema.TopicNameTable.Cols.ID));
                String ticker = c.getString(c.getColumnIndex(TopicNameSchema.TopicNameTable.Cols.TICKER));
                companyList.add(new Company(company,_id,ticker));
        }
        Fragment fragment = fm.findFragmentById(R.id.companyFrame);
        //if activity has been recreated after rotation fragment manager will save the fragment contents
        //so there's no need to add a new one
        if (fragment==null) {
            CompanyFragment companyFragment = new CompanyFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("topicCount",topicCount);
            bundle.putSerializable("companyList",companyList);
            companyFragment.setArguments(bundle);
            fm.beginTransaction()
                    .add(R.id.companyFrame, companyFragment,"company")
                    .commit();

        }
    }
}
