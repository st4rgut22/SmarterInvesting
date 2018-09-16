package com.iscool.edward.stockmarkettwitter;

import java.io.Serializable;
import java.util.UUID;

public class Reading implements Serializable {
    public Reading(String title, UUID id, String ticker) {
        this.title = title;
        this.id = id;
        this.ticker=ticker;
    }

    String title;
    UUID id;
    String ticker;
}
