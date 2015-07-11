package au.com.simplesoftware.gc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.parse.DeleteCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import au.com.simplesoftware.gc.bo.ParseGarageSaleInfo;
import au.com.simplesoftware.gc.bo.ParseMyFavorite;
import au.com.simplesoftware.gc.util.LocationUtil;
import au.com.simplesoftware.gc.util.UIHelper;


public class EditGarageSaleActivity extends AppCompatActivity {

    enum Status {NEW, VIEW, EDIT}

    ;
    private Status status;

    private ImageView favoriateImage;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText addressEditText;
    private EditText messageEditText;

    private Button deleteButton;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_garagesale);

        favoriateImage = (ImageView) findViewById(R.id.action_add_favoriate_id);
        nameEditText = (EditText) findViewById(R.id.name);
        phoneEditText = (EditText) findViewById(R.id.phone);
        emailEditText = (EditText) findViewById(R.id.garagesale_contact_email);
        addressEditText = (EditText) findViewById(R.id.address);
        messageEditText = (EditText) findViewById(R.id.message);

        saveButton = (Button) findViewById(R.id.action_save);
        deleteButton = (Button) findViewById(R.id.action_delete);

        if (MainActivity.currentGarageSale != null) {
            Log.d("GarageSale", "view/create/edit a garageSale");
            favoriateImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    toggleFavoriate();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("GarageSale", "Start saving...");
                    deleteGarageSale();
                    Log.d("GarageSale", "End saving");
                    return;
                }
            });

            //noinspection SimplifiableIfStatement
            saveButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("GarageSale", "Start saving...");
                    saveGarageSale();
                    Log.d("GarageSale", "End saving");
                    return;
                }
            });

            // determine the current state
            if (MainActivity.currentGarageSale.getObjectId() == null) {
                status = Status.NEW;
            } else {
                if (MainActivity.currentGarageSale.getUser().equals(ParseUser.getCurrentUser())) {
                    status = Status.EDIT;
                } else {
                    status = Status.VIEW;
                }
            }
            setComponentsVisibility();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setComponentsVisibility() {
        // new
        if (Status.NEW.equals(status)) {
            Log.d("GarageSale", "New one");
            // its a new one, we can save
            deleteButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
            // we can not add favoriate as current garage sale info is not saved yet.
            favoriateImage.setVisibility(View.GONE);
        } else if (Status.VIEW.equals(status)) {
            // its existing one, we enable depending on whether current user is owner
            Log.d("GarageSale", "View ONLY");
            deleteButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            favoriateImage.setVisibility(View.VISIBLE);
            populateFields(true);

        } else {
            // we can save the field
            Log.d("GarageSale", "Current user's post, can EDIT");
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            favoriateImage.setVisibility(View.VISIBLE);
            populateFields(false);
        }

        ParseMyFavorite found = MainActivity.gcFavorMap.get(MainActivity.currentGarageSale);
        if (found != null) {
            favoriateImage.setImageResource(R.drawable.bookmark48);
        } else {
            favoriateImage.setImageResource(R.drawable.unbookmark48);
        }
    }

    private void deleteGarageSale() {
        if (MainActivity.currentGarageSale != null) {
            final ParseMyFavorite favor = MainActivity.gcFavorMap.get(MainActivity.currentGarageSale);
            final Marker marker = MainActivity.gcMarkerMap.get(MainActivity.currentGarageSale);
            MainActivity.currentGarageSale.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d("GarageSale", "Delete current GarageSale");
                    Toast.makeText(getApplicationContext(),"Current GarageSale delted", Toast.LENGTH_LONG).show();
                    MainActivity.currentGarageSale = null;
                    if(marker!=null)
                    {
                        marker.remove();
                        Log.d("GarageSale", "Removed Marker on map");
                    }

                    // remove favorite one if found it.
                    if (favor != null) {
                        favor.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                MainActivity.gcFavorMap.remove(favor);
                                Toast.makeText(getApplicationContext(), "Removed from Favorite list", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    finish();
                }
            });
        }
    }

    private void toggleFavoriate() {

        final ParseMyFavorite found = MainActivity.gcFavorMap.get(MainActivity.currentGarageSale);
        if (found != null) {
            // remove from db and favoriate list
            favoriateImage.setImageResource(R.drawable.unbookmark48);
            found.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    MainActivity.gcFavorMap.remove(MainActivity.currentGarageSale);
                    Toast.makeText(getApplicationContext(), "Removed from favoriate list", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // add to favoriate list and add to database
            if (MainActivity.currentGarageSale.getObjectId() != null) {
                favoriateImage.setImageResource(R.drawable.bookmark48);

                final ParseMyFavorite favor = new ParseMyFavorite();
                favor.setUser(ParseUser.getCurrentUser());
                favor.setGarageSale(MainActivity.currentGarageSale);
                ParseACL acl = new ParseACL(ParseUser.getCurrentUser());
                // Give public read access
                acl.setPublicReadAccess(true);
                acl.setPublicWriteAccess(true);
                favor.setACL(acl);

                favor.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        MainActivity.gcFavorMap.put(MainActivity.currentGarageSale, favor);
                        Toast.makeText(getApplicationContext(), "Added to favoriate list", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }

    }

    private void saveGarageSale() {

        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String msg = messageEditText.getText().toString();

        // Create a post.
        final ParseGarageSaleInfo garageSaleInfo = MainActivity.currentGarageSale;
        if (UIHelper.isNotEmpty(name)) {
            garageSaleInfo.setName(name);
        }
        if (UIHelper.isNotEmpty(phone)) {
            garageSaleInfo.setPhoneNumber(phone);
        }
        if (UIHelper.isNotEmpty(email)) {
            garageSaleInfo.setEmail(email);
        }
        if (UIHelper.isNotEmpty(address)) {
            garageSaleInfo.setAddress(address);
        }
        if (UIHelper.isNotEmpty(msg)) {
            garageSaleInfo.setMessage(msg);
        }
        if (garageSaleInfo.getLocation() == null) {
            garageSaleInfo.setLocation(LocationUtil.generateParsePoint(LocationUtil.latestDeviceLocation));
        }
        garageSaleInfo.setUser(ParseUser.getCurrentUser());

        ParseACL acl = new ParseACL(ParseUser.getCurrentUser());
        // Give public read access
        acl.setPublicReadAccess(true);
        acl.setWriteAccess(ParseUser.getCurrentUser(), true);
        garageSaleInfo.setACL(acl);

        // Save the post
        garageSaleInfo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("GarageSale", "Garage Sale Info Saved OK");
                    finish();
                } else {
                    Log.d("GarageSale", "Error saving Garage Sale:" + garageSaleInfo.getObjectId() + ", with error:" + e);

                }
            }
        });
    }

    private void populateFields(boolean readOnly) {
        if (readOnly) {
            nameEditText.setFocusable(false);
            phoneEditText.setFocusable(false);
            emailEditText.setFocusable(false);
            addressEditText.setFocusable(false);
            messageEditText.setFocusable(false);
            saveButton.setVisibility(View.GONE);
        }
        nameEditText.setText(MainActivity.currentGarageSale.getName());
        phoneEditText.setText(MainActivity.currentGarageSale.getPhoneNumber());
        emailEditText.setText(MainActivity.currentGarageSale.getEmail());
        addressEditText.setText(MainActivity.currentGarageSale.getAddress());
        messageEditText.setText(MainActivity.currentGarageSale.getMessage());
    }

}
