package com.iscool.edward.stockmarkettwitter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private String TAG = "com.iscool.edward.stockmarkettwitter";
    SqlLite myDb;
    Context mContext;
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

    public void gameTutorial(View v){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Gameplay");
        alertDialogBuilder
                .setMessage(Html.fromHtml("Welcome my fellow investor! Let's review the the gameplay in Smarter Investing. " +
                        "<br><br> 1) You click 'Add Readings' to choose a company from an industry " +
                        "<br> 2) Click 'Take Quiz' for a fill in the blank quiz on a company " +
                        "<br> 3) Get rewarded for getting questions correct " +
                        "<br> 4) Buy stocks and see your cash and knowledge appreciate! <br>",Html.FROM_HTML_MODE_LEGACY))
                .setNegativeButton("Got it!",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
