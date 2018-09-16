package com.iscool.edward.stockmarkettwitter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public abstract class SingleFragmentActivity extends FragmentActivity{
    //same as activity
    protected static String readOrTopic = "com.iscool.edward.stockmarkettwitter.readOrTopic";
    protected abstract Fragment createTopicFragment();
    protected abstract Fragment createReadingFragment();
    Fragment firstFrag;
    Fragment secondFrag;
    String first;
    String showSec;
//  see nerdranchguide pg 313 for implementing a two pane view for tablets
//    @LayoutRes
//    protected int getLayoutResId(){
//        return R.layout.activity_quizbase;
//    }
// use if else statement to populate different containers
    public static Intent newIntent(String first,Context context){
        Intent i = new Intent(context,QuizActivity.class);
        i.putExtra(readOrTopic,first);
        return i;
    }

    protected FragmentManager fm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //creates new fragment, initially on the reading page
        super.onCreate(savedInstanceState);
        //set view with a single frame layout (a tablet would set a view with multiple layouts)
        setContentView(R.layout.activity_quizbase);
        fm = getSupportFragmentManager();
        //quizbase is the frame layout
        Fragment fragment = fm.findFragmentById(R.id.quizBase);
        //instead of using fragment view, use framelayout to allow you to easily swap details
        first = readOrTopic();
        if (fragment == null) {
            String showFirst = readOrTopic();
            if (showFirst.equals("read")){
                firstFrag = createReadingFragment();
                secondFrag = createTopicFragment();
                showSec = "topic";
            }
            else {
                firstFrag = createTopicFragment();
                secondFrag = createReadingFragment();
                showSec = "read";
            }
            fm.beginTransaction()
                    //put fragment in frame layout
                    //add both fragments to the fragment manager
                    .add(R.id.quizBase,secondFrag,showFirst)
                    .detach(secondFrag)
                    .add(R.id.quizBase, firstFrag,showSec)
                    .commit();
        }
    }
    //starting the activity from another activity
    @Override
    public void onResume(){
        //check values of first and showsec and attach/detach accordingl
        super.onResume();
        String str = readOrTopic();
        //detach
        if (!str.equals(first)){
            fm.beginTransaction()
                    .detach(firstFrag)
                    .attach(secondFrag)
                    .commitAllowingStateLoss();
        }
        //otherwise the attached fragment is correct
    }

    //get the string s from the activity we are starting quizactivity from
    protected String readOrTopic(){
        Intent i = new Intent();
        String fragName = i.getStringExtra(readOrTopic);
        return fragName;
    }
}
