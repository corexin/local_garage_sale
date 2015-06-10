package au.com.simplesoftware.localgaragesale;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseObject;

import au.com.simplesoftware.localgaragesale.bo.ParsePostMessage;
import au.com.simplesoftware.localgaragesale.util.ConfigHelper;


public class GarageSaleApplication extends android.app.Application {
  // Debugging switch
  public static final boolean APPDEBUG = false;

  // Debugging tag for the application
  public static final String APPTAG = "AnyWall";

  // Used to pass location from MainActivity to PostActivity
  public static final String INTENT_EXTRA_LOCATION = "location";

  // Key for saving the search distance preference
  private static final String KEY_SEARCH_DISTANCE = "searchDistance";

  private static final float DEFAULT_SEARCH_DISTANCE = 250.0f;

  private static SharedPreferences preferences;
  private static ConfigHelper configHelper;

  public GarageSaleApplication() {
  }

  @Override
  public void onCreate() {
    super.onCreate();

    ParseObject.registerSubclass(ParsePostMessage.class);

    Parse.initialize(this, "RwjLNIOfjrpPsNdEjPurrPnOCMdXRr7Hctr7w3lF", "z7t0G3ZSdBvEeDmX9c5QU33SOfeXWslklQrjWJ8w");
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
