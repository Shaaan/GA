package in.shaaan.ga_onlineorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewOrder extends AppCompatActivity {

    @Bind(R.id.vieworder_custName)
    TextView mCustName;
    @Bind(R.id.vieworder_date)
    TextView mDate;
    @Bind(R.id.vieworder_order)
    TextView mOrder;
    @Bind(R.id.vieworder_exp)
    TextView mExpiry;
    @Bind(R.id.vieworder_salesman)
    TextView mSalesman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String customer = intent.getStringExtra("custName");
        String order = intent.getStringExtra("order");
        String date = intent.getStringExtra("date");
        String by = intent.getStringExtra("by");
        String exp = intent.getStringExtra("exp");

        if (customer != null) {
            mCustName.setText(customer);
            mDate.setText(date);
            mOrder.setText(order);
            mExpiry.setText(exp);
            mSalesman.setText(by);
        } else {
            Toast.makeText(this, "Its null", Toast.LENGTH_SHORT).show();
        }

    }
}
