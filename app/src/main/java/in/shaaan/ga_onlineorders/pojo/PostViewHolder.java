package in.shaaan.ga_onlineorders.pojo;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.RecyclerView;

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

        custView = itemView.findViewById(R.id.view_cust_name);
        dateView = itemView.findViewById(R.id.view_date_time);
    }

    public void setCustView(String string) {
        custView.setText(string);
    }

    public void setDateView(String string1) {
        dateView.setText(string1);
    }

}
