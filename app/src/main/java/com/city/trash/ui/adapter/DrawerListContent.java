package com.city.trash.ui.adapter;



import com.city.trash.AppApplication;
import com.city.trash.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold the data for Navigation Drawer Items
 */
public class DrawerListContent {
    //An array of sample (Settings) items.
    public static List<DrawerItem> ITEMS = new ArrayList<>();

    //A map of sample (Settings) items, by ID.
    public static Map<String, DrawerItem> ITEM_MAP = new HashMap<>();

    static {
       /* addItem(new DrawerItem("1", AppApplication.getApplication().getResources().getString(R.string.info_bind), R.mipmap.icon_bangding));
        addItem(new DrawerItem("2", AppApplication.getApplication().getResources().getString(R.string.in_ware), R.mipmap.icon_ruku));
        addItem(new DrawerItem("3", AppApplication.getApplication().getResources().getString(R.string.out_ware), R.mipmap.icon_chuku));
        addItem(new DrawerItem("4", AppApplication.getApplication().getResources().getString(R.string.record_find), R.mipmap.icon_chaxu));
*//*        addItem(new DrawerItem("5", AppApplication.getApplication().getResources().getString(R.string.foreign_in_ware), R.mipmap.icon_foreign_in_ware));*//*
        addItem(new DrawerItem("5", AppApplication.getApplication().getResources().getString(R.string.default_version), R.mipmap.icon_version));
        addItem(new DrawerItem("6", AppApplication.getApplication().getResources().getString(R.string.logout), R.mipmap.icon_logout));
*/    }

    /**
     * Method to add a new item
     *
     * @param item - Item to be added
     */
    private static void addItem(DrawerItem item) {

        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A Drawer item represents an entry in the navigation drawer.
     */
    public static class DrawerItem {
        public String id;
        public String content;
        public int icon;

        public DrawerItem(String id, String content, int icon_id) {
            this.id = id;
            this.content = content;
            this.icon = icon_id;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
