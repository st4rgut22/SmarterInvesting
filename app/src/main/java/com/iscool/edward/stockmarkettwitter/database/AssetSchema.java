package com.iscool.edward.stockmarkettwitter.database;

public class AssetSchema {
    public static final class AssetTable {
        public static final String NAME = "asset";

        public static final class Cols {
            public static final String SHARES = "shares";
            public static final String COMPANY = "company";
            public static final String TICKER = "ticker";
        }
    }
}