package com.protectapp.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.protectapp.R;

public class ImagePickerHelper implements BottomSheetListener {
    private BottomSheet imagePicker;
    public ImagePickerHelper(Context context)
    {

        imagePicker = new BottomSheet.Builder(context, R.style.MyBottomSheetStyle)
                .setSheet(R.menu.img_picker_menu)
                .setTitle("Pick Image From")
                .setListener(this).create();

    }

    @Override
    public void onSheetShown(@NonNull BottomSheet bottomSheet) {

    }

    @Override
    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.img_picker_camera:

                break;
            case R.id.img_picker_gallery:

                break;
        }

    }

    @Override
    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, int i) {

    }
}
