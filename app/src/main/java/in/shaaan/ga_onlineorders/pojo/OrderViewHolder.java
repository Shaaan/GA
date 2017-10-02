package in.shaaan.ga_onlineorders.pojo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import in.shaaan.ga_onlineorders.R;

/**
 * Created by S on 29-09-2017.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder {
    public TextView prodName;
    public TextView quantity;
    public TextView scheme;
    View view;

    public OrderViewHolder(View itemView) {
        super(itemView);
        view = itemView;

        prodName = (TextView) itemView.findViewById(R.id.custName);
        quantity = (TextView) itemView.findViewById(R.id.quantity);
        scheme = (TextView) itemView.findViewById(R.id.scheme);
    }

    public void setProdName(String string) {
        prodName.setText(string);
    }

    public void setQuantity(String string1) {
        quantity.setText(string1);
    }

    public void setScheme(String string2) {
        scheme.setText(string2);
    }
}
