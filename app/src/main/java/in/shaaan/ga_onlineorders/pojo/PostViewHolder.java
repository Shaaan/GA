package in.shaaan.ga_onlineorders.pojo;

import android.support.annotation.Keep;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import in.shaaan.ga_onlineorders.R;

/**
 * Created by shant on 12-04-2017.
 */

@Keep
public class PostViewHolder extends RecyclerView.ViewHolder {
    public TextView custView;
    public TextView dateView;
    View mView;

    public PostViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

        custView = (TextView) itemView.findViewById(R.id.view_cust_name);
        dateView = (TextView) itemView.findViewById(R.id.view_date_time);
    }

    public void setCustView(String string) {
        custView.setText(string);
    }

    public void setDateView(String string1) {
        dateView.setText(string1);
    }

}
