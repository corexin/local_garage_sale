package au.com.simplesoftware.gc.bo;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Data model for a post.
 */
@ParseClassName("GarageSaleInfo")
public class ParseGarageSaleInfo extends ParseObject implements Serializable {
    public String getName() {
        return getString("name");
    }

    public void setName(String value) {
        put("name", value);
    }

    public String getPhoneNumber() {
        return getString("phone");
    }

    public void setPhoneNumber(String value) {
        put("phone", value);
    }

    public String getEmail() {
        return getString("email");
    }

    public void setEmail(String email) {
        put("email", email);
    }


    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String value) {
        put("address", value);
    }


    public String getMessage() {
        return getString("message");
    }

    public void setMessage(String value) {
        put("message", value);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }

    public static ParseQuery<ParseGarageSaleInfo> getQuery() {
        return ParseQuery.getQuery(ParseGarageSaleInfo.class);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{
                getName(),    //auto-boxed
                getAddress(), //auto-boxed
                getPhoneNumber(),
                getMessage()
                , getUser()
        });
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ParseGarageSaleInfo) {
            ParseGarageSaleInfo other = (ParseGarageSaleInfo) o;
            return Arrays.equals(new Object[]{getName(),    //auto-boxed
                    getAddress(), //auto-boxed
                    getPhoneNumber(),
                    getMessage()
                    , getUser()}, new Object[]{other.getName(),    //auto-boxed
                    other.getAddress(), //auto-boxed
                    other.getPhoneNumber(),
                    other.getMessage()
                    , other.getUser()});

        } else {
            return false;
        }
    }
}
