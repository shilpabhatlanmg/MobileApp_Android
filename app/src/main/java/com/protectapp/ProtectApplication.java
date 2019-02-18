package com.protectapp;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.protectapp.util.AppSession;

import java.io.File;

public class ProtectApplication extends Application {
private static final int MAX_SIZE= 25 * 1024 * 1024;
    @Override
    public void onCreate() {
        super.onCreate();
        AppSession.getInstance().init(this);
        initImageLoader(this);
    }
    private void initImageLoader(Context context) {
      DisplayImageOptions  imageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        File cacheDir = StorageUtils.getCacheDirectory(context);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(MAX_SIZE))
                .defaultDisplayImageOptions(imageOptions)
                .diskCache(new UnlimitedDiskCache(new File(Environment.getDownloadCacheDirectory() + File.separator + ".karantest")))
//                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }
}
