package com.moggot.vkontaktephotoviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private List<Bitmap> galleryList;
    private Context context;

    public PhotoAdapter(Context context, List<Bitmap> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoAdapter.ViewHolder viewHolder, int i) {

        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.img.setImageBitmap(galleryList.get(i));
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public ViewHolder(View view) {
            super(view);

            img = (ImageView) view.findViewById(R.id.img);
        }
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private PhotoAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final PhotoAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}