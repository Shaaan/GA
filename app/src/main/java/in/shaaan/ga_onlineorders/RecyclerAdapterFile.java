package in.shaaan.ga_onlineorders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.shaaan.ga_onlineorders.pojo.OrderData;

/**
 * Created by S on 02-10-2017.
 */

public class RecyclerAdapterFile extends RecyclerView.Adapter<RecyclerAdapterFile.MyViewHolder> {
    public List<OrderData> orderData;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product, quantity, scheme;
        public View parent;
        public MyViewHolder(View view) {
            super(view);
            product = (TextView) view.findViewById(R.id.view_prod_name);
            quantity = (TextView) view.findViewById(R.id.view_quantity_real);
            scheme = (TextView) view.findViewById(R.id.view_scheme);
            parent = view;
        }
        public TextView getView() {
            return product;
        }
    }

    public RecyclerAdapterFile(List<OrderData> orderData) {
        this.orderData = orderData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order1, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        OrderData orderData1 = orderData.get(position);
        holder.product.setText(orderData1.getProduct());
        holder.quantity.setText(orderData1.getQuantity());
        holder.scheme.setText(orderData1.getScheme());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderData.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
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
