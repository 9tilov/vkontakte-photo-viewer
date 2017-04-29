package com.moggot.vkontaktephotoviewer.observer;

import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moggot.vkontaktephotoviewer.R;
import com.vk.sdk.api.model.VKApiPhoto;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by toor on 22.02.17.
 */

public class PhotoDisplay implements Observer {

    private static final String LOG_TAG = PhotoDisplay.class.getSimpleName();

    private VKApiPhoto photo;
    private Resources res;

    public PhotoDisplay(Resources res, PhotoData photoData) {
        this.res = res;
        photoData.registerObserver(this);
    }

    @Override
    public void update(VKApiPhoto photo) {
        this.photo = photo;
    }

    public void display(View view) {
        hideProgressBar(view);
        displayDate(view);
        displayLikes(view);
    }

    private void hideProgressBar(View view) {
        ((ProgressBar) view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
    }

    private void displayDate(View view) {
        ((TextView) view.findViewById(R.id.tvAdded)).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.tvDate)).setVisibility(View.VISIBLE);
        Date date = new Date(photo.date);
        SimpleDateFormat df;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            df = new SimpleDateFormat("dd MMM yyyy", res.getConfiguration().getLocales().get(0));
        else
            df = new SimpleDateFormat("dd MMM yyyy", res.getConfiguration().locale);
        String dateText = df.format(date);
        ((TextView) view.findViewById(R.id.tvDate)).setText(dateText);
    }

    private void displayLikes(View view) {
        ((TextView) view.findViewById(R.id.tvLikes)).setVisibility(View.VISIBLE);
        if (photo.likes != 0)
            ((TextView) view.findViewById(R.id.tvLikes)).setText(photo.likes);
    }
}
