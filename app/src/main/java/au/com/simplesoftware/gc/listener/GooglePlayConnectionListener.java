package au.com.simplesoftware.gc.listener;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import au.com.simplesoftware.gc.MainActivity;
import au.com.simplesoftware.gc.R;
import au.com.simplesoftware.gc.util.LocationUtil;

/**
 * Created by steven on 7/07/2015.
 */
public class GooglePlayConnectionListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private final MainActivity mainActivity;

    public GooglePlayConnectionListener(MainActivity ctx) {
        this.mainActivity = ctx;
    }
    
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("GC", "onConnected");
        LocationUtil.startLocationUpdates();

        LocationUtil.reloadCurrentPosition();
        LocationUtil.moveToCurrentLocation(mainActivity.map, 15);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GC", "location suspended" + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("GarageSale", "Network connection issue" + connectionResult);
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(R.string.googleplay_connection_error_title).setMessage(R.string.googleplay_connection_error_messag);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
