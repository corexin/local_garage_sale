package au.com.simplesoftware.gc.adaptor;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import au.com.simplesoftware.gc.bo.ParseMyFavoriate;

/**
 * Created by steven on 28/06/2015.
 */
public class

        MyFavoriateAdaptorQueryFactory implements ParseQueryAdapter.QueryFactory<ParseMyFavoriate> {


    public ParseQuery<ParseMyFavoriate> create() {
        return generate();
    }

    public static ParseQuery<ParseMyFavoriate> generate()
    {   ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseMyFavoriate> query = ParseMyFavoriate.getQuery();
        query.orderByDescending("createdAt");
        query.whereEqualTo("user", currentUser);
        return query;
    }
}
