package au.com.simplesoftware.gc.adaptor;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import au.com.simplesoftware.gc.bo.ParseGarageSaleInfo;

/**
 * Created by steven on 28/06/2015.
 */
public class

        MyPostsAdaptorQueryFactory implements ParseQueryAdapter.QueryFactory<ParseGarageSaleInfo> {

    public ParseQuery<ParseGarageSaleInfo> create() {
        return generate();
    }
    
    public static ParseQuery<ParseGarageSaleInfo> generate() {

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseGarageSaleInfo> query = ParseGarageSaleInfo.getQuery();
        query.include("user");
        query.orderByDescending("createdAt");
        query.whereEqualTo("user", currentUser);

        return query;
    }


}
