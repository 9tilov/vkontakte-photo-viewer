package com.moggot.vkontaktephotoviewer;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Класс memory cache.
 * Используется паттерн одиночка, т.к. кэш един для всего приложения
 */
public class ImageCache {

    private LruCache<String, Bitmap> mMemoryCache;

    private static volatile ImageCache instance;

    private ImageCache() {
        init();
    }

    public static ImageCache getInstance() {
        ImageCache localInstance = instance;
        if (localInstance == null) {
            synchronized (ImageCache.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ImageCache();
                }
            }
        }
        return localInstance;
    }

    private void init() {
        //определяем максимальную память, выделенную приложению
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // берем 1/4 часть от доступной памяти
        final int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                //размер кэша измеряется в кб, поэтому делим на 1024
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
