package in.shaaan.ga_onlineorders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.shaaan.ga_onlineorders.pojo.OrderData;


public class BuildOrder extends AppCompatActivity {

    private static final String TAG = "BuildOrder";
    private static final String REQUIRED = "This is required";
    @Bind(R.id.submit)
    FloatingActionButton submit;
    @Bind(R.id.scheme_checkbox)
    CheckBox checkBox;
    @Bind(R.id.custName)
    AutoCompleteTextView completeTextView;
    @Bind(R.id.autocompleteview)
    AutoCompleteTextView autoCompleteTextView;
    @Bind(R.id.quantity)
    EditText editText;
    @Bind(R.id.orderList)
    RecyclerView recyclerView;
    @Bind(R.id.addProduct)
    Button addProduct;
    @Bind(R.id.view_scheme)
    TextView schemeView;
    /*@Bind(R.id.delete_product)
    Button remProd;*/
    /*@Bind(R.id.prod_del)
    Button productDelete;*/
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private String k;
    private LinearLayoutManager linearLayoutManager;
    private List<OrderData> orderData = new ArrayList<>();
    private RecyclerAdapterFile mAdapter;
    private TextView scheme;
    private int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_order);
        ButterKnife.bind(this);
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int layoutItemId = android.R.layout.simple_dropdown_item_1line;
        String[] drugArr = getResources().getStringArray(R.array.drugList);
        String[] custArr = getResources().getStringArray(R.array.custList);
        String[] strings = getResources().getStringArray(R.array.salesmen);
        List<String> salesmen = Arrays.asList(strings);
        List<String> drugList = Arrays.asList(drugArr);
        final List<String> custList = Arrays.asList(custArr);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, layoutItemId, drugList);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, layoutItemId, custList);

        autoCompleteTextView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(recyclerView.getContext(), android.support.v7.widget.DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerAdapterFile(orderData);
        recyclerView.setAdapter(mAdapter);

        completeTextView.setAdapter(adapter1);
        GaFirebase.isCalled();
        String s = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String bh = custList.toString();
        String s1 = salesmen.toString();
        StringTokenizer stringTokenizer = new StringTokenizer(s, "@");
        String partyTemp = stringTokenizer.nextToken().trim();

        if (s1.contains(s)) {
            completeTextView.setEnabled(true);
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            firebaseAnalytics.setUserProperty("salesman", "isSalesman");
        } else if (bh.contains(partyTemp)) {
            completeTextView.setText(partyTemp);
            completeTextView.setEnabled(false);
        } else {
            completeTextView.setText("Not a valid user");
            completeTextView.setEnabled(false);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("Signed in?", "Yes I did!" + user.getEmail());
            }
        };

        //Logic to check quantity
//        String pReqQuant = autoCompleteTextView.getText().toString();
//        Query query = GaFirebase.isCalled().getReference().child("tempDB").child("Quant").child(pReqQuant);
//        String aQuantity = query.toString();
//        int availableQuantity = Integer.parseInt(aQuantity);


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final String pReqQuant = autoCompleteTextView.getText().toString();
                StringTokenizer stringTokenizer1 = new StringTokenizer(pReqQuant, "[");
                String finalProd = stringTokenizer1.nextToken().trim();
                Log.d("Path", finalProd);
                DatabaseReference reference = GaFirebase.isCalled().getReference().child("tempDB").child("Quant").child(finalProd);
                Log.d("DBPath", reference.toString());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.d("QD", "Quan" + dataSnapshot );

                        if (autoCompleteTextView != null) {

                            if (dataSnapshot.child("quantity").getValue() != null) {
                                String s2 = dataSnapshot.child("quantity").getValue().toString();
                                Log.d("FirebaseDatabase", s2);
                                x = Integer.parseInt(s2);
                                if (x != 0 && x > 30) {
                                    addProduct.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.quantity_full));
//                                    Toast.makeText(BuildOrder.this, "YOLO", Toast.LENGTH_SHORT).show();
                                } else if (x != 0 && x < 30) {
//                                    Toast.makeText(BuildOrder.this, "Quantity is less than 30", Toast.LENGTH_SHORT).show();
                                    addProduct.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.quantity_moderate));
                                } else if (x == 0) {
//                                    Toast.makeText(BuildOrder.this, "No stock available", Toast.LENGTH_SHORT).show();
                                    addProduct.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.quantity_nill));
                                }
                            } else {
                                Log.d("FirebaseDatabase", "Getting no quantity from Database");
                            }
                            if (dataSnapshot.child("scheme").getValue() != null) {
                                String prodScheme = dataSnapshot.child("scheme").getValue().toString();
                                schemeView.setText(prodScheme);
                            } else {
                                schemeView.setText("None");
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    public void addProduct(View view) {
        String quantity = editText.getText().toString();
        String drug = autoCompleteTextView.getText().toString();
        if (drug.matches("")) {
            Snackbar.make(view, "You did not enter the product", Snackbar.LENGTH_LONG).show();
        } else if (quantity.matches("")) {
            Snackbar.make(view, "You did not add the quantity", Snackbar.LENGTH_LONG).show();
        } else {
//            prodList.append("" + drug + "     " + quantity + "\n");

            mAdapter.addItem(getDataA());
        }
        autoCompleteTextView.getText().clear();
        editText.getText().clear();
        autoCompleteTextView.requestFocus();
        checkBox.setChecked(false);
    }

    public OrderData getDataA() {
        OrderData instance = new OrderData();
        instance.setProduct(autoCompleteTextView.getText().toString());
//        instance.setQuantity(editText.getText().toString());
        String quant = editText.getText().toString();
        if (checkBox.isChecked()) {
            StringBuilder stringBuilder = new StringBuilder(quant);
            stringBuilder.append(" - With Scheme");
            String finalQuant = stringBuilder.toString();
            instance.setQuantity(finalQuant);
        } else {
            instance.setQuantity(editText.getText().toString());
        }

        Log.d("I am doing something", "seriously?");
        return instance;
    }

    public void sendOrder(View view) {
        if(mAdapter.getItems().size() == 0) {
            Snackbar.make(view, "No products added in order", Snackbar.LENGTH_SHORT).show();
            return;
        }
        submitOrder();
    }

    private void submitOrder() {
        final String customer = completeTextView.getText().toString();

        if (TextUtils.isEmpty(customer)) {
            completeTextView.setError(REQUIRED);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy, hh:mm");
        final String date = simpleDateFormat.format(calendar.getTime());
        Log.d("TAG", date);

        // Disable the submit button to prevent multiple orders
        setEditing(false);
        Toast.makeText(this, "Sending Order..", Toast.LENGTH_LONG).show();
        final String eMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final String userId = getUid();

        List<OrderData> products = mAdapter.getItems();
        final StringBuilder builder = new StringBuilder();

        for(OrderData product: products) {
            builder.append(String.format("%s %s\n", product.getProduct(), product.getQuantity()));
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // TODO: Switch to actual branch after development
                DatabaseReference reference = database.getReference("tempDB").child("salesman").child(userId);
                reference.keepSynced(true);
                String key = reference.child("salesman").child(userId).child("orders").push().getKey();
                reference.child(key).child("custName").setValue(customer);
                reference.child(key).child("products").setValue(builder.toString());
                reference.child(key).child("email").setValue(eMail);
//                reference.child(key).child("expProducts").setValue(expProduct);
                reference.child(key).child("date").setValue(date);
                String blah = builder.toString();
                Log.d("Data", blah);
            }
        });
        thread.start();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // TODO: Switch to actual branch after development
                DatabaseReference reference = database.getReference("tempDB").child("allOrders");
                reference.keepSynced(true);
                String key = reference.push().getKey();
                reference.child(key).child("email").setValue(eMail);
                reference.child(key).child("date").setValue(date);
                reference.child(key).child("custName").setValue(customer);
                reference.child(key).child("products").setValue(builder.toString());
//                reference.child(key).child("expProducts").setValue(expProduct);
            }
        });
        t.start();

        setEditing(true);

        try {
            t.join();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        finish();

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void setEditing(boolean enabled) {
        completeTextView.setEnabled(enabled);
//        prodList.setEnabled(enabled);
        if (enabled) {
            submit.setVisibility(View.VISIBLE);
        } else {
            submit.setVisibility(View.GONE);
        }
    }

}