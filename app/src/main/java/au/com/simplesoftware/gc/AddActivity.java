package au.com.simplesoftware.gc;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import au.com.simplesoftware.gc.bo.ParseGarageSaleInfo;
import au.com.simplesoftware.gc.util.LocationUtil;


public class AddActivity extends AppCompatActivity {

    private ParseGeoPoint geoPoint;

    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText messageEditText;
    protected ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        nameEditText = (EditText) findViewById(R.id.name);
        phoneEditText = (EditText) findViewById(R.id.phone);
        addressEditText = (EditText) findViewById(R.id.address);
        messageEditText=(EditText)findViewById(R.id.message);

        Intent intent = getIntent();
        Location location = intent.getParcelableExtra(LocationUtil.INTENT_EXTRA_LOCATION);
        geoPoint = LocationUtil.generateParsePoint(location);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveGarageSale();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveGarageSale () {

        // Set up a progress dialog
        mProgressBar.setVisibility(View.VISIBLE);

        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String address =  addressEditText.getText().toString();
        String msg =  messageEditText.getText().toString();

        // Create a post.
        ParseGarageSaleInfo garageSaleInfo = new ParseGarageSaleInfo();
        garageSaleInfo.setLocation(geoPoint);
        garageSaleInfo.setName(name);
        garageSaleInfo.setPhoneNumber(phone);
        garageSaleInfo.setAddress(address);
        garageSaleInfo.setMessage(msg);
        garageSaleInfo.setUser(ParseUser.getCurrentUser());

        ParseACL acl = new ParseACL();
        // Give public read access
        acl.setPublicReadAccess(true);
        garageSaleInfo.setACL(acl);

        // Save the post
        garageSaleInfo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("GC","New Garage Sale Info Saved");
                mProgressBar.setVisibility(View.GONE);
                finish();
            }
        });
    }
}
