package com.iscool.edward.stockmarkettwitter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class ArticleActivity extends AppCompatActivity{
    private TextView wikiArticle;
    private Button quizMe;
    private Button submitMe;
    private LinearLayout articleBody;
    private String quiz;
    private String paragraph;
    private ArrayList<String> answerList;
    private ArrayList<String> userAnswers;
    SqlLite sqlLiteHelper;
    Article article;
    Context mContext;
    String title;
    private static String quizId = "com.iscool.edward.stockmarkettwitter.uuid";
    private static String reading = "com.iscool.edward.stockmarkettwitter.readId";

    public static Intent newIntent(Context mContext,String uuid,String readId){
        Intent i = new Intent(mContext,ArticleActivity.class);
        i.putExtra(quizId,uuid);
        i.putExtra(reading,readId);
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
        articleBody = (LinearLayout) findViewById(R.id.articleBody);
        Intent intent = getIntent();
        String quizUUID = intent.getStringExtra(quizId);
        String readUUID = intent.getStringExtra(reading);
        //create a pragraph
        paragraph = Article.getParagraph(sqlLiteHelper,wikiArticle,quizUUID);
        //opening a new database, creates a new database file if it does not already exist
        //prompt user for title

        quizMe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                quizMe.setVisibility(View.GONE);
                submitMe.setVisibility(View.VISIBLE);
                quiz = Article.createQuiz(paragraph);
                wikiArticle.setText(Html.fromHtml(quiz,Html.FROM_HTML_MODE_LEGACY));
                wikiArticle.setTextSize(14);
                answerList = Article.getAnswer(sqlLiteHelper,quizUUID);
                //adding answer fields
                for (int i=0;i<answerList.size();i++){
                    EditText editText = new EditText(mContext);
                    editText.setText(Integer.toString(i+1));
                    articleBody.addView(editText);
                }
            }
        });

        submitMe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                userAnswers = new ArrayList<String>();
                //record each response
                for (int i=0;i<articleBody.getChildCount();i++){
                    if (articleBody.getChildAt(i) instanceof EditText){
                        EditText value = (EditText) articleBody.getChildAt(i);
                        String answer = value.getText().toString();
                        userAnswers.add(answer);
                    }
                }
                float score = calculateScore(readUUID,quizUUID);
                //new activity
                Intent intent = ArticleScoreActivity.newIntent(mContext,score,readUUID,quizUUID);
                startActivity(intent);
            }
        });
    }

    public float calculateScore(String readUUID,String quizUUID){
        //answer list will always be the same as user answer list
        float total = answerList.size();
        float correct = 0;
        float score;
        for (int i=0;i<total;i++){
            if (answerList.get(i).equals(userAnswers.get(i))){
                correct++;
            }
        }
        score = Math.round((correct/total)*100);
        //adds score to quiz, and prevents the quiz from being taken again
        Article.addScore(sqlLiteHelper,quizUUID,readUUID,paragraph,score);
        Article.incProgress(sqlLiteHelper,readUUID);
        return score;
    }
}
