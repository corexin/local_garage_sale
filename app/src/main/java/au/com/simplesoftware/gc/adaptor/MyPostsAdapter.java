package au.com.simplesoftware.gc.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseQueryAdapter;

import au.com.simplesoftware.gc.R;
import au.com.simplesoftware.gc.bo.ParseGarageSaleInfo;

/**
 * Created by steven on 28/06/2015.
 */
public class MyPostsAdapter extends ParseQueryAdapter<ParseGarageSaleInfo> {

    public MyPostsAdapter(Context context) {
        super(context, new MyPostsAdaptorQueryFactory());
    }

    @Override
    public View getItemView(ParseGarageSaleInfo post, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(getContext(), R.layout.drawer_list_item, null);
        }

        TextView contentView = (TextView) view.findViewById(R.id.drawer_item_text_id);
        contentView.setText(post.getAddress());

        return view;
    }

}