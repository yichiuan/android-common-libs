package com.yichiuan.common.bottomphotopicker;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public final class MediaPicker {

    private RecyclerView recyclerView;

    private PhotoAdapter adapter;

    private Cursor cursor;

    private List<Uri> selectecUris = new ArrayList<>();

    private StateListener stateListener;

    private MediaPicker(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        adapter = new PhotoAdapter(recyclerView.getContext(), null);
        adapter.setSelectListener(new PhotoAdapter.SelectListener() {
            @Override
            public void onSelected(Uri uri) {
                if (!selectecUris.contains(uri)) {
                    selectecUris.add(uri);

                    if (stateListener != null) {
                        stateListener.changed(selectecUris);
                    }
                }
            }

            @Override
            public void onDeselected(Uri uri) {
                if (selectecUris.remove(uri)) {
                    if (stateListener != null) {
                        stateListener.changed(selectecUris);
                    }
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public static MediaPicker with(@NonNull RecyclerView recyclerView) {
        return new MediaPicker(recyclerView);
    }

    public MediaPicker load(@NonNull Cursor cursor) {
        adapter.swapCursor(cursor);
        return this;
    }

    public MediaPicker subscribe(@NonNull StateListener stateListener) {
        this.stateListener = stateListener;
        return this;
    }

    public interface StateListener {
        void changed(List<Uri> uris);
    }
}
