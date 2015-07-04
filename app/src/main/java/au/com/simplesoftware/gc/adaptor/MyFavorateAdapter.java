package au.com.simplesoftware.gc.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseQueryAdapter;

import au.com.simplesoftware.gc.R;
import au.com.simplesoftware.gc.bo.ParseMyFavoriate;

/**
 * Created by steven on 28/06/2015.
 */
public class MyFavorateAdapter extends ParseQueryAdapter<ParseMyFavoriate> {

    public MyFavorateAdapter(Context context) {
        super(context, new MyFavoriateAdaptorQueryFactory());
    }

    @Override
    public View getItemView(ParseMyFavoriate favoriate, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(getContext(), R.layout.drawer_list_item, null);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.drawer_item_image_id);
        imageView.setImageResource(R.drawable.list_bookmark32);

        TextView contentView = (TextView) view.findViewById(R.id.drawer_item_text_id);
        contentView.setText(favoriate.getGarageSale().getAddress());

        return view;
    }

}