package com.moggot.vkontaktephotoviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Класс адаптера для RecyclerView, для отображения фото на экране превью
 */
public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {

    private List<Bitmap> galleryList;

    public PreviewAdapter(List<Bitmap> galleryList) {
        this.galleryList = galleryList;
    }

    @Override
    public PreviewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PreviewAdapter.ViewHolder viewHolder, int i) {

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
        private PreviewAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final PreviewAdapter.ClickListener clickListener) {
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