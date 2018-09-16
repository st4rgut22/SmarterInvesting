package com.iscool.edward.stockmarkettwitter;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.iscool.edward.stockmarkettwitter.database.QuizSchema;

public class QuizCursorWrapper extends CursorWrapper {
    public QuizCursorWrapper(Cursor cursor){
        super(cursor);
    }
    public int getAvailability(){
        int available = getInt(getColumnIndex(QuizSchema.QuizTable.Cols.AVAILABLE));
        return available;
    }
    public String getQuiz(){
        String paragraph = getString(getColumnIndex(QuizSchema.QuizTable.Cols.PARAGRAPH));
        return paragraph;
    }
    public String getQuizUUID(){
        String uuid = getString(getColumnIndex(QuizSchema.QuizTable.Cols.UUID));
        return uuid;
    }
    public String getQuizScore(){
        String uuid = getString(getColumnIndex(QuizSchema.QuizTable.Cols.SCORE));
        return uuid;
    }
}
