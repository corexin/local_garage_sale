package au.com.simplesoftware.gc.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQueryAdapter;

import au.com.simplesoftware.gc.R;
import au.com.simplesoftware.gc.bo.ParseMyFavorite;

/**
 * Created by steven on 28/06/2015.
 */
public class MyFavoriateAdapter extends ParseQueryAdapter<ParseMyFavorite> {

    public MyFavoriateAdapter(Context context) {
        super(context, new MyFavoriateAdaptorQueryFactory());
    }

    @Override
    public View getItemView(ParseMyFavorite favoriate, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(getContext(), R.layout.drawer_list_item, null);
        }
        TextView contentView = (TextView) view.findViewById(R.id.drawer_item_text_id);

        try {
            favoriate.getGarageSale().fetch();
            contentView.setText(favoriate.getGarageSale().getAddress());
            ImageView imageView = (ImageView) view.findViewById(R.id.drawer_item_image_id);
            imageView.setImageResource(R.drawable.bookmark32);

        } catch (ParseException e) {
            Log.e("GarageSale" ,e.getMessage());
            return null;
        }

        return view;
    }

}