package au.com.simplesoftware.gc.util;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by steven on 18/06/2015.
 */
public class UIHelper {

    public static ProgressDialog displayProgressDialog(Activity activity, String message)
    {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage(message);
        dialog.show();
        return dialog;
    }
}
