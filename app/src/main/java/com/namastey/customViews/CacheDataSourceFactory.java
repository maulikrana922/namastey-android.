package com.namastey.customViews;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.namastey.R;

import java.io.File;

public class CacheDataSourceFactory implements DataSource.Factory {
    private final Context context;
    private final DefaultDataSourceFactory defaultDatasourceFactory;
    private final long maxFileSize, maxCacheSize;
    private static SimpleCache sDownloadCache;

    public CacheDataSourceFactory(Context context, long maxCacheSize, long maxFileSize) {
        super();
        this.context = context;
        this.maxCacheSize = maxCacheSize;
        this.maxFileSize = maxFileSize;
        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        defaultDatasourceFactory = new DefaultDataSourceFactory(this.context,
                bandwidthMeter,
                new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter));
    }
    public static SimpleCache getInstance(Context context) {
        if (sDownloadCache == null) sDownloadCache = new SimpleCache(new File(context.getCacheDir(), "exoCache"), new NoOpCacheEvictor());
        return sDownloadCache;
    }

    @Override
    public DataSource createDataSource() {
//        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(maxCacheSize);
//        SimpleCache simpleCache = new SimpleCache(new File(context.getCacheDir(), "media"), evictor);
        return new CacheDataSource(getInstance(context), defaultDatasourceFactory.createDataSource(),
                new FileDataSource(), new CacheDataSink(getInstance(context), maxFileSize),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
    }
}