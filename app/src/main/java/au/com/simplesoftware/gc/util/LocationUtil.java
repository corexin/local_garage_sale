package au.com.simplesoftware.gc.util;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.parse.ParseGeoPoint;

/**
 * Created by steven on 12/06/2015.
 */
public class LocationUtil {

    public static final String INTENT_EXTRA_LOCATION = "location";
    // The update interval
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // Update interval in milliseconds
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;

    // A fast interval ceiling
    private static final int FAST_CEILING_IN_SECONDS = 1;

    // A fast ceiling of update intervals, used when the app is visible
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;

    public static boolean isGooglePlayServiceConnected(Activity activity) {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            // In debug mode, log the status
            Log.d("GC", "Google play services available");

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            return false;
        }
    }

    public static ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    public static ParseGeoPoint generateParsePoint(Location location) {
        return new ParseGeoPoint(location.getLatitude(), location.getLongitude());
    }

    public static void initiLocationRequest(LocationRequest locationRequest) {
        // Create a new global location parameters object
        locationRequest = LocationRequest.create();
        // Set the update interval
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Set the interval ceiling to one minute
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);
    }

    public  static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
