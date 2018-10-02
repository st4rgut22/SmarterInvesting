package com.iscool.edward.stockmarkettwitter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.topic_fragment,container,false);
        RecyclerView rc = (RecyclerView) v.findViewById(R.id.topicList);
        rc.setLayoutManager(new GridLayoutManager(getActivity(),2));
        RecyclerView.Adapter adapter = new TopicAdapter(topics,getActivity());
        rc.setAdapter(adapter);
        return v;
    }
}
