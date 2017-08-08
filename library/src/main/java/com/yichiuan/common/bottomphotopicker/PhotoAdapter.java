package com.yichiuan.common.bottomphotopicker;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yichiuan.common.R;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>
        implements View.OnClickListener {

    private static final String MIME_TYPE_GIF = "image/gif";

    static final String EXTRA_SRC = "src";
    static final String EXTRA_TRANSITION_NAME = "transition";

    private final LayoutInflater inflater;

    private Cursor cursor;
    private SelectListener selectListener;

    public PhotoAdapter(Context context, Cursor cursor) {
        inflater = LayoutInflater.from(context);
        this.cursor = cursor;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = inflater.inflate(R.layout.item_photo_cell, parent, false);
        PhotoViewHolder holder = new PhotoViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            Context context = parent.getContext();
            Intent intent = new Intent(context, PhotoPreviewActivity.class);

            moveCursor(holder.getAdapterPosition());

            int idOfData = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int indexOfData = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String id = cursor.getString(idOfData);
            String src = cursor.getString(indexOfData);
            intent.putExtra(EXTRA_SRC, src);
            intent.putExtra(EXTRA_TRANSITION_NAME, id);

            ViewCompat.setTransitionName(holder.photoView, id);

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context, holder.photoView, id);

            holder.itemView.getContext().startActivity(intent, options.toBundle());
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        holder.checkView.setTag(position);
        holder.checkView.setOnClickListener(this);

        moveCursor(position);
        int indexOfData = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int indexOfMimeType = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);

        String src = cursor.getString(indexOfData);
        String mimeType = cursor.getString(indexOfMimeType);

        Context context = inflater.getContext();

        if (mimeType.equals(MIME_TYPE_GIF)) {
            holder.badgeView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(src)
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.DATA))
                    .apply(placeholderOf(android.R.drawable.screen_background_dark))
                    .into(holder.photoView);
        } else {
            holder.badgeView.setVisibility(View.INVISIBLE);
            Glide.with(context).load(src)
                    .apply(placeholderOf(android.R.drawable.screen_background_dark))
                    .into(holder.photoView);
        }
    }

    private void moveCursor(int position) {
        if (!isCursorValid(cursor)) {
            throw new IllegalStateException("Cannot bind view holder when cursor is in invalid state.");
        }

        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position
                    + " when trying to bind view holder");
        }
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
        final ImageView photoView;
        final CheckView checkView;
        final TextView badgeView;

        PhotoViewHolder(View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.imageView);
            checkView = itemView.findViewById(R.id.checkView);
            badgeView = itemView.findViewById(R.id.textview_badge);
        }
    }
}
