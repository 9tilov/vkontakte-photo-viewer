package com.moggot.vkontaktephotoviewer.observer;

import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.moggot.vkontaktephotoviewer.R;
import com.vk.sdk.api.model.VKApiPhoto;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by toor on 29.04.17.
 */

public class DownloadPhotoDisplay implements Observer {

    private static final String LOG_TAG = DownloadPhotoDisplay.class.getSimpleName();

    private VKApiPhoto photo;

    public DownloadPhotoDisplay(PhotoData photoData) {
        photoData.registerObserver(this);
    }

    @Override
    public void update(VKApiPhoto photo) {
        this.photo = photo;
    }

    public void display(View view) {
        displayProgressBar(view);
        hideText(view);
    }

    private void displayProgressBar(View view) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        CubeGrid cubeGrid = new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid);
    }

    private void hideText(View view) {
        ((TextView) view.findViewById(R.id.tvAdded)).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.tvLikes)).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.tvDate)).setVisibility(View.GONE);
    }
}