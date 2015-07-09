package au.com.simplesoftware.gc.listener;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by steven on 7/07/2015.
 */
public class DrawerToggleListener extends ActionBarDrawerToggle {
    private AppCompatActivity activity;
    public DrawerToggleListener(AppCompatActivity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        this.activity = activity;
    }

    /** Called when a drawer has settled in a completely closed state. */
    public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
        activity.getSupportActionBar().setTitle("Garage Sale");
        activity.supportInvalidateOptionsMenu();
    }

    /** Called when a drawer has settled in a completely open state. */
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        activity.getSupportActionBar().setTitle("My Posts");
        activity.supportInvalidateOptionsMenu();
    }
}
