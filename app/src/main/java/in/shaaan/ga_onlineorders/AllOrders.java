package in.shaaan.ga_onlineorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.shaaan.ga_onlineorders.pojo.DividerItemDecoration;
import in.shaaan.ga_onlineorders.pojo.OrderData;
import in.shaaan.ga_onlineorders.pojo.PostViewHolder;

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
    private FirebaseRecyclerAdapter<OrderData, PostViewHolder> mAdapter;
    private LinearLayoutManager mManager;
//    private GaAdapter mAdapter;


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

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        mManager = new LinearLayoutManager(this);
        mManager.setStackFromEnd(true);
        mManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mManager);

        // Initialize Database
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("salesman").child(getUid());
        Query query = databaseReference1.orderByValue();

        // Views
//        mCustName = (TextView) findViewById(R.id.view_cust_name);
//        mDateTime = (TextView) findViewById(R.id.view_date_time);


        mAdapter = new FirebaseRecyclerAdapter<OrderData, PostViewHolder>(OrderData.class, R.layout.item_order, PostViewHolder.class, databaseReference1) {
            @Override
            public void populateViewHolder(final PostViewHolder postViewHolder, final OrderData orderData, final int position) {
                postViewHolder.setCustView(orderData.getCustName());
                postViewHolder.setDateView(orderData.getDate());

                postViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatabaseReference orderRef = getRef(position);
//                        Toast.makeText(AllOrders.this, "test" + custName, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AllOrders.this, ViewOrder.class);
                        intent.putExtra("custName", orderData.getCustName());
                        intent.putExtra("order", orderData.getProducts());
                        intent.putExtra("date", orderData.getDate());
                        intent.putExtra("by", orderData.getEmail());
                        intent.putExtra("exp", orderData.getExpProducts());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);


        /*recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));*/
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        /*
        mAdapter = new GaAdapter(this, databaseReference1);
        recyclerView.setAdapter(mAdapter);
*/
        /*mManager = new LinearLayoutManager(getApplicationContext());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);

        Query query = databaseReference1.child("salesman").child(getUid());
        mAdapter = new FirebaseRecyclerAdapter<OrderData, PostViewHolder>(OrderData.class, R.layout.item_order, PostViewHolder.class, query) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, OrderData model, int position) {
                final DatabaseReference databaseReference = getRef(position);

                final String key = databaseReference.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AllOrders.this, "Tes", Toast.LENGTH_LONG).show();
                    }
                });

                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AllOrders.this, "tttttt", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        recyclerView.setAdapter(mAdapter);*/
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
}
