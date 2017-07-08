package com.yichiuan.common.bottomphotopicker;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yichiuan.common.R;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>{

    private LayoutInflater layoutInflater;

    private Cursor cursor;

    public PhotoAdapter(Context context, Cursor cursor) {
        layoutInflater = LayoutInflater.from(context);
        this.cursor = cursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.item_photo_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Cursor cursor = getItem(position);

        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
        String src = cursor.getString(index);

        Context context = layoutInflater.getContext();

        Glide.with(context).load(src)
                .apply(placeholderOf(android.R.drawable.screen_background_dark))
                .into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    private Cursor getItem(final int position)
    {
        if (!cursor.isClosed())
        {
            cursor.moveToPosition(position);
        }

        return cursor;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView photoView;

        ViewHolder(View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.imageView);
        }
    }
}
