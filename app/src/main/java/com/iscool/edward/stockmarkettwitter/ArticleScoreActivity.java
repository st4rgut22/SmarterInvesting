package com.iscool.edward.stockmarkettwitter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.iscool.edward.stockmarkettwitter.database.PlayerSchema;


public class ArticleScoreActivity extends AppCompatActivity{
    private static String scoreStr = "com.iscool.edward.stockmarkettwitter.score";
    private static String correctStr = "com.iscool.edward.stockmarkettwitter.correct";
    private static String readStr = "com.iscool.edward.stockmarkettwitter.read";
    private static String quizStr = "com.iscool.edward.stockmarkettwitter.quiz";
    Context context;
    private TextView quizScore;
    private TextView quizMsg;
    private SqlLite db;
    private float correct;
    String quizId;
    String readId;
    private float earn;
    float score;

    public static Intent newIntent(Context context,float score,float correct,String readUUID,String quizUUID){
        Intent i = new Intent(context,ArticleScoreActivity.class);
        i.putExtra(correctStr,correct);
        i.putExtra(scoreStr,score);
        i.putExtra(readStr,readUUID);
        i.putExtra(quizStr,quizUUID);
        return i;
    }

    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_score);
        Intent i = getIntent();
        context = this;
        //get helper
        db = new SqlLite(context);
        db.getWritableDatabase();
        score = i.getExtras().getFloat(scoreStr);
        readId = i.getExtras().getString(readStr);
        quizId = i.getExtras().getString(quizStr);
        correct = i.getExtras().getFloat(correctStr);
        quizScore = (TextView)findViewById(R.id.score);
        quizMsg = (TextView)findViewById(R.id.msg);
        quizScore.setText((int)correct + " Correct");
        if (score==0){
            quizMsg.setText("Better luck next time.");
        }
        else {
            awardPrize();
            quizMsg.setText("Congragulations! You earned "+earn+" dollars.");
        }
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menuAsset:
                Intent i = new Intent(this,AssetActivity.class);
                startActivity(i);
                return true;
            case R.id.menuRead:
                Intent j = QuizActivity.newIntent("read","quiz",this);
                startActivity(j);
                return true;
            case R.id.menuBuyStock:
                Intent k = QuizActivity.newIntent("read","buy",this);
                startActivity(k);
                return true;
            case R.id.menuTopic:
                Intent l = QuizActivity.newIntent("topic","read",this);
                startActivity(l);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void awardPrize(){
        ContentValues cv = new ContentValues();
        Cursor c = db.allRows(PlayerSchema.PlayerTable.NAME);
        if (c.moveToFirst()){
            int mulah = c.getInt(c.getColumnIndex(PlayerSchema.PlayerTable.Cols.MONEY));
            earn = correct*20;
            mulah+=earn;
            cv.put("money",mulah);
            //UPDATES all rows with new money count (only one row returned)
            db.updateRow(PlayerSchema.PlayerTable.NAME,cv,null,null);
        }
        else {
            System.out.println("player not found");
        }
    }
}
