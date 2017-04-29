package com.moggot.vkontaktephotoviewer;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.api.model.VKPhotoArray;

public class SlideshowDialogFragment extends DialogFragment {

    private static final String LOG_TAG = SlideshowDialogFragment.class.getSimpleName();

    private ViewPager viewPager;

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
        View view = inflater.inflate(R.layout.fragment_image_slider, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        VKPhotoArray photos = getArguments().getParcelable("images");
        int selectedPosition = getArguments().getInt("position");

        PageViewAdapter myViewPagerAdapter = new PageViewAdapter(getContext().getResources(), photos);
        viewPager.setAdapter(myViewPagerAdapter);

        setCurrentItem(selectedPosition);

        return view;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
