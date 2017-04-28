package com.moggot.vkontaktephotoviewer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoArray;

import java.io.InputStream;
import java.util.ArrayList;

public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private VKPhotoArray images;
    private ViewPager viewPager;
    private int selectedPosition = 0;

    private static final String LOG_TAG = "MyViewPagerAdapter";

    static SlideshowDialogFragment newInstance() {
        return new SlideshowDialogFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);

        images = getArguments().getParcelable("images");
        selectedPosition = getArguments().getInt("position");

        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            Log.v(LOG_TAG, "pos1 = " + position);

            displayMetaInfo(position);

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
//        Image image = images.get(position);
//        lblTitle.setText(image.getName());
//        lblDate.setText(image.getTimestamp());
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    //	adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        ImageCache cache;

        public MyViewPagerAdapter() {
            cache = ImageCache.getInstance();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.image_fullscreen_preview, container, false);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);

            VKApiPhoto photo = images.get(position);

            DownloadImageTask task = new DownloadImageTask(progressBar, imageViewPreview);
            task.execute(photo);
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

            private static final String LOG_TAG = "DownloadImageSetTask";

            private ImageView iw;
            private ProgressBar progressBar;

            public DownloadImageTask(ProgressBar progressBar, ImageView iw) {
                this.iw = iw;
                this.progressBar = progressBar;
            }

            protected Bitmap doInBackground(VKApiPhoto... photo) {
                String url = photo[0].photo_604;
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
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);

            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iw.setImageBitmap(result);
                progressBar.setVisibility(View.GONE);
            }
        }

    }


}
