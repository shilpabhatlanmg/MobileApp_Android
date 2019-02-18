package com.protectapp.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class AppLinearLayoutManager extends LinearLayoutManager {
 private boolean isScrollEnabled = true;

 public AppLinearLayoutManager(Context context) {
  super(context);
 }

 public AppLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
  super(context, orientation, reverseLayout);
 }

 public void setScrollEnabled(boolean flag) {
  this.isScrollEnabled = flag;
 }

 @Override
 public boolean canScrollVertically() {
  //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
  return isScrollEnabled && super.canScrollVertically();
 }
}