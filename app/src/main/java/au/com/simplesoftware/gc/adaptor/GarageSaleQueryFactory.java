package au.com.simplesoftware.gc.adaptor;

import android.location.Location;

import com.parse.ParseQuery;

import java.util.Calendar;

import au.com.simplesoftware.gc.GarageSaleApplication;
import au.com.simplesoftware.gc.MainActivity;
import au.com.simplesoftware.gc.bo.ParseGarageSaleInfo;
import au.com.simplesoftware.gc.util.LocationUtil;

/**
 * Created by steven on 5/07/2015.
 */
public class GarageSaleQueryFactory {

    public static ParseQuery<ParseGarageSaleInfo> getGarageSaleQuery(Location currentLocation){
        ParseQuery<ParseGarageSaleInfo> query = ParseGarageSaleInfo.getQuery();
        Calendar currentCal = Calendar.getInstance();
        currentCal.add(Calendar.MONTH, -1);
        query.whereGreaterThan("createdAt", currentCal.getTime());

        Float distance = GarageSaleApplication.getSearchDistance();
        query.whereWithinKilometers("location", LocationUtil.generateParsePoint(currentLocation), distance);

        query.setLimit(MainActivity.MAX_POST_SEARCH_RESULTS);

        return query;
    }
}
