package com.iscool.edward.stockmarkettwitter.database;

public class TopicNameSchema {
    public static final class TopicNameTable{
        public static final String NAME = "topicName";
        public static final class Cols{
            public static final String ID = "id";
            public static final String FOREIGNID = "foreignId";
            public static final String COMPANY = "company";
            public static final String TICKER = "ticker";
        }
    }
}
