package com.iscool.edward.stockmarkettwitter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iscool.edward.stockmarkettwitter.database.ReadingSchema;

import java.util.ArrayList;

public class ReadingAdapter extends RecyclerView.Adapter<ReadingAdapter.ReadingHolder>{
    ArrayList<Reading>mReadingArrayList;
    Context mContext;
    onReadingClicked mCallback;
    SqlLite mSqlLite;
    String bindType;


    ReadingAdapter(ArrayList<Reading>readingArrayList,Context context,String read){
        mReadingArrayList = readingArrayList;
        mContext = context;
        mSqlLite = new SqlLite(mContext);
        mCallback = (onReadingClicked)((QuizActivity)mContext);
        bindType=read;
    }

    @Override
    public ReadingAdapter.ReadingHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View viewHolder = layoutInflater.inflate(R.layout.reading_item_view,parent,false);
        return new ReadingHolder(viewHolder);
    }

    public class ReadingHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        Button priceRead;
        Button chartDifficult;
        LinearLayout mLinearLayout;
        ReadingHolder(View v){
            super(v);
            mTextView = (TextView) v.findViewById(R.id.readTitle);
            mLinearLayout = v.findViewById(R.id.readList);
            priceRead = v.findViewById(R.id.priceRead);
            chartDifficult = v.findViewById(R.id.chartDifficulty);
        }

        public void bindQuizReading(Reading reading){
            //we bind the view to the data object
            mTextView.setText(reading.title);
            priceRead.setText("Read");
            chartDifficult.setVisibility(View.GONE);
            priceRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = reading.title;
                    Cursor c = mSqlLite.queryReading(ReadingSchema.ReadingTable.Cols.TITLE + "=?",new String[]{str});
                    if (c.moveToFirst()) {
                        //get uuid to retrieve quizzes
                        String uuid = c.getString(c.getColumnIndex(ReadingSchema.ReadingTable.Cols.UUID));
                        mCallback.onReadingClicked(uuid);
                        //detach reading fragment, add new quiz fragment
                        ((QuizActivity) mContext).switch2Quiz();
                    }
                    else {
                        Log.d("ReadingAdapter","cant find the reading in reading database " + reading.title);
                    }
                }
            });
        }

        public void bindStockReading(Reading reading){
            //we bind the view to the data object
            mTextView.setText(reading.title);
            priceRead.setText("Buy");
            chartDifficult.setText("Chart");
            if (Build.VERSION.SDK_INT<26){
                //localDateTime requires API 26
                chartDifficult.setVisibility(View.GONE);
            }
            String str = reading.title;
            Cursor c = mSqlLite.queryReading(ReadingSchema.ReadingTable.Cols.TITLE + "=?",new String[]{str});
            String uuid;
            if (c.moveToFirst()) {
                uuid = c.getString(c.getColumnIndex(ReadingSchema.ReadingTable.Cols.UUID));
                chartDifficult.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent seeChart = ChartActivity.newIntent("chart",uuid,(QuizActivity)mContext);
                        mContext.startActivity(seeChart);
                    }
                });
                priceRead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent buyStock = ChartActivity.newIntent("buy",uuid,(QuizActivity)mContext);
                        mContext.startActivity(buyStock);
                    }
                });
            }
            else {
                Log.d("ReadingAdapter","cant find the reading in reading database " + reading.title);
            }
        }
    }

    @Override
    public int getItemCount(){
        return mReadingArrayList.size();
    }

    @Override
    public void onBindViewHolder(ReadingHolder readingHolder,int position){
        Reading read = mReadingArrayList.get(position);
        if (bindType.equals("quiz")) {
            readingHolder.bindQuizReading(read);
        }
        else {
            readingHolder.bindStockReading(read);
        }
    }

    public interface onReadingClicked{
        void onReadingClicked(String id);
    }
}
