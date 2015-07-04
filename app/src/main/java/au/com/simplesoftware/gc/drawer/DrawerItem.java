package au.com.simplesoftware.gc.drawer;

import au.com.simplesoftware.gc.R;

/**
 * Created by steven on 27/06/2015.
 */
public class DrawerItem {
    public enum TYPE {MY_ITEM,MY_FAVERATE_ITEM}

    public TYPE type;
    public int icon;
    public String address;
    public int removeIcon = R.drawable.list_remove32;

    // Constructor.
    public DrawerItem(String address) {
        this.address = address;
    }
}
