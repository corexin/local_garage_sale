package au.com.simplesoftware.gc.util;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

import au.com.simplesoftware.gc.MainActivity;
import au.com.simplesoftware.gc.listener.GooglePlayConnectionListener;

/**
 * Created by steven on 12/06/2015.
 */
public class LocationUtil {

    public static float MIN_MOVE_METER = 10f;

    // The update interval
    private static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // Update interval in milliseconds
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;

    // A fast interval ceiling
    private static final int FAST_CEILING_IN_SECONDS = 5;

    // A fast ceiling of update intervals, used when the app is visible
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;


    public static GoogleApiClient googleApiClient;
    public static LocationRequest locationRequest;
    private static MainActivity mainActivity;
    public static Location latestCurrentLocation;

    private LocationUtil() {
    }

    public static void init(MainActivity ctx) {
        mainActivity = ctx;
        initGoogleApiClient(ctx);
        initiLocationRequest();
        Log.d("GarageSale", "LocationUtil init");
    }

    public static void initGoogleApiClient(MainActivity ctx) {
        GooglePlayConnectionListener listener = new GooglePlayConnectionListener(ctx);
        googleApiClient = new GoogleApiClient.Builder(ctx).addApi(LocationServices.API).addConnectionCallbacks(listener).addOnConnectionFailedListener(listener).build();
    }

    public static void initiLocationRequest() {
        // Create a new global location parameters object
        locationRequest = LocationRequest.create();
        // Set the update interval
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Set the interval ceiling to one minute
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);
    }

    public static void connectGoogleAPIClient() {
        googleApiClient.connect();
        Log.d("GarageSale", "googleapiclient connect");
    }

    public static void disconnectGoogleAPIClient() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            Log.d("GarageSale", "googleapiclient disconnect");
        }
    }

    public static void startLocationUpdates() {
        if (googleApiClient.isConnected()) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, LocationUtil.locationRequest, mainActivity);
                Log.d("GarageSale", "request location update");
            }
        }
    }

    public static void stopLocationUpdates() {
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.
                    removeLocationUpdates(
                            googleApiClient, mainActivity);
            Log.d("GC", "stop location update.");
        }
    }


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

    public static ParseGeoPoint generateParsePoint(Location location) {
        return new ParseGeoPoint(location.getLatitude(), location.getLongitude());
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void animateTo(GoogleMap map, Location location, int zoom) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        animateTo(map, latLng, zoom);
    }

    public static void animateTo(GoogleMap map, LatLng latLng, int zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        map.animateCamera(cameraUpdate);
    }

    public static void moveToCurrentLocation(GoogleMap map, int zoom) {
        LatLng latLng = new LatLng(latestCurrentLocation.getLatitude(), latestCurrentLocation.getLongitude());
        moveTo(map, latLng, zoom);
    }

    public static void moveTo(GoogleMap map, Location location, int zoom) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveTo(map, latLng, zoom);
    }

    public static void moveTo(GoogleMap map, LatLng latLng, int zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        map.moveCamera(cameraUpdate);
    }

    public static Location reloadCurrentPosition() {

        if (googleApiClient != null && googleApiClient.isConnected()) {
            latestCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);
            Log.d("GC", latestCurrentLocation.getLatitude() + ":" + latestCurrentLocation.getLongitude());
        }

        return latestCurrentLocation;
    }

    public static Location generateLocation(LatLng target) {
        Location result = new Location("Camera location");
        result.setLatitude(target.latitude);
        result.setLongitude(target.longitude);
        return result;
    }
}
