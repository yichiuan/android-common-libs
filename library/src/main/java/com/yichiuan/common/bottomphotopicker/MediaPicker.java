package com.yichiuan.common.bottomphotopicker;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public final class MediaPicker {

    private RecyclerView recyclerView;

    private PhotoAdapter adapter;

    private Cursor cursor;

    private StateListener stateListener;

    private MediaPicker(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        recyclerView.setHasFixedSize(true);

        adapter = new PhotoAdapter(recyclerView.getContext(), null);
        adapter.setSelectListener(new PhotoAdapter.SelectListener() {
            @Override
            public void onSelected(ArrayList<Uri> uris) {
                if (stateListener != null) {
                    stateListener.changed(uris);
                }
            }

            @Override
            public void onDeselected(ArrayList<Uri> uris) {
                if (stateListener != null) {
                    stateListener.changed(uris);
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

    public ArrayList<Uri> getSelectedUris() {
        return adapter.getSelectedUris();
    }

    public void setSelectedUris(ArrayList<Uri> uris) {
        adapter.setSelectedUris(uris);
    }

    public interface StateListener {
        void changed(ArrayList<Uri> uris);
    }
}
