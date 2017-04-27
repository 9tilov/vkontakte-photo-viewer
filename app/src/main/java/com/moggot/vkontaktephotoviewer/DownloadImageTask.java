package com.moggot.vkontaktephotoviewer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.vk.sdk.VKUIHelper.getApplicationContext;

/**
 * Created by toor on 27.04.17.
 */

public class DownloadImageTask extends AsyncTask<VKPhotoArray, Bitmap, Void> {

    private static final String LOG_TAG = "DownloadImageTask";

    private PhotoAdapter adapter;
    private List<Bitmap> photos;

    public DownloadImageTask(Context context) {
        RecyclerView recyclerView = (RecyclerView) ((Activity) context).findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(3, 1);
        recyclerView.setLayoutManager(layoutManager);

        photos = new ArrayList<>();
        adapter = new PhotoAdapter(getApplicationContext(), photos);
        recyclerView.setAdapter(adapter);
    }

    protected Void doInBackground(VKPhotoArray... photos) {
        for (VKApiPhoto photo : photos[0]) {
            String urldisplay = photo.photo_604;
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
//                return mIcon11;
                publishProgress(mIcon11);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        super.onProgressUpdate(values);
        photos.add(values[0]);
        Log.v(LOG_TAG, "size = " + photos.size());
        adapter.notifyDataSetChanged();
    }

//    protected void onPostExecute(Bitmap result) {
//        photos.add(result);
//        Log.v(LOG_TAG, "size = " + photos.size());
//        adapter.notifyDataSetChanged();
//    }
}
