package com.protectapp.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.protectapp.R;

public class AppToastBuilder {

    private Context context;
    private String message;
    public AppToastBuilder(Context context)
    {
        this.context=context;
    }
    public AppToastBuilder setMessage(String message)
    {
        this.message=message;
        return this;
    }
    public AppToastBuilder setMessage(int message)
    {
        this.message=context.getString(message);
        return this;
    }

    public Toast build()
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_view,
                null);
        TextView text = layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        return toast;
    }
}
