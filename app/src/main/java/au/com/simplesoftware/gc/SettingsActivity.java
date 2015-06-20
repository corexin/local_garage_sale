package au.com.simplesoftware.gc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import java.util.Collections;
import java.util.List;

/**
 * Activity that displays the settings screen.
 */
public class SettingsActivity extends Activity {

  private List<Float> availableOptions = GarageSaleApplication.getConfigHelper().getSearchDistanceAvailableOptions();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_settings);

    float currentSearchDistance = GarageSaleApplication.getSearchDistance();
    if (!availableOptions.contains(currentSearchDistance)) {
      availableOptions.add(currentSearchDistance);
    }
    Collections.sort(availableOptions);

    // The search distance choices
    RadioGroup searchDistanceRadioGroup = (RadioGroup) findViewById(R.id.searchdistance_radiogroup);

    for (int index = 0; index < availableOptions.size(); index++) {
      float searchDistance = availableOptions.get(index);

      RadioButton button = new RadioButton(this);
      button.setId(index);
      button.setText(getString(R.string.settings_distance_format, (int)searchDistance));
      searchDistanceRadioGroup.addView(button, index);

      if (currentSearchDistance == searchDistance) {
        searchDistanceRadioGroup.check(index);
      }
    }

    // Set up the selection handler to save the selection to the application
    searchDistanceRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        GarageSaleApplication.setSearchDistance(availableOptions.get(checkedId));
      }
    });

    // Set up the log out button click handler
    Button logoutButton = (Button) findViewById(R.id.button_save_setting);
    logoutButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // Start and intent for the dispatch activity
        Intent intent = new Intent(SettingsActivity.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
      }
    });
  }
}
