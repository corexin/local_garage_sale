package au.com.simplesoftware.gc.util;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

/**
 * Created by steven on 18/06/2015.
 */
public class UIHelper {


    public static boolean isNotEmpty(String text)
    {
        return !(text == null || text.trim().length()==0);
    }

    public static void displayAlertDialog(Context ctx, String title, String message)
    {
        Log.e("GarageSale", "GooglePlay connection issue");
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title).setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
