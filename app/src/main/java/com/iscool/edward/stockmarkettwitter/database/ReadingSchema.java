package com.iscool.edward.stockmarkettwitter.database;

public class ReadingSchema {
    public static final class ReadingTable{
        public static final String NAME = "readingTable";
        public static final class Cols{
            public static final String TITLE = "title";
            public static final String AVAILABLE = "available";
            public static final String UUID = "uuid";
            public static final String PROGRESS = "progress";
            public static final String TICKER = "ticker";
        }
    }
}
