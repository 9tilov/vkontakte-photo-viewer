package com.moggot.vkontaktephotoviewer;

import android.animation.IntArrayEvaluator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiPhotos;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKPhotoArray;

import org.json.JSONArray;


public class PreviewFragment extends Fragment {

    private static final String LOG_TAG = "PreviewFragment";

    public PreviewFragment() {
        // Required empty public constructor
    }

    public static PreviewFragment newInstance() {
        return new PreviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
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
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        VKAccessToken token = VKAccessToken.currentToken();
        VKRequest request = new VKRequest("photos.getAll", VKParameters.from(token.userId, "request", "count", "200"), VKPhotoArray.class);
        Log.v(LOG_TAG, "request = " + request.toString());

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    VKPhotoArray photos = (VKPhotoArray) response.parsedModel;
                    new DownloadImageTask(getContext()).execute(photos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
