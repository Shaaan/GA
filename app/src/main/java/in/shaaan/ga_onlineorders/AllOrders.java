package in.shaaan.ga_onlineorders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AllOrders extends AppCompatActivity {

    // [END declare_auth]
    private static final String TAG = "ViewActivity";
    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;
    // [START declare_auth]
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference1;

    private TextView mCustName;
    private TextView mDateTime;
    private TextView mOrderBy;
    private TextView mOrder;
    private RecyclerView recyclerView;
    private GaAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                    Snackbar.make(findViewById(R.id.coordinatorLayout), "Logged in successfully", Snackbar.LENGTH_LONG);
                    Toast.makeText(AllOrders.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    // not signed in
                    Toast.makeText(AllOrders.this, "Please login to continue!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        };

        // Initialize Database
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("salesman").child(getUid());

        // Views
        mCustName = (TextView) findViewById(R.id.view_cust_name);
        mDateTime = (TextView) findViewById(R.id.view_date_time);
        mOrderBy = (TextView) findViewById(R.id.view_order_by);
        mOrder = (TextView) findViewById(R.id.view_order);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OrderData orderData = dataSnapshot.getValue(OrderData.class);
                mCustName.setText(orderData.getCustName());
                mDateTime.setText(orderData.getDate());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference1.addValueEventListener(eventListener);
        mAdapter = new GaAdapter(this, databaseReference1);
        recyclerView.setAdapter(mAdapter);
    }

    public void buildOrder(View view) {
        Intent intent = new Intent(this, BuildOrder.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_orders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            return true;
            AuthUI.getInstance()
                    .signOut(this);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private static class GaViewHolder extends RecyclerView.ViewHolder {
        public TextView custName;
        public TextView dateTime;

        public GaViewHolder(View itemView) {
            super(itemView);

            custName = (TextView) itemView.findViewById(R.id.view_cust_name);
            dateTime = (TextView) itemView.findViewById(R.id.view_date_time);
        }
    }

    private static class GaAdapter extends RecyclerView.Adapter<GaViewHolder> {
        ArrayList<OrderData> orderDatas;
        private Context context;
        private DatabaseReference databaseReference;
        private ChildEventListener mchildEventListener;
        private ValueEventListener mListner;
        private List<String> stringList = new ArrayList<>();
        private List<String> strings = new ArrayList<>();

        public GaAdapter(final Context context1, DatabaseReference reference) {
            context = context1;
            databaseReference = reference;

            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    orderDatas.add(dataSnapshot.getValue(OrderData.class));

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            reference.addChildEventListener(childEventListener);
            mchildEventListener = childEventListener;
        }

        @Override
        public GaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_order, parent, false);
            return new GaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GaViewHolder holder, int position) {
            OrderData orderData1 = orderDatas.get(position);
            holder.custName.setText(orderData1.getCustName());
            holder.dateTime.setText(orderData1.getDate());
        }

        @Override
        public int getItemCount() {
            return orderDatas.size();
        }
    }
}
