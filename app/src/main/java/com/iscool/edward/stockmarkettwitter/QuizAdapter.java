package com.iscool.edward.stockmarkettwitter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iscool.edward.stockmarkettwitter.database.QuizSchema;

import java.util.ArrayList;
import java.util.UUID;
import java.util.zip.Inflater;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizHolder>{
    Context mContext;
    String readId;
    String TAG = "com.iscool.edward.stockmarkettwitter";
    ArrayList<Quiz> mQuizArrayList;
    SqlLite mSqlLite;

    QuizAdapter(Context context,ArrayList<Quiz>quizList){
        mContext = context;
        mQuizArrayList=quizList;
        mSqlLite = new SqlLite(mContext);
    }

    @Override
    public QuizAdapter.QuizHolder onCreateViewHolder(ViewGroup parent,int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewHolder = inflater.inflate(R.layout.quiz_item_view,parent,false);
        return new QuizHolder(viewHolder);
    }

    public class QuizHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView score;
        Button quizMe;
        QuizHolder(View v){
            super(v);
            name = v.findViewById(R.id.quizId);
            score = v.findViewById(R.id.quizScore);
            quizMe = v.findViewById(R.id.quizClose);
        }
        public void bindQuiz(Quiz q){
            //bind data to the quiz
            name.setText("Test " + Integer.toString(q.quizId));
            score.setText("Score " + q.score);//get this on another line
            QuizCursorWrapper cw = mSqlLite.queryQuiz(QuizSchema.QuizTable.Cols.UUID + "=?",new String[]{q.id});
            cw.moveToFirst();
            int open = cw.getAvailability();
            if (open==0){
                ((ViewGroup)quizMe.getParent()).removeView(quizMe);
            }
            else {
                quizMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ViewGroup)v.getParent()).removeView(v);
                        ContentValues cv = new ContentValues();
                        cv.put("available",0);
                        mSqlLite.updateRow(QuizSchema.QuizTable.NAME,cv, QuizSchema.QuizTable.Cols.UUID + "=?",new String[]{q.id});
                        System.out.println(q.id);
                        //Cursor c =mSqlLite.rawQuery("UPDATE " + QuizSchema.QuizTable.NAME + " SET AVAILABLE=0 WHERE UUID=" + Integer.toString(q.quizId), null);
                        Intent i = ArticleActivity.newIntent(((QuizActivity) mContext), q.id, QuizActivity.readUUID);
                        mContext.startActivity(i);

                    }
                });
            }
        }
    }

    @Override
    public int getItemCount(){
        return mQuizArrayList.size();
    }

    @Override
    public void onBindViewHolder(QuizHolder quizHolder,int position){
        Quiz q = mQuizArrayList.get(position);
        quizHolder.bindQuiz(q);
    }
}
