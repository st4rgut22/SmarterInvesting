package com.iscool.edward.stockmarkettwitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private String TAG = "com.iscool.edward.stockmarkettwitter";
    SqlLite myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new SqlLite(this);
        myDb.getWritableDatabase();
//        Intent i = new Intent(this,ArticleActivity.class); //instantiates article, opens up database
//        startActivity(i);
//        second parameter is for reading fragment ONLY ("quiz" or "read" different recycler views)
//        Intent createQuizFragment = QuizActivity.newIntent("read","quiz",this);
//        startActivity(createQuizFragment);
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
}
