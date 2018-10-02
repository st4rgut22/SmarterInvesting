package com.iscool.edward.stockmarkettwitter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iscool.edward.stockmarkettwitter.database.QuizSchema;

import java.util.ArrayList;

public class QuizFragment extends Fragment {
    ArrayList<Quiz>quizList;
    SqlLite mSqlLite;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSqlLite = new SqlLite(getActivity());
        quizList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.quiz_fragment,container,false);
        RecyclerView rc = v.findViewById(R.id.quizList);
        quizList = getQuiz();
        rc.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.Adapter adapter = new QuizAdapter(getActivity(),quizList);
        rc.setAdapter(adapter);
        return v;
    }

    public ArrayList<Quiz> getQuiz(){
        //get reading id, and filter for foreign values here.
        //pass this id from reading fragment to the quiz fragment
        String foreignKey = ((QuizActivity)getActivity()).readUUID;
        QuizCursorWrapper c = mSqlLite.queryQuiz(QuizSchema.QuizTable.Cols.FOREIGNID + "=?",new String[]{foreignKey});
        int number=1;
        if (c.moveToFirst()) {
            do {
                Quiz q = new Quiz(c.getQuizScore());
                q.setQuizId(number++);
                q.id = c.getQuizUUID();
                quizList.add(q);
            }
            while (c.moveToNext());
        }
        else {
            Log.d("quizFragment","Returning an empty quizList. you probably haven't taken any quizzes for the reading yet");
        }
        return quizList;
    }

}
