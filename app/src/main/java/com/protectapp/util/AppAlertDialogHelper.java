package com.protectapp.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.protectapp.R;

public class AppAlertDialogHelper {
    private AppAlertDialogHelper()
    {

    }
    public static final void showMessage(Context context,String title,String message)
    {
        new AlertDialog.Builder(context, R.style.AlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("OK",null)

                .create().show();
    }
    public static final void showMessage(Context context,int title,int message)
    {
        new AlertDialog.Builder(context, R.style.AlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("OK",null)

                .create().show();
    }
    public static final void showActionMessage(Context context, int title, int message, DialogInterface.OnClickListener actionListener)
    {
        new AlertDialog.Builder(context, R.style.AlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes",actionListener)
                .setNegativeButton("Cancel",null)

                .create().show();
    }
    public static final void showActionMessage(Context context, String title, String message, DialogInterface.OnClickListener actionListener)
    {
        new AlertDialog.Builder(context, R.style.AlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes",actionListener)
                .setNegativeButton("Cancel",null)

                .create().show();
    }
}
