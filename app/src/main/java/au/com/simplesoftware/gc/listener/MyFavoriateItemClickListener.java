package au.com.simplesoftware.gc.listener;

import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Map;

import au.com.simplesoftware.gc.adaptor.MyFavoriateAdapter;
import au.com.simplesoftware.gc.bo.ParseGarageSaleInfo;
import au.com.simplesoftware.gc.bo.ParseMyFavorite;
import au.com.simplesoftware.gc.util.LocationUtil;

/**
 * Created by steven on 4/07/2015.
 */
public class MyFavoriateItemClickListener implements ListView.OnItemClickListener {

    private GoogleMap map;

    private LinearLayout leftDrawer;
    private DrawerLayout contentLayout;

    private MyFavoriateAdapter myPostsAdapter;

    private  Map<ParseGarageSaleInfo, Marker> gcMarkerMap;

    public MyFavoriateItemClickListener(GoogleMap map, Map<ParseGarageSaleInfo, Marker> gcMarkerMap, MyFavoriateAdapter myPostsAdapter,
                                        DrawerLayout contentLayout, LinearLayout leftDrawer
    )
    {
        this.map = map;
        this.gcMarkerMap = gcMarkerMap;
        this.myPostsAdapter = myPostsAdapter;
        this.contentLayout = contentLayout;
        this.leftDrawer = leftDrawer;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        selectItem(position);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        contentLayout.closeDrawer(leftDrawer);

        ParseMyFavorite favoriate = (ParseMyFavorite) myPostsAdapter.getItem(position);
        Marker marker = gcMarkerMap.get(favoriate.getGarageSale());
        LocationUtil.animateTo(map, marker.getPosition(), 15);

    }
}