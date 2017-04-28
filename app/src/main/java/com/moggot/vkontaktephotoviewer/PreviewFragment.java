package com.moggot.vkontaktephotoviewer;

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

    private static final String LOG_TAG = "PreviewFragment";

    private PhotoAdapter adapter;
    private List<Bitmap> photosBitmap;
    private VKPhotoArray photos;

    public PreviewFragment() {
        // Required empty public constructor
    }

    public static PreviewFragment newInstance() {
        return new PreviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.imageGallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(3, 1);
        recyclerView.setLayoutManager(layoutManager);

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

        photosBitmap = new ArrayList<>();

        adapter = new PhotoAdapter(getApplicationContext(), photosBitmap);
        recyclerView.setAdapter(adapter);
        view.findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.logout();
                if (VKSdk.isLoggedIn())
                    Log.v(LOG_TAG, "CAN'T logout");
                else
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, LoginFragment.newInstance())
                            .commitAllowingStateLoss();

            }
        });

        VKAccessToken token = VKAccessToken.currentToken();
        VKRequest request = new VKRequest("photos.getAll", VKParameters.from(token.userId, "request", "count", "200"), VKPhotoArray.class);
        Log.v(LOG_TAG, "request = " + request.toString());

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    photos = (VKPhotoArray) response.parsedModel;
                    Bitmap defaultIcon = BitmapFactory.decodeResource(getActivity().getResources(),
                            android.R.color.white);
                    for (int i = 0; i < photos.size(); ++i) {
                        photosBitmap.add(defaultIcon);
                    }
                    new DownloadImageSetTask().execute(photos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class DownloadImageSetTask extends AsyncTask<VKPhotoArray, Bitmap, Void> {

        private static final String LOG_TAG = "DownloadImageSetTask";

        private int index = 0;

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
            photosBitmap.set(index, values[0]);
            ++index;
            adapter.notifyDataSetChanged();
        }
    }
}
