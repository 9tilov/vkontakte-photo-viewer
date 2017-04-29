package com.moggot.vkontaktephotoviewer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moggot.vkontaktephotoviewer.observer.DownloadPhotoDisplay;
import com.moggot.vkontaktephotoviewer.observer.PhotoData;
import com.moggot.vkontaktephotoviewer.observer.PhotoDisplay;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoArray;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by toor on 29.04.17.
 */

public class PageViewAdapter extends PagerAdapter {

    private static final String LOG_TAG = PageViewAdapter.class.getSimpleName();

    private ImageCache cache;
    private VKPhotoArray images;
    private Resources res;

    public PageViewAdapter(Resources res, VKPhotoArray images) {
        this.images = images;
        this.cache = ImageCache.getInstance();
        this.res = res;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.image_fullscreen_preview, container, false);

        VKApiPhoto photo = images.get(position);
        DownloadImageTask task = new DownloadImageTask(view);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photo);
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == ((View) obj);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    private class DownloadImageTask extends AsyncTask<VKApiPhoto, Void, Bitmap> {

        private final String LOG_TAG = DownloadImageTask.class.getSimpleName();

        private View view;
        PhotoData photoData;
        VKApiPhoto photo;

        public DownloadImageTask(View view) {
            this.view = view;
            this.photoData = new PhotoData();
        }

        @Override
        protected void onPreExecute() {
            DownloadPhotoDisplay downloadPhotoDisplay = new DownloadPhotoDisplay(photoData);
            downloadPhotoDisplay.display(view);
        }

        protected Bitmap doInBackground(VKApiPhoto... photoInput) {
            String url = photoInput[0].photo_604;
            photo = photoInput[0];

            Bitmap image = null;
            try {
                image = cache.getBitmapFromMemCache(url);
                if (image == null) {
                    InputStream in = new java.net.URL(url).openStream();
                    image = BitmapFactory.decodeStream(in);
                    cache.addBitmapToMemoryCache(url, image);
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            PhotoDisplay photoDisplay = new PhotoDisplay(res, photoData);
            photoData.setPhoto(photo);
            photoDisplay.display(view);

            ((ImageView) view.findViewById(R.id.image_preview)).setImageBitmap(result);
        }
    }

}