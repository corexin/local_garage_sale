package au.com.simplesoftware.gc.adaptor;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import au.com.simplesoftware.gc.bo.ParseMyFavorite;

/**
 * Created by steven on 28/06/2015.
 */
public class

        MyFavoriateAdaptorQueryFactory implements ParseQueryAdapter.QueryFactory<ParseMyFavorite> {


    public ParseQuery<ParseMyFavorite> create() {
        return generate();
    }

    public static ParseQuery<ParseMyFavorite> generate()
    {   ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseMyFavorite> query = ParseMyFavorite.getQuery();
        query.orderByDescending("createdAt");
        query.whereEqualTo(ParseMyFavorite.userKey, currentUser);
        query.include(ParseMyFavorite.userKey);
        query.include(ParseMyFavorite.favoriateKey);
        return query;
    }
}
