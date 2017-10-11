package in.shaaan.ga_onlineorders;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import butterknife.Bind;
import in.shaaan.ga_onlineorders.pojo.OrderData;

/**
 * Created by S on 02-10-2017.
 */

public class RecyclerAdapterFile extends RecyclerView.Adapter<RecyclerAdapterFile.MyViewHolder> {
    public List<OrderData> orderData;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView product, quantity, scheme;
        private Button button;

        public MyViewHolder(View view) {
            super(view);
//            view.setOnClickListener(this);
            button = (Button) view.findViewById(R.id.delete_product);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orderData.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
            /*view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    orderData.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    return true;
                }
            });*/
            product = (TextView) view.findViewById(R.id.view_prod_name);
            quantity = (TextView) view.findViewById(R.id.view_quantity_real);
            scheme = (TextView) view.findViewById(R.id.view_scheme);
        }

        public TextView getView() {
            return product;
        }

        @Override
        public void onClick(View view) {
            Log.d("onclick", "I am number" + getAdapterPosition());
            Snackbar.make(view, "Long press to delete the item", Snackbar.LENGTH_SHORT).show();
        }

    }

    public RecyclerAdapterFile(List<OrderData> orderData) {
        this.orderData = orderData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order1, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        OrderData orderData1 = orderData.get(position);
        holder.product.setText(orderData1.getProduct());
        holder.quantity.setText(orderData1.getQuantity());
        holder.scheme.setText(orderData1.getScheme());
    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public void addItem(OrderData item) {
        orderData.add(item);
        notifyItemInserted(orderData.size() - 1);
    }

    public List<OrderData> getItems() {
        return orderData;
    }
}
