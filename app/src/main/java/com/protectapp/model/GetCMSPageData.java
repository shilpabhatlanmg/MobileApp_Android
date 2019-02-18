package com.protectapp.model;

import com.google.gson.annotations.SerializedName;

public class GetCMSPageData {
   @SerializedName("PAGE")
   private String page;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
