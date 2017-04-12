package in.shaaan.ga_onlineorders.pojo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by shant on 12-04-2017.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {
    public TextView custView;
    public TextView dateView;

    public PostViewHolder(View itemView) {
        super(itemView);

        custView = (TextView) itemView.findViewById(android.R.id.text1);
        dateView = (TextView) itemView.findViewById(android.R.id.text2);
    }

    public void setCustView(String string) {
        custView.setText(string);
    }

    public void setDateView(String string1) {
        dateView.setText(string1);
    }

}
