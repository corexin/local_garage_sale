package au.com.simplesoftware.gc.bo;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Data model for a post.
 */
@ParseClassName("MyFavorite")
public class ParseMyFavorite extends ParseObject implements Serializable {

    public static final String userKey = "user";
    public static final String favoriateKey = "favoriate";

    public ParseUser getUser() {
        return getParseUser(userKey);
    }

    public void setUser(ParseUser value) {
        put(userKey, value);
    }

    public ParseGarageSaleInfo getGarageSale() {
        return (ParseGarageSaleInfo) getParseObject(favoriateKey);
    }

    public void setGarageSale(ParseGarageSaleInfo gc) {
        put(favoriateKey, gc);
    }


    public static ParseQuery<ParseMyFavorite> getQuery() {
        return ParseQuery.getQuery(ParseMyFavorite.class);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{
                getUser(), getGarageSale().getObjectId()
        });
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ParseMyFavorite) {
            ParseMyFavorite other = (ParseMyFavorite) o;
            return Arrays.equals(new Object[]{getUser(), getGarageSale().getObjectId()}, new Object[]{other.getUser(), other.getGarageSale().getObjectId()});

        } else {
            return false;
        }
    }
}
