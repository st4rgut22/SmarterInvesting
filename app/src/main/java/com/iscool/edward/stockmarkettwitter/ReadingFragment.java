package com.iscool.edward.stockmarkettwitter;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.iscool.edward.stockmarkettwitter.database.ReadingSchema;

import java.util.ArrayList;
import java.util.UUID;

public class ReadingFragment extends Fragment {
    public ReadingFragment(){}
    String quizOrBuy;
    ArrayList<Reading>library = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        String key = QuizActivity.buyOrQuiz;
        quizOrBuy = (String) getArguments().getSerializable(QuizActivity.buyOrQuiz);
    }

    public void buildReadList(Cursor c){
        if(c.moveToFirst()) {
            do {
                String title = c.getString(c.getColumnIndex(ReadingSchema.ReadingTable.Cols.TITLE));
                String uuid = c.getString(c.getColumnIndex(ReadingSchema.ReadingTable.Cols.UUID));
                String ticker = c.getString(c.getColumnIndex(ReadingSchema.ReadingTable.Cols.TICKER));
                library.add(new Reading(title, UUID.fromString(uuid), ticker));
            }
            while (c.moveToNext());
        }
        else {
            //add more readings!
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.reading_fragment,container,false);
        Button addRead = v.findViewById(R.id.addReading);
        library = new ArrayList<Reading>();
        SqlLite sqlLite = new SqlLite(getActivity());
        Cursor c = sqlLite.allRows(ReadingSchema.ReadingTable.NAME);
        buildReadList(c);
        addRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((QuizActivity)getActivity()).switch2Topic();
            }
        });
        if (library.size()!=0) {
            //remove the add reading button
            addRead.setVisibility(v.GONE);
            RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.readList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            ReadingAdapter readingAdapter = new ReadingAdapter(library, getActivity(),quizOrBuy);
            recyclerView.setAdapter(readingAdapter);
        }
        return v;
    }
}
