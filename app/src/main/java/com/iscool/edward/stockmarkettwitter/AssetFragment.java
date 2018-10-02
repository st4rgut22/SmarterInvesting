package com.iscool.edward.stockmarkettwitter;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import static android.view.View.GONE;

public class AssetFragment extends Fragment {
    SqlLite mSqlLite;
    ArrayList<Asset>mAssetArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSqlLite = new SqlLite(getActivity());
        Serializable serialArray = getArguments().getSerializable(((AssetActivity)getActivity()).arrayName);
        mAssetArrayList = (ArrayList)serialArray;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.asset_fragment,container,false);
        RecyclerView rc = v.findViewById(R.id.assetRecyclerView);
        Button buyBtn = v.findViewById(R.id.buyStock);
        rc.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (mAssetArrayList.size()!=0){
            buyBtn.setVisibility(GONE);
            TextView netWorth = (TextView)v.findViewById(R.id.netWorth);
            AssetAdapter adapter = new AssetAdapter(mAssetArrayList,getActivity(),netWorth);
            rc.setAdapter(adapter);
        }
        else {
            TextView msg = v.findViewById(R.id.msg);
            msg.setVisibility(View.VISIBLE);
            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to readings view here to earn more mulah
                    Intent i = QuizActivity.newIntent("read","buy",getActivity());
                    startActivity(i);
                }
            });
        }
        return v;
    }
}
