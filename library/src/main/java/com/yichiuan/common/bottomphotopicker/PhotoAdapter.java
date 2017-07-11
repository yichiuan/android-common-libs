package com.yichiuan.common.bottomphotopicker;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yichiuan.common.R;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>
        implements View.OnClickListener {

    private LayoutInflater inflater;

    private Cursor cursor;

    private SelectListener selectListener;

    public PhotoAdapter(Context context, Cursor cursor) {
        inflater = LayoutInflater.from(context);
        this.cursor = cursor;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = inflater.inflate(R.layout.item_photo_cell, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        if (!isCursorValid(cursor)) {
            throw new IllegalStateException("Cannot bind view holder when cursor is in invalid state.");
        }

        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position
                    + " when trying to bind view holder");
        }

        holder.checkView.setTag(position);
        holder.checkView.setOnClickListener(this);

        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
        String src = cursor.getString(index);

        Context context = inflater.getContext();

        Glide.with(context).load(src)
                .apply(placeholderOf(android.R.drawable.screen_background_dark))
                .into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        if (isCursorValid(cursor)) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return;
        }

        if (newCursor != null) {
            cursor = newCursor;
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
            cursor = null;
        }
    }

    private static boolean isCursorValid(Cursor cursor) {
        return cursor != null && !cursor.isClosed();
    }

    @Override
    public void onClick(View view) {
        CheckView checkView = (CheckView) view;

        int position = (int)checkView.getTag();

        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position
                    + " when trying to bind view holder");
        }
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID));
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        if (checkView.checked()) {
            checkView.setCheck(false);
            selectListener.onDeselected(uri);
        } else {
            checkView.setCheck(true);
            selectListener.onSelected(uri);
        }
    }

    public void setSelectListener(SelectListener selectListener) {
        this.selectListener = selectListener;
    }

    interface SelectListener {
        void onSelected(Uri uri);
        void onDeselected(Uri uri);
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoView;
        CheckView checkView;

        PhotoViewHolder(View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.imageView);
            checkView = itemView.findViewById(R.id.checkView);
        }
    }
}
