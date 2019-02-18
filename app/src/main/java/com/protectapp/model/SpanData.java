package com.protectapp.model;

import android.view.View;

/**
 * Created by karan.kalsi on 6/12/2018.
 */

public class SpanData {
    private String string;
    private int color;
    private float relativeSize;
    private boolean isBold=false;
    private View.OnClickListener onClickListener=null;

    public SpanData(String string, int color, float relativeSize,boolean isBold) {
        this.string = string;
        this.color = color;
        this.relativeSize = relativeSize;
        this.isBold=isBold;
    }
    public SpanData(String string, int color, float relativeSize,boolean isBold, View.OnClickListener onClickListener) {
        this.string = string;
        this.color = color;
        this.relativeSize = relativeSize;
        this.isBold=isBold;
        this.onClickListener = onClickListener;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public float getRelativeSize() {
        return relativeSize;
    }

    public void setRelativeSize(float relativeSize) {
        this.relativeSize = relativeSize;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
