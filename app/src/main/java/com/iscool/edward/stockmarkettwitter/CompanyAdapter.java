package com.iscool.edward.stockmarkettwitter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iscool.edward.stockmarkettwitter.database.PlayerSchema;
import com.iscool.edward.stockmarkettwitter.database.ReadingSchema;
import com.iscool.edward.stockmarkettwitter.database.TopicNameSchema;

import java.util.ArrayList;
import java.util.UUID;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyHolder> {
    //initialize context
    Context mContext;
    ArrayList<Company>companyList;
    SqlLite sqlLite;
    int topicCount;

    CompanyAdapter(Context context,ArrayList<Company> companyList,int topicCount){
        mContext=context;
        this.companyList=companyList;
        sqlLite = new SqlLite(mContext);
        this.topicCount=topicCount;
    }

    @Override
    public CompanyAdapter.CompanyHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewHolder = inflater.inflate(R.layout.company_item_view,parent,false);
        return new CompanyHolder(viewHolder);
    }

    public class CompanyHolder extends RecyclerView.ViewHolder {
        Button companyBtn;

        CompanyHolder(View v) {
            super(v);
            //find view by id
            companyBtn = v.findViewById(R.id.companyBtn);
        }

        public void bindAsset(Company companyName) {
            //bind to data
            String name = companyName.getName();
            String id = companyName.getId();
            String ticker = companyName.getTicker();
            companyBtn.setText(name);
            companyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //remove company
                    //if clicked on the company
                    boolean remove = sqlLite.removeRow(TopicNameSchema.TopicNameTable.NAME, TopicNameSchema.TopicNameTable.Cols.COMPANY, name);
                    if (remove) {
                        ContentValues cv = sqlLite.setReadingContentValues(name, 1, UUID.fromString(id), 0, ticker);
                        boolean insert = sqlLite.insertRow(ReadingSchema.ReadingTable.NAME, cv);
                        ContentValues newcv = new ContentValues();
                        newcv.put("topic",++topicCount);
                        sqlLite.updateRow(PlayerSchema.PlayerTable.NAME,newcv,null,null);
                        Article article = new Article(mContext, name, id);
                        article.populateQuizTable();
                        Intent j = QuizActivity.newIntent("read", "quiz", mContext);
                        mContext.startActivity(j);
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount(){
        return companyList.size();
    }

    @Override
    public void onBindViewHolder(CompanyHolder assetHolder,int position){
        Company companyName = companyList.get(position);
        assetHolder.bindAsset(companyName);
    }

}