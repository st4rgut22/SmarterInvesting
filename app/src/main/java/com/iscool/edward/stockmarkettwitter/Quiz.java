package com.iscool.edward.stockmarkettwitter;

import java.util.UUID;

public class Quiz {
    String id;
    String score;
    int quizId; //what order the quizzes have been taken
    Quiz(String score){
        this.score = score;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
}
