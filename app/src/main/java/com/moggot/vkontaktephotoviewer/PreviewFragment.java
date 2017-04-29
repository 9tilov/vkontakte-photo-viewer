package com.moggot.vkontaktephotoviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.vk.sdk.VKUIHelper.getApplicationContext;

public class PreviewFragment extends Fragment {

    private static final String LOG_TAG = PreviewFragment.class.getSimpleName();

    private PhotoAdapter adapter;
    private List<Bitmap> photosBitmap;
    private VKPhotoArray photos;
    private DownloadImageSetTask task;

    public PreviewFragment() {
    }

    public static PreviewFragment newInstance() {
        return new PreviewFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


        photosBitmap = new ArrayList<>();
        adapter = new PhotoAdapter(photosBitmap);

        VKAccessToken token = VKAccessToken.currentToken();
        VKRequest request = new VKRequest("photos.getAll", VKParameters.from(token.userId, "request", "count", "200"), VKPhotoArray.class);
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    photos = (VKPhotoArray) response.parsedModel;
                    task = (DownloadImageSetTask) getActivity().getLastNonConfigurationInstance();
                    if (task == null) {
                        task = new DownloadImageSetTask();
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photos);
                    }
                    task.link(PreviewFragment.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.imageGallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(3, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(new PhotoAdapter.RecyclerTouchListener(getApplicationContext(), new PhotoAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("images", photos);
                bundle.putInt("position", position);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

        }));
    }

    private Object onRetainNonConfigurationInstance() {
        task.unLink();
        return task;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                VKSdk.logout();
                if (VKSdk.isLoggedIn())
                    Log.v(LOG_TAG, "CAN'T logout");
                else
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, LoginFragment.newInstance())
                            .commitAllowingStateLoss();
                return true;

        }
        return onOptionsItemSelected(item);
    }


    private static class DownloadImageSetTask extends AsyncTask<VKPhotoArray, Bitmap, Void> {

        private static final String LOG_TAG = "DownloadImageSetTask";

        private PreviewFragment fragment;

        void link(PreviewFragment fragment) {
            this.fragment = fragment;
        }

        // обнуляем ссылку
        void unLink() {
            fragment = null;
        }

        public DownloadImageSetTask() {
        }

        protected Void doInBackground(VKPhotoArray... photos) {
            for (VKApiPhoto photo : photos[0]) {
                String url = photo.photo_130;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    Bitmap image = BitmapFactory.decodeStream(in);
                    publishProgress(image);
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
            fragment.photosBitmap.add(values[0]);
            fragment.adapter.notifyDataSetChanged();
        }
    }
}
