package au.com.simplesoftware.gc;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import au.com.simplesoftware.gc.adaptor.MyFavorateAdapter;
import au.com.simplesoftware.gc.adaptor.MyPostsAdapter;
import au.com.simplesoftware.gc.bo.ParseGarageSaleInfo;
import au.com.simplesoftware.gc.util.LocationUtil;


public class MainActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private static final int MAX_POST_SEARCH_RESULTS = 20;

    private SupportMapFragment mapFragment;
    private Location currentLocation;

    // A request to connect to Location Services
    private LocationRequest locationRequest;
    private GoogleApiClient locationClient;

    private LinearLayout leftDrawer;
    private ListView myPostListView;
    private ListView myFavoriteListView;
    private DrawerLayout contentLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private HashMap<Marker, ParseGarageSaleInfo> markerGCMap = new HashMap<Marker, ParseGarageSaleInfo>();
    private HashMap<ParseGarageSaleInfo, Marker> gcMakerMap = new HashMap<ParseGarageSaleInfo, Marker>();

    private MyPostsAdapter myPostsAdapter = new MyPostsAdapter(this);
    private MyFavorateAdapter myFavorateAdapter = new MyFavorateAdapter(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.drawer);
        myPostsAdapter.setAutoload(true);

        leftDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        contentLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        myPostListView = (ListView) findViewById(R.id.left_drawer_my_posts);
        myFavoriteListView = (ListView) findViewById(R.id.left_drawer_my_favorites);

        // Set the adapter for the list view
        myPostListView.setAdapter(myPostsAdapter);
        myFavoriteListView.setAdapter(myFavorateAdapter);

        // Set the list's click listener
        myPostListView.setOnItemClickListener(new DrawerItemClickListener());
        myFavoriteListView.setOnItemClickListener(new DrawerItemClickListener());


        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                contentLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("Garage Sale");
                supportInvalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("My Posts");
                myPostsAdapter.loadObjects();
                supportInvalidateOptionsMenu();
            }
        };

        contentLayout.setDrawerListener(mDrawerToggle);

        LocationUtil.initiLocationRequest(locationRequest);

        // Create a new location client, using the enclosing class to handle callbacks.
        locationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(au.com.simplesoftware.gc.R.id.map_fragment);
        // Enable the current location "blue dot"
        mapFragment.getMap().setMyLocationEnabled(true);
        // Set up the camera change handler
        mapFragment.getMap().setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                // When the camera changes, update the query
                reloadParseData(null);
            }
        });
        mapFragment.getMap().setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        Log.d("GC", "onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(au.com.simplesoftware.gc.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item java.lang.Objectclicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("GaragelSale", "option selected ");
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d("GaragelSale", "home option selected ");
            return true;
        } else if (id == R.id.action_add) {
            Intent intent = new Intent(this, AddActivity.class);
            intent.putExtra(LocationUtil.INTENT_EXTRA_LOCATION, currentLocation);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_refresh) {
            onLocationChanged(currentLocation);
            return true;
        } else if (id == R.id.action_logout) {
            // Call the Parse log out method
            ParseUser.logOut();
            Intent intent = new Intent(this, DispatchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connect to Google Play service failed", Toast.LENGTH_LONG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("GC", "onStart");
        // Connect the client.
        if (LocationUtil.isGooglePlayServiceConnected(this)) {
            locationClient.connect();
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Log.d("GC", "onResumeFragments");
        ((ParseQueryAdapter) myPostListView.getAdapter()).loadObjects();
        if (locationClient.isConnected()) {
            Log.d("GC", "location client connected");
        }
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        Log.d("GC", "onStop");
        locationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("GC", "onLocationChanged");
        reloadCurrentPosition();
        reloadParseData(location);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("GC", "onConnected");

        reloadCurrentPosition();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GC", "location suspended" + i);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, locationRequest, this);
        Log.d("GC", "onConnectionSuspended");
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                locationClient, this);
        Log.d("GC", "stop location update");
    }

    private void reloadParseData(Location loc) {
        if (loc != null)
            currentLocation = loc;

        ParseQuery<ParseGarageSaleInfo> query = ParseQuery.getQuery("GarageSaleInfo");
        Calendar currentCal = Calendar.getInstance();
        currentCal.add(Calendar.MONTH, -1);
        query.whereGreaterThan("createdAt", currentCal.getTime());

        Float distance = GarageSaleApplication.getSearchDistance();
        query.whereWithinKilometers("location", LocationUtil.geoPointFromLocation(currentLocation), distance);
        query.setLimit(MAX_POST_SEARCH_RESULTS);
        query.findInBackground(new FindCallback<ParseGarageSaleInfo>() {

            @Override
            public void done(final List<ParseGarageSaleInfo> list, com.parse.ParseException e) {
                if (e == null) {
                    // if query return a list
                    Log.d("GV", "Found " + list.size() + " records");
                    mapFragment.getMap().clear();
                    markerGCMap.clear();

                    for (ParseGarageSaleInfo gc : list) {
                        addMarker(gc, "Garage Sale");
                    }
                } else {
                    // if there is exception
                    Log.d("GV", e.toString());
                    Toast.makeText(getApplicationContext(), "Error retriving from parse database", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void addMarker(ParseGarageSaleInfo gc, String title) {
        LatLng latlng = new LatLng(gc.getLocation().getLatitude(), gc.getLocation().getLongitude());
        MarkerOptions opt = new MarkerOptions().position(latlng).title("Garage Sale").icon(
                BitmapDescriptorFactory.fromResource(R.drawable.marker_48));
        Marker marker = mapFragment.getMap().addMarker(opt);
        markerGCMap.put(marker, gc);
        gcMakerMap.put(gc, marker);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.i("GoogleMapActivity", "onMarkerClick");
        ParseGarageSaleInfo gc = markerGCMap.get(marker);

        Toast.makeText(getApplicationContext(),
                "Garage Sale : " + gc.getName() + ": " + gc.getPhoneNumber() + ": " + gc.getAddress(), Toast.LENGTH_LONG)
                .show();
        return false;
    }

    private void reloadCurrentPosition() {
        if (locationClient.isConnected()) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                    locationClient);
        }
        reloadParseData(currentLocation);
        LocationUtil.moveTo(mapFragment.getMap(), currentLocation, 15);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {


        contentLayout.closeDrawer(leftDrawer);

        ParseGarageSaleInfo gc = (ParseGarageSaleInfo) myPostsAdapter.getItem(position);
        Marker marker = gcMakerMap.get(gc);
        LocationUtil.animateTo(mapFragment.getMap(), marker.getPosition(), 15);

    }

}
