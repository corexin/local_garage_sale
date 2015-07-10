package au.com.simplesoftware.gc;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseObject;

import au.com.simplesoftware.gc.bo.ParseMyFavorite;
import au.com.simplesoftware.gc.util.ConfigHelper;
import au.com.simplesoftware.gc.bo.ParseGarageSaleInfo;


public class GarageSaleApplication extends android.app.Application {

  // Key for saving the search distance preference
  private static final String KEY_SEARCH_DISTANCE = "searchDistance";

  private static final float DEFAULT_SEARCH_DISTANCE = 20.0f;

  private static SharedPreferences preferences;
  private static ConfigHelper configHelper;

  public GarageSaleApplication() {
  }

  @Override
  public void onCreate() {
    super.onCreate();

    ParseObject.registerSubclass(ParseGarageSaleInfo.class);
    ParseObject.registerSubclass(ParseMyFavorite.class);
    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    Parse.initialize(this, "P8yzXOB16Qh5Tk0iUiooMDFd239aPvyC0QVAoy58", "at34PyVBLSd6BtjQercp3qMUc61Q1l1GhRwgSRbZ");

    preferences = getSharedPreferences("com.parse.garagesale", Context.MODE_PRIVATE);

    configHelper = new ConfigHelper();
    configHelper.fetchConfigIfNeeded();
  }

  public static float getSearchDistance() {
    return preferences.getFloat(KEY_SEARCH_DISTANCE, DEFAULT_SEARCH_DISTANCE);
  }

  public static ConfigHelper getConfigHelper() {
    return configHelper;
  }

  public static void setSearchDistance(float value) {
    preferences.edit().putFloat(KEY_SEARCH_DISTANCE, value).commit();
  }

}
