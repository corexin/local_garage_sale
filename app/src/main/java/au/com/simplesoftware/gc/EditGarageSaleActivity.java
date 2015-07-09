package au.com.simplesoftware.gc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import au.com.simplesoftware.gc.bo.ParseGarageSaleInfo;
import au.com.simplesoftware.gc.bo.ParseMyFavoriate;
import au.com.simplesoftware.gc.util.LocationUtil;
import au.com.simplesoftware.gc.util.UIHelper;


public class EditGarageSaleActivity extends AppCompatActivity {

    private ParseGarageSaleInfo currentGarageSale;

    private ImageView favoriateImage ;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText addressEditText;
    private EditText messageEditText;

    private Button saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_garagesale);

        favoriateImage = (ImageView)findViewById(R.id.action_add_favoriate_id);
        nameEditText = (EditText) findViewById(R.id.name);
        phoneEditText = (EditText) findViewById(R.id.phone);
        emailEditText = (EditText) findViewById(R.id.garagesale_contact_email);
        addressEditText = (EditText) findViewById(R.id.address);
        messageEditText=(EditText)findViewById(R.id.message);

        saveButton = (Button) findViewById(R.id.action_save);

        if(MainActivity.currentGarageSale!=null)
        {
            Log.d("GarageSale","view/create/edit a garageSale");
            currentGarageSale = MainActivity.currentGarageSale;

            favoriateImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    toggleFavoriate();
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

            if(currentGarageSale.getObjectId()==null)
            {
                Log.d("GarageSale", "New one");
                // its a new one, we can save
                saveButton.setVisibility(View.VISIBLE);

                // we can not add favoriate as current garage sale info is not saved yet.
                favoriateImage.setVisibility(View.GONE);
            }else {
                // its existing one, we enable depending on whether current user is owner
                Log.d("GarageSale", "Existing one");
                if(currentGarageSale.getUser().equals(ParseUser.getCurrentUser()))
                {
                    // we can save the changes
                    populateFields(currentGarageSale, false);
                    saveButton.setVisibility(View.VISIBLE);
                    Log.d("GarageSale", "owner of the garagesale");
                }
                else {
                    // we can not save changes, make save button invisible
                    populateFields(currentGarageSale, true);
                    saveButton.setVisibility(View.GONE);
                    Log.d("GarageSale", "not owner of the garagesale");
                }
            }

            ParseMyFavoriate found = foundCurrentInMyFavoriatesList();
            if(found!=null)
            {
                favoriateImage.setImageResource(R.drawable.bookmark48);
            }
            else {
                favoriateImage.setImageResource(R.drawable.unbookmark48);
            }

        }
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

     private void toggleFavoriate() {

        final ParseMyFavoriate found = foundCurrentInMyFavoriatesList();
        if(found!=null)
        {
            // remove from db and favoriate list
            favoriateImage.setImageResource(R.drawable.unbookmark48);
            deletecurrentInMyFavoriatesList(found);
            found.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(getApplicationContext(), "Removed from favoriate list", Toast.LENGTH_LONG);
                }
            });
        }
        else {
            // add to favoriate list and add to database
            if(currentGarageSale.getObjectId()!=null) {
                ParseMyFavoriate favor = new ParseMyFavoriate();
                favor.setUser(ParseUser.getCurrentUser());
                favor.setGarageSale(currentGarageSale);
                favor.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(getApplicationContext(), "Added to favoriate list", Toast.LENGTH_LONG);
                    }
                });
                addCurrentInMyFavoriatesList(favor);
                favoriateImage.setImageResource(R.drawable.bookmark48);
            }
        }

    }

    private ParseMyFavoriate foundCurrentInMyFavoriatesList() {
        for(ParseMyFavoriate favor: MainActivity.dualFavoriteLists.myFavoriatesListUpdates)
        {
            if(favor.getGarageSale().equals(currentGarageSale))
            {
                return favor;
            }
        }
        return null;
    }

    private void deletecurrentInMyFavoriatesList(ParseMyFavoriate favor) {
        MainActivity.dualFavoriteLists.remove(favor);
    }
    private void addCurrentInMyFavoriatesList(ParseMyFavoriate favor) {
        MainActivity.dualFavoriteLists.add(favor);
    }

    private void saveGarageSale () {

        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String address =  addressEditText.getText().toString();
        String msg =  messageEditText.getText().toString();

        // Create a post.
        final ParseGarageSaleInfo garageSaleInfo = currentGarageSale;
        if(UIHelper.isNotEmpty(name)) {
            garageSaleInfo.setName(name);
        }
        if(UIHelper.isNotEmpty(phone)){
            garageSaleInfo.setPhoneNumber(phone);
        }
        if(UIHelper.isNotEmpty(email)) {
            garageSaleInfo.setEmail(email);
        }
        if(UIHelper.isNotEmpty(address)) {
            garageSaleInfo.setAddress(address);
        }
        if(UIHelper.isNotEmpty(msg)) {
            garageSaleInfo.setMessage(msg);
        }

        garageSaleInfo.setLocation(LocationUtil.generateParsePoint(LocationUtil.latestCurrentLocation));
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
                    Log.d("GarageSale", "Error saving Garage Sale:"+ garageSaleInfo.getObjectId() + ", with error:" + e);

                }
            }
        });
    }

    private void populateFields(ParseGarageSaleInfo garageSaleInfo, boolean readOnly)
    {
        if(readOnly)
        {
            nameEditText.setFocusable(false);
            phoneEditText.setFocusable(false);
            emailEditText.setFocusable(false);
            addressEditText.setFocusable(false);
            messageEditText.setFocusable(false);
            saveButton.setVisibility(View.GONE);
        }
        nameEditText.setText(garageSaleInfo.getName());
        phoneEditText.setText(garageSaleInfo.getPhoneNumber());
        emailEditText.setText(garageSaleInfo.getEmail());
        addressEditText.setText(garageSaleInfo.getAddress());
        messageEditText.setText(garageSaleInfo.getMessage());
    }

}
