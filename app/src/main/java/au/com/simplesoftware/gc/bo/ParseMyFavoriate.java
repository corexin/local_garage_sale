package au.com.simplesoftware.gc.bo;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;

/**
 * Data model for a post.
 */
@ParseClassName("MyFavoriate")
public class ParseMyFavoriate extends ParseObject {
    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public ParseGarageSaleInfo getGarageSale() {
        return (ParseGarageSaleInfo) getParseObject("garagesale");
    }

    public void setGarageSale(ParseGarageSaleInfo gc) {
        put("garagesale", gc);
    }

    public static ParseQuery<ParseMyFavoriate> getQuery() {
        return ParseQuery.getQuery(ParseMyFavoriate.class);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{
                getUser(), getGarageSale()
        });
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ParseMyFavoriate) {
            ParseMyFavoriate other = (ParseMyFavoriate) o;
            return Arrays.equals(new Object[]{getGarageSale()
                    , getUser()}, new Object[]{other.getGarageSale()
                    , other.getUser()});

        } else {
            return false;
        }
    }
}
