package com.dima.pikabu.src;

import java.util.ArrayList;

import static com.dima.pikabu.MainActivity.activity;

public abstract class InfiniteScrollListener {
    private int bufferItemCount = 10;
    private int currentPage = 0;
    private int itemCount = 0;
    private boolean isLoading = true;
    private ArrayList<PostItem> objects;

    protected InfiniteScrollListener(int bufferItemCount, ArrayList<PostItem> products) {
        this.bufferItemCount = bufferItemCount;
        this.objects = products;
    }

    public abstract void loadMore(int page, int totalItemsCount);

    public void onScroll(int firstVisibleItem, int visibleItemCount)
    {
        int totalItemCount=objects.size();
        if (totalItemCount < itemCount) {
            this.itemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.isLoading = true;
            }
        }

        for (int i = 0; i < totalItemCount; i++) {
            if(i<firstVisibleItem || i>firstVisibleItem + visibleItemCount) {
                objects.get(i).remove_image();
            }
            else{
                if(i>=firstVisibleItem && i<firstVisibleItem + visibleItemCount) {
                    objects.get(i).obn_image();
                }
                else{
                    objects.get(i).obn_first_image();
                }
            }
        }

        if (isLoading && (totalItemCount > itemCount)) {
            isLoading = false;
            itemCount = totalItemCount;
            currentPage++;
        }

        if (!isLoading && !activity.fpl.boolArhiv && !activity.fpl.boolFavorit && (totalItemCount - visibleItemCount)<=(firstVisibleItem + bufferItemCount)) {
            loadMore(currentPage + 1, totalItemCount);
            isLoading = true;
        }
    }
}