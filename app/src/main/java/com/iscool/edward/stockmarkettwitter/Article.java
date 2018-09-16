package com.iscool.edward.stockmarkettwitter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.RequiresPermission;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.iscool.edward.stockmarkettwitter.database.AnswerSchema.AnswerTable;
import com.iscool.edward.stockmarkettwitter.database.QuizSchema;
import com.iscool.edward.stockmarkettwitter.database.QuizSchema.QuizTable;
import com.iscool.edward.stockmarkettwitter.database.ReadingSchema;
import com.iscool.edward.stockmarkettwitter.database.ReadingSchema.ReadingTable;

public class Article {
    //create new articles,etc.
    public static final String TAG = "com.iscool.edward.stockmarkettwitter.com";
    public static String PRODUCT_URL;
    private SqlLite db;
    private String paragraph;
    private String title;
    private String quizUUID;
    private String readUUID;
    private Context mContext;


    public String getQuizUUID() {
        return quizUUID;
    }

    public String getReadUUID() {
        return readUUID;
    }

    Article(Context context,String title,String readUUID){
        this.mContext=context;
        this.title = title;
        this.readUUID = readUUID;
        db = new SqlLite(mContext);
        db.getWritableDatabase();
    }

    public static String createQuiz(String quiz){
        quiz = quiz.replaceAll("<a href((?![[0-9]+]).)*?</a>", "_________");
        return quiz;
    }

    public static ArrayList<String> getAnswer(SqlLite sqlLite,String uuid){
        ArrayList<String>aList = new ArrayList<String>();
        Cursor cursor = sqlLite.queryAnswer(AnswerTable.Cols.FOREIGNID + " = ?",
                new String[]{uuid});
        try {
            while (cursor.moveToNext()) {
                String answer = cursor.getString(cursor.getColumnIndex(AnswerTable.Cols.ANSWER));
                aList.add(answer);
            }
        } finally {
            cursor.close();
        }
        return aList;
    }

    public void populateQuizTable(){
        Cursor c = db.queryReading(ReadingTable.Cols.TITLE + "=?",new String[]{title});
        if (c.moveToFirst()) {
            int available = c.getInt(c.getColumnIndex(ReadingTable.Cols.AVAILABLE));
            String ticker = c.getString(c.getColumnIndex(ReadingTable.Cols.TICKER));
            if (available == 1) {
                loadProducts(title, readUUID);
                ContentValues cv = db.setReadingContentValues(title, 0, UUID.fromString(readUUID), 0,ticker);
                //update
                db.updateRow(ReadingTable.NAME, cv, ReadingTable.Cols.TITLE + "=?", new String[]{title});
            }
        }
        else {
            Log.d(TAG,"you have already added quizzes to this reading");
        }
    }

    public static String getParagraph(SqlLite sqlLite,TextView text,String uuid){ //arg UUID readingId
        //select first quiz with uuid equal to readingId
        //called upon first load and subsequent loads
        //Cursor findEntry = db.query("sku_table", columns, "owner=? and price=?", new String[] { owner, price }, null, null, null);
        QuizCursorWrapper cursor = sqlLite.queryQuiz(QuizTable.Cols.UUID + " = ? ", new String[]{uuid});
//        QuizCursorWrapper cursor = db.queryQuiz(QuizTable.Cols.FOREIGNID + "=? and " + QuizTable.Cols.AVAILABLE + "=?", //treats what follows as a string value not code
//                new String[]{uuid,"YES"}); //add another condition
        try {
            cursor.moveToFirst();
            String p = cursor.getQuiz();
            String paragraph = p; //save the linked version to create a quiz
            p = removeLinks(p);
            String quizUUID = cursor.getQuizUUID(); //get the id of this quiz
            Log.d(TAG, "the quiz uuid is " + quizUUID);
            text.setText(Html.fromHtml(p,Html.FROM_HTML_MODE_LEGACY));
            return paragraph;
        } finally {
            cursor.close();
        }
    }

    public static void addScore(SqlLite db,String quizUUID, String readUUID, String paragraph, float score){
        // paragraph, score, quiz uuid, read uuid
        //after adding the score we can safely delete it
        //table stores uuid as string so change it back to uuid
        UUID qUUID = UUID.fromString(quizUUID);
        UUID rUUID = UUID.fromString(readUUID);
        ContentValues quizValue = db.setQuizContentValues(paragraph,score,rUUID,qUUID,0);
        db.updateRow(QuizTable.NAME,quizValue,QuizTable.Cols.UUID + "=?",new String[]{quizUUID});
    }

    public static void incProgress(SqlLite db,String readUUID){
        Cursor c = db.queryReading(ReadingTable.Cols.UUID + " = ?", new String[]{readUUID});
        String ticker="";
        if (c!=null){
            c.moveToFirst();
            ticker = c.getString(c.getColumnIndex(ReadingTable.Cols.TICKER));
            int progress = c.getInt(c.getColumnIndex(ReadingTable.Cols.PROGRESS));
            progress++;
            ContentValues cv = new ContentValues();
            cv.put("progress",progress);
            db.updateRow(ReadingTable.NAME,cv,ReadingTable.Cols.UUID + "=?",new String[]{readUUID});
        }
        else {
            Log.d(TAG,"could not retrieve progress value");
        }
    }

    public static String removeLinks(String quiz){
        return quiz.replaceAll("<a href=.*?>","").replaceAll("</a>","");
    }

    public UUID addQuiz(Matcher m,UUID readId){
        String question="";
        //excludes the links with numbers in brackets, eg [10] while including everything else
        String pRegex = "<a href((?![[0-9]+]).)*?</a>"; //square brackets are treated as literals
        //replace pRegex with a unlinked version and replace it with blank spaces
        Pattern q = Pattern.compile(pRegex);
        boolean hasQuiz=false;
        UUID quizId = null;
        while (m.find()){
            //chance that local variable paragraph and inner class variable have different values, so making it a global variable
            paragraph = m.group(0);
            Pattern p = Pattern.compile(pRegex);
            Matcher qMatcher = q.matcher(paragraph);
            ArrayList<String>qList = new ArrayList<String>();
            while (qMatcher.find()){
                question = qMatcher.group(0);
                //add question
                qList.add(removeLinks(question));
            }
            Log.d(TAG,"The paragraph is " + paragraph);
            //if there are at least 3 links in a paragraph save it to mysqlite database
            if (qList.size()>=3) {
                quizId = UUID.randomUUID();
                ContentValues quizValue = db.setQuizContentValues(paragraph,0,readId,quizId,1);
                db.insertRow(QuizTable.NAME,quizValue);
                //how to display?
                for (int i=0;i<qList.size();i++){
                    ContentValues answerValue = db.setAnswerContentValues(qList.get(i),quizId);
                    db.insertRow(AnswerTable.NAME,answerValue);
                }
            }
        }
        return quizId;
    }

    public void loadProducts(String articleTitle,String uuid){
        //gets json text from mysql server get call, then parses the jsonarray into tee shirt objects
        PRODUCT_URL = "https://en.wikipedia.org/api/rest_v1/page/mobile-html/" + articleTitle;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,PRODUCT_URL,
                //runs on a background thread, you should put everything in on Response
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //gets a new reading
                        boolean success;
                        String readId = uuid;
//                        ContentValues readValue = db.setReadingContentValues(articleTitle,1,readId,0);
//                        db.insertRow(ReadingTable.NAME,readValue);
                        // hold off, we want to display 1 paragraph at a time
//                        article.setText(Html.fromHtml(response,Html.FROM_HTML_MODE_LEGACY));
                        String newReading = "";
                        String regex = "(<p>)(.*)(</p>)";
                        Pattern p = Pattern.compile(regex);
                        Matcher m = p.matcher(response);
                        UUID lastQuiz = addQuiz(m,UUID.fromString(readId));
                        //has at least one quiz
//                        if (lastQuiz==null) {
//                            ContentValues noReading = db.setReadingContentValues(articleTitle,0,readId,0);
//                            db.updateRow(ReadingTable.NAME,noReading,ReadingTable.Cols.UUID + "=?",new String[]{readUUID});
//                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(mContext,error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(mContext).add(stringRequest);
    }
}
