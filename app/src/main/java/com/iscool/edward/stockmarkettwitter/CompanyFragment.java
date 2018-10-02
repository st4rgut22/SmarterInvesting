package com.iscool.edward.stockmarkettwitter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

public class CompanyFragment extends Fragment{
    ArrayList<Company>cList;
    int topicCount;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        topicCount = getArguments().getInt("topicCount");
        cList = (ArrayList<Company>)getArguments().getSerializable("companyList");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.company_fragment,container,false);
        RecyclerView rc = v.findViewById(R.id.companyRecycler);
        rc.setLayoutManager(new LinearLayoutManager(getActivity()));
        CompanyAdapter adapter = new CompanyAdapter(getActivity(),cList,topicCount);
        rc.setAdapter(adapter);
        return v;
    }
}
