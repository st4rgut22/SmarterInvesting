package com.iscool.edward.stockmarkettwitter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iscool.edward.stockmarkettwitter.database.PlayerSchema;

import java.util.ArrayList;


public class ArticleActivity extends AppCompatActivity{
    private TextView wikiArticle;
    private Button quizMe;
    private Button submitMe;
    private LinearLayout answer;
    private String quiz;
    private String paragraph;
    private ArrayList<String> answerList;
    SqlLite sqlLiteHelper;
    int quizzes;
    Context mContext;
    String title;
    String quizUUID;
    ArrayList<String> userAnswers = new ArrayList<String>();
    private static String quizId = "com.iscool.edward.stockmarkettwitter.uuid";
    private static String quizCount = "com.iscool.edward.stockmarkettwitter.quizCount";
    private static String reading = "com.iscool.edward.stockmarkettwitter.readId";

    public static Intent newIntent(Context mContext,String uuid,String readId,int quizC){
        Intent i = new Intent(mContext,ArticleActivity.class);
        i.putExtra(quizId,uuid);
        i.putExtra(reading,readId);
        i.putExtra(quizCount,quizC);
        return i;
    }

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_article);
        mContext=this;
        wikiArticle = (TextView)findViewById(R.id.wikiArticle);
        sqlLiteHelper = new SqlLite(mContext);
        sqlLiteHelper.getWritableDatabase();
//        article = new Article(sqlLiteHelper,wikiArticle,this,title);
        //returns reading identifier, which we pass to score activity
        quizMe = (Button)findViewById(R.id.takeQuiz);
        submitMe = (Button)findViewById(R.id.submitQuiz);
        answer = (LinearLayout) findViewById(R.id.answerField);
        Intent intent = getIntent();
        quizUUID = intent.getStringExtra(quizId);
        String readUUID = intent.getStringExtra(reading);
        quizzes = intent.getIntExtra(quizCount,0);
        //create a pragraph
        paragraph = Article.getParagraph(sqlLiteHelper,wikiArticle,quizUUID);
        //opening a new database, creates a new database file if it does not already exist
        //prompt user for title

        quizMe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                createAnswerFields();
            }
        });

        submitMe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //record each response
                //compare answers
                storeAnswers();
                float correct = calculateCorrect();
                float score = calculateScore(correct,readUUID,quizUUID);
                //new activity
                Intent intent = ArticleScoreActivity.newIntent(mContext,score,correct,readUUID,quizUUID);
                startActivity(intent);
            }
        });

    }

    public void createAnswerFields(){
        quizMe.setVisibility(View.GONE);
        submitMe.setVisibility(View.VISIBLE);
        quiz = Article.createQuiz(paragraph);
        wikiArticle.setText(Html.fromHtml(quiz,Html.FROM_HTML_MODE_LEGACY));
        answerList = Article.getAnswer(sqlLiteHelper,quizUUID);
        //adding answer fields
        for (int i=0;i<answerList.size();i++){
            EditText editText = new EditText(mContext);
            editText.setText(Integer.toString(i+1));
            answer.addView(editText);
        }
    }

    public void storeAnswers(){
        //populates user answers with answers
        for (int i=0;i<answer.getChildCount();i++){
            if (answer.getChildAt(i) instanceof EditText){
                EditText value = (EditText) answer.getChildAt(i);
                String answer = value.getText().toString();
                userAnswers.add(answer);
            }
        }
    }

    public void restoreAnswers(ArrayList<String>userAnswers) {
        for (int i = 0; i < answer.getChildCount(); i++) {
            if (answer.getChildAt(i) instanceof EditText) {
                EditText value = (EditText) answer.getChildAt(i);
                value.setText(userAnswers.get(i));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        storeAnswers(); //save the answers on rotation
        outState.putStringArrayList("userAnswerStrings",userAnswers);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState){
        userAnswers = inState.getStringArrayList("userAnswerStrings");
        if (userAnswers.size()!=0) {
            createAnswerFields();
            restoreAnswers(userAnswers);
            super.onSaveInstanceState(inState);
        }
    }

    public float calculateCorrect() {
        float total = answerList.size();
        float correct = 0;
        for (int i = 0; i < total; i++) {
            if (answerList.get(i).toLowerCase().equals(userAnswers.get(i).toLowerCase())) {
                correct++;
            }
        }
        return correct;
    }

    public float calculateScore(float correct,String readUUID,String quizUUID){
        //answer list will always be the same as user answer list
        float score;
        float total = answerList.size();
        score = Math.round((correct/total)*100);
        //adds score to quiz, and prevents the quiz from being taken again
        Article.addScore(sqlLiteHelper,quizUUID,readUUID,paragraph,score);
        int readingTotal = Article.incProgress(sqlLiteHelper,readUUID);
        System.out.println("total quizzes " + quizzes);
        System.out.println("readings completed " + readingTotal);
        if (quizzes==readingTotal){
            //if we have finished all the readings decrement topic list
            Cursor c = sqlLiteHelper.allRows(PlayerSchema.PlayerTable.NAME);
            if(c.moveToFirst()) {
                int topicProgress = c.getInt(c.getColumnIndex(PlayerSchema.PlayerTable.Cols.TOPIC));
                topicProgress--;
                ContentValues cv = new ContentValues();
                cv.put("topic",topicProgress);
                sqlLiteHelper.updateRow(PlayerSchema.PlayerTable.NAME,cv,null,null);
            }
        }
        return score;
    }
}
