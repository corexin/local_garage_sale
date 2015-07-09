package au.com.simplesoftware.gc.listener;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import au.com.simplesoftware.gc.EditGarageSaleActivity;
import au.com.simplesoftware.gc.MainActivity;
import au.com.simplesoftware.gc.bo.ParseGarageSaleInfo;

/**
 * Created by steven on 7/07/2015.
 */
public class MarkerClickListener implements GoogleMap.OnMarkerClickListener {

    private final Context context;
    private HashMap<Marker, ParseGarageSaleInfo> markerGCMap = new HashMap<Marker, ParseGarageSaleInfo>();

    public MarkerClickListener(Context ctx, HashMap<Marker, ParseGarageSaleInfo> markerGCMap) {
        this.context = ctx;
        this.markerGCMap = markerGCMap;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (context != null) {
            Log.i("GoogleMapActivity", "onMarkerClick");
            ParseGarageSaleInfo gc = markerGCMap.get(marker);
            MainActivity.currentGarageSale = gc;
            Intent intent = new Intent(context, EditGarageSaleActivity.class);
            context.startActivity(intent);
            return true;
        }
        return false;
    }
}
