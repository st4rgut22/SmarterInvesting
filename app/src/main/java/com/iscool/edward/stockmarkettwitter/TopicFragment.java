package com.iscool.edward.stockmarkettwitter;

import android.arch.lifecycle.ViewModelProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iscool.edward.stockmarkettwitter.database.SharedViewModel;
import com.iscool.edward.stockmarkettwitter.database.TopicNameSchema;
import com.iscool.edward.stockmarkettwitter.database.TopicSchema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class TopicFragment extends Fragment {
    //interacts with the model
    ArrayList<Topic> topics;
    SqlLite db;
    public static final String TAG = "com.iscool.edward.stockmarkettwitter.com";
    FragmentManager fm;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        db = new SqlLite(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.topic_fragment,container,false);
        RecyclerView rc = (RecyclerView) v.findViewById(R.id.topicList);
        rc.setLayoutManager(new GridLayoutManager(getActivity(),2));
        RecyclerView.Adapter adapter = new TopicAdapter(topics,getActivity());
        rc.setAdapter(adapter);
        return v;
    }
}
