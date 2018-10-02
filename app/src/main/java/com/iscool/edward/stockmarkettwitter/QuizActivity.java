package com.iscool.edward.stockmarkettwitter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.iscool.edward.stockmarkettwitter.database.SharedViewModel;

public class QuizActivity extends AppCompatActivity implements ReadingAdapter.onReadingClicked {
    private SharedViewModel model;
    //global variables bad??
    TopicFragment topicFragment;
    ReadingFragment readingFragment;
    static String readUUID;
    protected static String readOrTopic = "com.iscool.edward.stockmarkettwitter.readOrTopic";
    protected static String buyOrQuiz = "com.iscool.edward.stockmarkettwitter.buyOrQuiz";
    String first;
    protected FragmentManager fm;

//    @Override
//    public boolean onCreateOptionsMenu(android.view.Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }

    @Override
    public void onBackPressed(){
        fm.popBackStack();
    }

    public void onCreate(Bundle savedInstanceState) {
        //creates new fragment, initially on the reading page
        super.onCreate(savedInstanceState);
        //set view with a single frame layout (a tablet would set a view with multiple layouts)
        setContentView(R.layout.activity_quizbase);
        fm = getSupportFragmentManager();
        //quizbase is the frame layout
        Fragment fragment = fm.findFragmentById(R.id.quizBase);
        //instead of using fragment view, use framelayout to allow you to easily swap details
        if (fragment == null) {
            first = readTopic();
            whichSwitch(first);
        }
    }

    public static Intent newIntent(String first,String second, Context context){
        Intent i = new Intent(context,QuizActivity.class);
        i.putExtra(readOrTopic,first);
        i.putExtra(buyOrQuiz,second); //if its read, then buy or quiz?
        return i;
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
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

//    @Override
//    public void onResume(){
//        //check values of first and showsec and attach/detach accordingl
//        super.onResume();
//        //detach
//        Intent i = getIntent();
//        if (i!=null){
//            //back space
//            String str = readTopic();
//            whichSwitch(str);
//
//        }
//    }

    //get the string s from the activity we are starting quizactivity from
    protected String readTopic(){
        //gets the string "read" "topic" or "quiz" fragment
        Intent i = getIntent();
        String fragName = i.getStringExtra(readOrTopic);
        return fragName;
    }

    @Override
    public void onReadingClicked(String readUUID){
        this.readUUID=readUUID;
    }

    public void whichSwitch(String which){
        if (which.equals("read")){
            switch2Read();
        }
        else if (which.equals("topic")){
            switch2Topic();
        }
        else {
            System.out.println("what you choose? I dont get it");
        }
    }

    public void switch2Quiz(){
        switchFragment(new QuizFragment());
    }

    public void switch2Topic(){
        switchFragment(new TopicFragment());
    }

    public void switch2Read(){
        ReadingFragment read = new ReadingFragment();
        read.setArguments(getIntent().getExtras());
        switchFragment(read);
    }

    protected void switchFragment(Fragment fragment){
        //remove current fragments, add fragment to container
        fm.beginTransaction().replace(R.id.quizBase, fragment).commit();
    }
}
