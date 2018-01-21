package in.shaaan.ga_onlineorders;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import in.shaaan.ga_onlineorders.pojo.OrderData;

/**
 * Created by S on 02-10-2017.
 */

public class RecyclerAdapterFile extends RecyclerView.Adapter<RecyclerAdapterFile.MyViewHolder> {
    public List<OrderData> orderData;

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
        holder.product.setText(orderData1.getProducts());
        holder.quantity.setText(orderData1.getQuantity());
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView product, quantity;
        private Button button;

        public MyViewHolder(View view) {
            super(view);
            button = view.findViewById(R.id.delete_product);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orderData.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

            product = view.findViewById(R.id.view_prod_name);
            quantity = view.findViewById(R.id.view_quantity_real);
//            scheme = view.findViewById(R.id.view_scheme);
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
}
