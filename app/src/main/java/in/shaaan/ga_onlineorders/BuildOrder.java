package in.shaaan.ga_onlineorders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import in.shaaan.ga_onlineorders.pojo.OrderData;


public class BuildOrder extends AppCompatActivity {

    private static final String TAG = "BuildOrder";
    private static final String REQUIRED = "This is required";
    @Bind(R.id.submit)
    FloatingActionButton submit;
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
    @Bind(R.id.view_quantity)
    TextView showStock;
    @Bind(R.id.view_mrp)
    TextView viewMrp;
    @Bind(R.id.view_quantity_view)
    View quant;
    @Bind(R.id.scheme_linear)
    LinearLayout fullContent;
    @Bind(R.id.prodNameView)
    TextView prodView;
    String finalQ;
    String ItemID;
    String finalP;
    String PartyId;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    //    private String k;
    private LinearLayoutManager linearLayoutManager;
    private List<OrderData> orderData = new ArrayList<>();
    private RecyclerAdapterFile mAdapter;
    //    private TextView scheme;
    private int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_order);
        ButterKnife.bind(this);
        final android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        showStock.setVisibility(View.GONE);
        quant.setVisibility(View.GONE);
        editText.setEnabled(false);


        int layoutItemId = android.R.layout.simple_dropdown_item_1line;
//        String[] drugArr = getResources().getStringArray(R.array.drugList);
//        String[] custArr = getResources().getStringArray(R.array.custList);
//        String[] strings = getResources().getStringArray(R.array.salesmen);
//        List<String> salesmen = Arrays.asList(strings);
//        List<String> drugList = Arrays.asList(drugArr);
//        final List<String> custList = Arrays.asList(custArr);

        BufferedReader cReader = null;
        BufferedReader dReader = null;
        BufferedReader sReader = null;
        List<String> custList = new ArrayList<>();
        List<String> drugList = new ArrayList<>();
        List<String> salesmanList = new ArrayList<>();
        try {
            String custListItem = getFilesDir().getPath() + "/custList.txt";
            cReader = new BufferedReader(new FileReader(custListItem));
            String cLI;
            while ((cLI = cReader.readLine()) != null) {
                custList.add(cLI);
//                notifyAll();
//                in.close();
            }
            String drugListItem = getFilesDir().getPath() + "/drugList.txt";
            dReader = new BufferedReader(new FileReader(drugListItem));
            String dLI;
            while ((dLI = dReader.readLine()) != null) {
                drugList.add(dLI);
            }
            String salesmanListItem = getFilesDir().getPath() + "/salesman.txt";
            sReader = new BufferedReader(new FileReader(salesmanListItem));
            String sLI;
            while ((sLI = sReader.readLine()) != null) {
                salesmanList.add(sLI);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, layoutItemId, custList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, layoutItemId, drugList);
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
        String s1 = salesmanList.toString();
        String bh = custList.toString();
        StringTokenizer stringTokenizer = new StringTokenizer(s, "@");
        String partyTemp = stringTokenizer.nextToken().trim();

        if (s1.contains(s)) {
            completeTextView.setEnabled(true);
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            firebaseAnalytics.setUserProperty("salesman", "isSalesman");
            showStock.setVisibility(View.VISIBLE);
            quant.setVisibility(View.VISIBLE);
        } else if (bh.contains(partyTemp)) {
            completeTextView.setText(partyTemp);
            completeTextView.setEnabled(false);
        } else {
            completeTextView.setText("Not a valid user");
            completeTextView.setEnabled(false);
            submit.setVisibility(View.GONE);
            fullContent.setVisibility(View.GONE);
            prodView.setText("Wrong Customer ID. Please contact Gayatri Agencies for help");
        }

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("Signed in?", "Yes I did!" + user.getEmail());
            }
        };

        // Logic to check party id
        completeTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String enteredParty = completeTextView.getText().toString();
                String[] parts = enteredParty.split(" ");
                int n = parts.length;
                finalP = parts[1];
                for (int x = 2; x < n; x++) {
                    finalP = finalP + " " + parts[x];
                }
                finalP = finalP.replace(".", "_");
                Log.d("Path", finalP);
//                mDatabaseReference = GaFirebase.isCalled().getReference().child("nodejs-data").child("Party").child(finalP);
                mDatabaseReference = GaFirebase.isCalled().getReference().child("nodejs-data").child("Party");
                mDatabaseReference.keepSynced(true);
                Log.d("DBPath", mDatabaseReference.toString());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (completeTextView != null) {
//                            if (dataSnapshot.child(finalP).child("PartyId").getValue() != null) {
                            if (dataSnapshot.getValue() != null) {
                                Log.d("DSnap", dataSnapshot.getValue().toString());
                                PartyId = dataSnapshot.child(finalP).child("PartyId").getValue().toString();
                                Log.d("PartyId", PartyId);
//                                Toast.makeText(BuildOrder.this, PartyId, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                autoCompleteTextView.requestFocus();
            }
        });


//        Toasty.warning(BuildOrder.this, "Please wait while data is synced from the server..", Toast.LENGTH_SHORT).show();
        // Logic to check quantity
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String pReqQuant = autoCompleteTextView.getText().toString();
                String[] parts = pReqQuant.split(" ");
                int n = parts.length;
                finalQ = parts[1];
                for (int x = 2; x < n; x++) {
                    finalQ = finalQ + " " + parts[x];
                }
                finalQ = finalQ.replace(".", "_");
                Log.d("Path", finalQ);
//                mDatabaseReference = GaFirebase.isCalled().getReference().child("nodejs-data").child("Quant").child(finalQ);
                mDatabaseReference = GaFirebase.isCalled().getReference().child("nodejs-data").child("Quant");
//                Log.d("DBPath", mDatabaseReference.toString());
                mDatabaseReference.keepSynced(true);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.d("QD", "Quan" + dataSnapshot);

                        if (autoCompleteTextView != null) {

                            if (dataSnapshot.child(finalQ).child("TotalStock").getValue() != null) {
                                String s2 = dataSnapshot.child(finalQ).child("TotalStock").getValue().toString();
                                Log.d("FirebaseDatabase", s2);
                                showStock.setText(s2);
                                x = Integer.parseInt(s2);
                                if (x != 0 && x > 30) {
                                    addProduct.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.quantity_full));
                                } else if (x != 0 && x < 30) {
//                                    Toast.makeText(BuildOrder.this, "Quantity is less than 30", Toast.LENGTH_SHORT).show();
                                    addProduct.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.quantity_moderate));
                                } else if (x == 0) {
//                                    Toast.makeText(BuildOrder.this, "No stock available", Toast.LENGTH_SHORT).show();
                                    addProduct.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.quantity_nill));
                                }
                            } else {
                                Log.d("FirebaseDatabase", "Getting no quantity from Database");
                                addProduct.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.quantity_nill));
                                showStock.setText("0");
                            }
                            if (dataSnapshot.child(finalQ).child("Scheme").getValue() != null) {
                                String prodScheme = dataSnapshot.child(finalQ).child("Scheme").getValue().toString();
                                schemeView.setText(prodScheme);
                            } else {
                                schemeView.setText("None");
                            }
                            if (dataSnapshot.child(finalQ).child("MRP").getValue() != null) {
                                String mrp = dataSnapshot.child(finalQ).child("MRP").getValue().toString();
                                viewMrp.setText(mrp);
                            } else {
                                viewMrp.setText("NA");
                            }
                            if (dataSnapshot.child(finalQ).child("ItemDetailId").getValue() != null) {
                                ItemID = dataSnapshot.child(finalQ).child("ItemDetailId").getValue().toString();
                            }
                            editText.setEnabled(true);
                            editText.requestFocus();
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
            autoCompleteTextView.requestFocus();
        } else if (quantity.matches("")) {
            Snackbar.make(view, "You did not add the quantity", Snackbar.LENGTH_LONG).show();
            editText.requestFocus();
        } else {
//            prodList.append("" + drug + "     " + quantity + "\n");

            mAdapter.addItem(getDataA());
            autoCompleteTextView.getText().clear();
            editText.getText().clear();
            autoCompleteTextView.requestFocus();
//            checkBox.setChecked(false);
            addProduct.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            schemeView.setText("");
            viewMrp.setText("");
            showStock.setText("");
        }

    }

    public OrderData getDataA() {
        OrderData instance = new OrderData();
        instance.setItemId(ItemID);
        String tmpProd = autoCompleteTextView.getText().toString();
        instance.setProducts(tmpProd.substring(tmpProd.indexOf(" ") + 1));
        instance.setQuantity(editText.getText().toString());
//        Log.d("I am doing something", "seriously?");
        return instance;
    }

    public void sendOrder(View view) {
        if (mAdapter.getItems().size() == 0) {
            Snackbar.make(view, "No products added in order", Snackbar.LENGTH_SHORT).show();
            return;
        }
        submitOrder();
    }

    private void submitOrder() {
        String customerTmp = completeTextView.getText().toString();
        String customerTmp1 = customerTmp.substring(customerTmp.indexOf(" "));
        final String customer = PartyId + " " + customerTmp1;
        final String cs = customer.substring(customer.indexOf(" ") + 1);
        Log.d("CS", customer);

//        Toast.makeText(BuildOrder.this, customer, Toast.LENGTH_SHORT).show();

        if (TextUtils.isEmpty(customerTmp)) {
            completeTextView.setError(REQUIRED);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy, hh:mm");
        final String date = simpleDateFormat.format(calendar.getTime());
        Log.d("TAG", date);

        // Disable the submit button to prevent multiple orders
        setEditing(false);
//        Toast.makeText(this, "Sending Order..", Toast.LENGTH_LONG).show();
        Toasty.success(this, "Sending Order..", Toast.LENGTH_LONG).show();
        final String eMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final String userId = getUid();

        List<OrderData> products = mAdapter.getItems();
        final StringBuilder builder = new StringBuilder();

        for (OrderData product : products) {
            builder.append(String.format("%s %s\n", product.getItemId() + " " + product.getProducts(), product.getQuantity()));
//            builder.append();
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // TODO: Switch to actual branch after development
                DatabaseReference reference = database.getReference("tempDB").child("salesman").child(userId).push();
                reference.setValue(new OrderData(customer, eMail, date, builder.toString()));
            }
        });
        thread.start();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // TODO: Switch to actual branch after development
                DatabaseReference reference = database.getReference("tempDB").child("autoInsOrders").push();
                reference.setValue(new OrderData(customer, eMail, date, builder.toString()));
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
        if (enabled) {
            submit.setVisibility(View.VISIBLE);
        } else {
            submit.setVisibility(View.GONE);
        }
    }

}