package com.iscool.edward.stockmarkettwitter.database;

public class QuizSchema {
    public static final class QuizTable{
        //nested class
        public static final String NAME = "quizTable";
        public static final class Cols{
            public static final String SCORE = "score";
            public static final String PARAGRAPH = "paragraph";
            public static final String FOREIGNID = "foreignId";
            public static final String UUID = "uuid";
            public static final String AVAILABLE = "available";
        }
    }
}
