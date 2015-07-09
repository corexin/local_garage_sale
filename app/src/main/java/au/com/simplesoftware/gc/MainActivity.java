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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

import au.com.simplesoftware.gc.adaptor.GarageSaleQueryFactory;
import au.com.simplesoftware.gc.adaptor.MyFavoriateAdapter;
import au.com.simplesoftware.gc.adaptor.MyFavoriateAdaptorQueryFactory;
import au.com.simplesoftware.gc.adaptor.MyPostsAdapter;
import au.com.simplesoftware.gc.adaptor.MyPostsAdaptorQueryFactory;
import au.com.simplesoftware.gc.bo.ParseGarageSaleInfo;
import au.com.simplesoftware.gc.bo.ParseMyFavoriate;
import au.com.simplesoftware.gc.listener.DrawerToggleListener;
import au.com.simplesoftware.gc.listener.MarkerClickListener;
import au.com.simplesoftware.gc.listener.MyFavoriateItemClickListener;
import au.com.simplesoftware.gc.listener.MyPostItemClickListener;
import au.com.simplesoftware.gc.util.DualFavoriteLists;
import au.com.simplesoftware.gc.util.LocationUtil;


public class MainActivity extends AppCompatActivity implements LocationListener {

    public static final int MAX_POST_SEARCH_RESULTS = 20;

    public GoogleMap map;

    private ActionBarDrawerToggle mDrawerToggle;

    private HashMap<Marker, ParseGarageSaleInfo> markerGCMap = new HashMap<Marker, ParseGarageSaleInfo>();
    private HashMap<ParseGarageSaleInfo, Marker> gcMarkerMap = new HashMap<ParseGarageSaleInfo, Marker>();

    private MyPostsAdapter myPostsAdapter = new MyPostsAdapter(this);
    private MyFavoriateAdapter myFavoriateAdapter = new MyFavoriateAdapter(this);

    public static DualFavoriteLists dualFavoriteLists = new DualFavoriteLists();
    public static ParseGarageSaleInfo currentGarageSale;

    private DrawerLayout mainActivityLayout;
    private LinearLayout leftDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GarageSale", "onCreate");

        setContentView(R.layout.activity_main);
        LocationUtil.init(this);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.drawer);

        // find main layout and drawer layout
        leftDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        mainActivityLayout = (DrawerLayout) findViewById(R.id.activity_main_layout_id);

        // find map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        this.map = mapFragment.getMap();
        // Enable the current location "blue dot"
        map.setMyLocationEnabled(true);

        // Set up the camera change handler
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                // When the camera changes, update the query
                Log.d("GarageSale", "on camerage change");
                reloadParseData(LocationUtil.generateLocation(position.target));
            }
        });
        map.setOnMarkerClickListener(new MarkerClickListener(this, markerGCMap));

        // find two list view in drawer
        ListView myPostListView = (ListView) findViewById(R.id.left_drawer_my_posts);
        ListView myFavoriteListView = (ListView) findViewById(R.id.left_drawer_my_favorites);
        // Set the adapter for the list view
        myPostListView.setAdapter(myPostsAdapter);
        myFavoriteListView.setAdapter(myFavoriateAdapter);
        // Set the list's click listener
        myPostListView.setOnItemClickListener(new MyPostItemClickListener(map, gcMarkerMap, myPostsAdapter, mainActivityLayout, leftDrawer));
        myFavoriteListView.setOnItemClickListener(new MyFavoriateItemClickListener(map, gcMarkerMap, myFavoriateAdapter, mainActivityLayout, leftDrawer));


        mDrawerToggle = new DrawerToggleListener(this, mainActivityLayout, R.string.drawer_open, R.string.drawer_close);
        mainActivityLayout.setDrawerListener(mDrawerToggle);

        LocationUtil.init(this);

        MyFavoriateAdaptorQueryFactory.generate().findInBackground(new FindCallback<ParseMyFavoriate>() {
            @Override
            public void done(List list, ParseException e) {
                if (e == null) {
                    // if query return a list
                    Log.d("GarageSale", "Found Favoriate" + list.size() + " records");

                    dualFavoriteLists.clear();
                    dualFavoriteLists.addAllOriginal(list);

                } else {
                    // if there is exception
                    Log.d("GarageSale", e.toString());
                    Toast.makeText(getApplicationContext(), "Error retriving from parse database", Toast.LENGTH_LONG);
                }
            }
        });
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
            Intent intent = new Intent(this, EditGarageSaleActivity.class);
            currentGarageSale = new ParseGarageSaleInfo();
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_refresh) {
            reloadParseData(LocationUtil.latestCurrentLocation);
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
    protected void onStart() {
        super.onStart();
        Log.d("GarageSale", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GarageSale", "onResume");
        LocationUtil.connectGoogleAPIClient();
        myPostsAdapter.notifyDataSetChanged();
        Log.d("GarageSale", "notify mypostadaptor database changes");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationUtil.stopLocationUpdates();
        LocationUtil.disconnectGoogleAPIClient();
    }

    @Override
    public void onLocationChanged(Location location) {
        float[] result = new float[2];
        Location.distanceBetween(LocationUtil.latestCurrentLocation.getLatitude(), LocationUtil.latestCurrentLocation.getLongitude(), location.getLatitude(), location.getLongitude(), result);

        if (result[0] > LocationUtil.MIN_MOVE_METER) {
            Log.d("GarageSale", "onLocationChanged - distance changed: " + result[0] + ":" + result[1]);
            reloadParseData(LocationUtil.reloadCurrentPosition());
        }
    }

    void reloadParseData(Location loc) {
        if (loc != null) {
            ParseQuery<ParseGarageSaleInfo> aroundQuery = GarageSaleQueryFactory.getGarageSaleQuery(loc);
            aroundQuery.findInBackground(new FindCallback<ParseGarageSaleInfo>() {
                @Override
                public void done(final List<ParseGarageSaleInfo> list, com.parse.ParseException e) {
                    if (e == null && !list.isEmpty()) {
                        // if query return a list
                        Log.d("GarageSale", "Found surounding" + list.size() + " GarageSale records");
                        map.clear();
                        markerGCMap.clear();
                        gcMarkerMap.clear();

                        for (ParseGarageSaleInfo gc : list) {
                            addMarker(gc);
                        }
                    } else {
                        // if there is exception
                        Log.d("GarageSale", e.toString());
                    }

                    ParseQuery<ParseGarageSaleInfo> mypostQuery = MyPostsAdaptorQueryFactory.generate();
                    mypostQuery.findInBackground(new FindCallback<ParseGarageSaleInfo>() {

                        @Override
                        public void done(final List<ParseGarageSaleInfo> list, com.parse.ParseException e) {
                            if (e == null && !list.isEmpty()) {
                                // if query return a list
                                Log.d("GarageSale", "Found mine " + list.size() + " GarageSale records");
                                for (ParseGarageSaleInfo gc : list) {
                                    addMarker(gc);
                                }
                            } else {
                                // if there is exception
                                Log.d("GarageSale", e.toString());
                            }
                        }
                    });
                }
            });

        }
    }

    private void addMarker(ParseGarageSaleInfo gc) {

        if(gcMarkerMap.get(gc)==null) {
            Log.d("GarageSale", "add marker " + gc.getAddress());
            LatLng latlng = new LatLng(gc.getLocation().getLatitude(), gc.getLocation().getLongitude());
            MarkerOptions opt = new MarkerOptions().position(latlng).title("Garage Sale").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.marker_48));
            Marker marker = map.addMarker(opt);
            markerGCMap.put(marker, gc);
            gcMarkerMap.put(gc, marker);
        }else{
            Log.d("GarageSale",gc.getAddress() + "is already added to map");
        }
    }

}
