package com.iscool.edward.stockmarkettwitter;

import android.arch.lifecycle.ViewModelProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iscool.edward.stockmarkettwitter.database.ReadingSchema;
import com.iscool.edward.stockmarkettwitter.database.SharedViewModel;
import com.iscool.edward.stockmarkettwitter.database.TopicNameSchema;
import com.iscool.edward.stockmarkettwitter.database.TopicSchema;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.UUID;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicHolder>{
    private String TAG = "com.iscool.edward.stockmarkettwitter";
    ArrayList<Topic>mTopicArrayList;
    Context mContext;
    SqlLite sqlLite;
    TopicAdapter(ArrayList<Topic> arrayList,Context context){
        mContext=context;
        sqlLite = new SqlLite(mContext);
        mTopicArrayList = arrayList;
        mTopicArrayList = sqlLite.displayTopics();
    }

    @Override
    public TopicAdapter.TopicHolder onCreateViewHolder(ViewGroup parent,int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View viewHolder  = layoutInflater.inflate(R.layout.topic_item_view,parent,false);
        return new TopicHolder(viewHolder);//viewholders allow us to bind data to the objects]
    }

    public class TopicHolder extends RecyclerView.ViewHolder{
        Button btn;
        TextView mTextView;
        LinearLayout mLinearLayout;
        public TopicHolder(View itemView){
            super(itemView);
            btn = itemView.findViewById(R.id.topic_btn);
            mTextView = itemView.findViewById(R.id.chillin);
            mLinearLayout = itemView.findViewById(R.id.topicLayout);
        }
        public void bindTopic(final Topic topic){
            btn.setBackground(mContext.getDrawable(topic.imgurl));
            btn.setText(topic.category);
            mTextView.setText(topic.category);
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //actually needs to be the reading fragment
                    QuizActivity qa = ((QuizActivity)mContext);
                    String title;
                    TextView tv = v.findViewById(R.id.chillin);
                    Button bt = v.findViewById(R.id.topic_btn);
                    title = tv.getText().toString();
                    //add a reading
                    Cursor c = sqlLite.queryTopic(TopicSchema.TopicTable.Cols.TITLE + " = ?",new String[]{title});
                    //delegate this to the reading fragment
                    if (c.moveToFirst()) {
                        Log.d(TAG,"retrieve the topic success");
                        String id = c.getString(c.getColumnIndex(TopicSchema.TopicTable.Cols.ID));
                        //find the id of the topic
                        //query the topicName table for a company whose FOREIGNID equals the topic id
                        c = sqlLite.queryTopicName(TopicNameSchema.TopicNameTable.Cols.FOREIGNID + " = ? ",new String[]{id});
                        if (c.moveToFirst()){
                            Log.d(TAG,"retrieve the company name success");
                            String company = c.getString(c.getColumnIndex(TopicNameSchema.TopicNameTable.Cols.COMPANY));
                            String _id = c.getString(c.getColumnIndex(TopicNameSchema.TopicNameTable.Cols.ID));
                            String ticker = c.getString(c.getColumnIndex(TopicNameSchema.TopicNameTable.Cols.TICKER));
                            //remove this entry from the topic Name table
                            boolean remove = sqlLite.removeRow(TopicNameSchema.TopicNameTable.NAME, TopicNameSchema.TopicNameTable.Cols.COMPANY,company);
                            if (remove){Log.d(TAG,"removed company name from topic name table");}
                            //we got the new company, now add it as a reading
                            ContentValues cv = sqlLite.setReadingContentValues(company,1, UUID.fromString(_id),0,ticker);
                            boolean insert = sqlLite.insertRow(ReadingSchema.ReadingTable.NAME,cv);
                            if (insert){Log.d(TAG,"inserted row into reading table");}
                            Article article = new Article(mContext,company,_id);
                            article.populateQuizTable();
                            qa.switch2Read();
                        }
                        else {
                            Log.d(TAG,"can't find a matching company. Add more for the index " + title);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return mTopicArrayList.size();
    }

    @Override
    public void onBindViewHolder(TopicHolder holder, int position){
        //binds view to data
        Topic t = mTopicArrayList.get(position);
        holder.bindTopic(t);
    }
}
