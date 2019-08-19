package in.shaaan.ga_onlineorders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewOrder extends AppCompatActivity {

    @BindView(R.id.vieworder_custName)
    TextView mCustName;
    @BindView(R.id.vieworder_date)
    TextView mDate;
    @BindView(R.id.vieworder_order)
    TextView mOrder;
    @BindView(R.id.vieworder_salesman)
    TextView mSalesman;
    @BindView(R.id.delete_order)
    Button deleteButton;
    @BindView(R.id.vieworder_expiry)
    TextView expiryAdj;

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
        String expiry = intent.getStringExtra("expiry");
        final String orderURL = intent.getStringExtra("orderURL");

        StringTokenizer stringTokenizer = new StringTokenizer(by, "@");
        String salesman = stringTokenizer.nextToken().trim();

        if (customer != null && date != null && order != null && by != null) {
            mCustName.setText(customer);
            mDate.setText(date);
            mOrder.setText(order);
            mSalesman.setText(salesman);
        }
        if (expiry != null) {
            expiryAdj.setText("Expiry Adjusted in this invoice");
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = GaFirebase.isCalled().getReference(orderURL);
                databaseReference.removeValue();
                Log.d("Removed,", orderURL);
                finish();
            }
        });
    }
}
