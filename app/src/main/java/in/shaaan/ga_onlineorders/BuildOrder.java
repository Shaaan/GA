package in.shaaan.ga_onlineorders;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import in.shaaan.ga_onlineorders.pojo.OrderData;


public class BuildOrder extends AppCompatActivity {

    private static final String TAG = "BuildOrder";
    private static final String REQUIRED = "This is required";
    static String partyTempHolder = "";
    @BindView(R.id.submit)
    FloatingActionButton submitFAB;
    @BindView(R.id.buildOrder_custName)
    AutoCompleteTextView customerNameAutocompleteTextView;
    @BindView(R.id.buildOrder_productName_autocompleteview)
    AutoCompleteTextView productNameAutoCompleteTextView;
    @BindView(R.id.buildOrder_addQuantity)
    EditText addQuantityEditText;
    @BindView(R.id.orderList)
    RecyclerView recyclerView;
    @BindView(R.id.addProduct)
    Button addProductButton;
    @BindView(R.id.view_scheme_value)
    TextView schemeView;
    @BindView(R.id.buildOrder_viewQuantity_value)
    TextView showStock;
    @BindView(R.id.view_mrp)
    TextView viewMrp;
    @BindView(R.id.buildOrder_viewQuantity_caption)
    View productQuantity;
    @BindView(R.id.scheme_linear)
    LinearLayout fullContent;
    @BindView(R.id.prodNameView)
    TextView prodView;
    @BindView(R.id.adjust_expiry)
    CheckBox expiryCheckbox;
    String finalQuantity;
    String finalParty;
    String PCode;
    String adjustExpiry;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference1;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private LinearLayoutManager linearLayoutManager;
    private List<OrderData> orderData = new ArrayList<>();
    private RecyclerAdapterFile mAdapter;
    private int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_order);
        ButterKnife.bind(this);
        final androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        showStock.setVisibility(View.GONE);
        productQuantity.setVisibility(View.GONE);
        addQuantityEditText.setEnabled(false);
        submitFAB.hide();

        // Set Dropdown
        int layoutItemId = android.R.layout.simple_dropdown_item_1line;

        // Create BufferedReaders for lists
//        BufferedReader cReader = null;
        BufferedReader dReader = null;
        BufferedReader sReader = null;
        List<String> custList = new ArrayList<>();
        List<String> drugList = new ArrayList<>();
        List<String> salesmanList = new ArrayList<>();
        try {
            /*String custListItem = getFilesDir().getPath() + "/custList.txt";
            cReader = new BufferedReader(new FileReader(custListItem));
            String cLI;
            while ((cLI = cReader.readLine()) != null) {
                custList.add(cLI);
            }*/
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
        productNameAutoCompleteTextView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerAdapterFile(orderData);
        recyclerView.setAdapter(mAdapter);

        mDatabaseReference1 = GaFirebase.isCalled().getReference().child("nodejs-data").child("Quant");
        mDatabaseReference1.keepSynced(true);

        customerNameAutocompleteTextView.setAdapter(adapter1);
        GaFirebase.isCalled();
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String salesmanListString = salesmanList.toString();
        String customerListString = custList.toString();
        StringTokenizer stringTokenizer = new StringTokenizer(currentUserEmail, "@");
        partyTempHolder = stringTokenizer.nextToken().trim();
        Log.d("PartyTemp", partyTempHolder);
        /*for (String item : custList) {
            if (item.toLowerCase().contains(partyTempHolder.toLowerCase())) {
                partyTempHolder = item;
                Log.d("Party is", partyTempHolder);
                break;
            }
        }*/

        mDatabaseReference = GaFirebase.isCalled().getReference().child("nodejs-data").child("Party");
        mDatabaseReference.keepSynced(true);

//
//      Check who the current user is and set parameters accordingly
//
        if (salesmanListString.contains(currentUserEmail)) {
            customerNameAutocompleteTextView.setEnabled(true);
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            firebaseAnalytics.setUserProperty("salesman", "isSalesman");
            showStock.setVisibility(View.VISIBLE);
            productQuantity.setVisibility(View.VISIBLE);
            submitFAB.show();
            salesman();
        } else {
            party();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("Signed in?", "Yes I did!" + user.getEmail());
            }
        };


        // Logic to check buildOrder_addQuantity
        productNameAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String pReqQuant = productNameAutoCompleteTextView.getText().toString();
                String[] parts = pReqQuant.split(" ");
                int n = parts.length;
                finalQuantity = parts[1];
                for (int x = 2; x < n; x++) {
                    finalQuantity = parts[parts.length - 1];
                }
                Log.d("Path", finalQuantity);
                Crashlytics.log(finalQuantity);
                productNameAutoCompleteTextView.setText(pReqQuant.replace(finalQuantity, ""));
                mDatabaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.d("QD", "Quan" + dataSnapshot);

                        if (finalQuantity != null) {

                            if (dataSnapshot.child("Stock").child(finalQuantity).child("TotalStock").getValue() != null) {
                                String s2 = dataSnapshot.child("Stock").child(finalQuantity).child("TotalStock").getValue().toString();
                                Log.d("FirebaseDatabase", s2);
                                showStock.setText(s2);
                                x = Integer.parseInt(s2);
                                if (x != 0 && x > 30) {
                                    addProductButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.quantity_full));
                                } else if (x != 0 && x < 30) {
                                    addProductButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.quantity_moderate));
                                } else if (x == 0) {
                                    addProductButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.quantity_nill));
                                }
                            } else {
                                Log.d("FirebaseDatabase", "Getting no buildOrder_addQuantity from Database");
                                addProductButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.quantity_nill));
                                showStock.setText("0");
                            }
                            if (dataSnapshot.child("Scheme").child(finalQuantity).child("Scheme").getValue() != null) {
                                String prodScheme = dataSnapshot.child("Scheme").child(finalQuantity).child("Scheme").getValue().toString();
                                schemeView.setText(prodScheme);
                            } else {
                                schemeView.setText("None");
                            }
                            if (dataSnapshot.child("Products").child(finalQuantity).child("MRP").getValue() != null) {
                                String mrp = dataSnapshot.child("Products").child(finalQuantity).child("MRP").getValue().toString();
                                viewMrp.setText(mrp);
                            } else {
                                viewMrp.setText("NA");
                            }
                            addQuantityEditText.setEnabled(true);
                            addQuantityEditText.requestFocus();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }


    public void party() {
        /*String enteredParty = customerNameAutocompleteTextView.getText().toString();
        String[] parts = enteredParty.split(" ");
        int n = parts.length;
        finalParty = parts[1];
        for (int x = 2; x < n; x++) {
            finalParty = finalParty + " " + parts[x];
        }
        finalParty = finalParty.replace(".", "_");
        Log.d("Path", finalParty);*/
        Log.d("DBPath", mDatabaseReference.toString());
        mDatabaseReference.child(partyTempHolder).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("DSnap", dataSnapshot.getValue().toString());
                    Crashlytics.log(PCode);
                    PCode = dataSnapshot.child("PartyId").getValue().toString();
                    String partyCode = dataSnapshot.child("PCode").getValue().toString();
                    String PartyName = dataSnapshot.child("PartyName").getValue().toString();
                    customerNameAutocompleteTextView.setText(partyCode + " " + PartyName);
                    Log.d("PCode from listner:", PCode);
                    productNameAutoCompleteTextView.requestFocus();
                    submitFAB.show();

                } else {
                    customerNameAutocompleteTextView.setText(partyTempHolder);
                    customerNameAutocompleteTextView.setEnabled(false);
                    Toasty.error(BuildOrder.this, "Invalid party code. Registered correctly?", Toast.LENGTH_LONG).show();
                    fullContent.setVisibility(View.GONE);
                    prodView.setText("Customer ID not found. Please contact Gayatri Agencies for help");
                    submitFAB.hide();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void salesman() {
        customerNameAutocompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String enteredParty = customerNameAutocompleteTextView.getText().toString();
                String[] parts = enteredParty.split(" ");
                finalParty = parts[0];
                Log.d("PartyC", finalParty);
//                mDatabaseReference = GaFirebase.isCalled().getReference().child("nodejs-data").child("Party");
//                mDatabaseReference.keepSynced(true);
                Log.d("DBPath", mDatabaseReference.toString());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (customerNameAutocompleteTextView != null) {
                            if (dataSnapshot.getValue() != null) {
                                Log.d("DSnap", dataSnapshot.getValue().toString());
                                PCode = dataSnapshot.child(finalParty).child("PCode").getValue().toString();
                                Log.d("PCode", PCode);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                productNameAutoCompleteTextView.requestFocus();
            }
        });
    }

    public void addProduct(View view) {
        String quantity = addQuantityEditText.getText().toString();
        String drug = productNameAutoCompleteTextView.getText().toString();
        /*if (PCode == null) {
            Toasty.error(BuildOrder.this, "Please re-enter party", Toast.LENGTH_LONG).show();
            customerNameAutocompleteTextView.setText("");
            customerNameAutocompleteTextView.requestFocus();
        }*/
        if (drug.isEmpty()) {
            Snackbar.make(view, "You did not enter the product", Snackbar.LENGTH_LONG).show();
            productNameAutoCompleteTextView.requestFocus();
        } else if (quantity.isEmpty()) {
            Snackbar.make(view, "You did not add the Quantity", Snackbar.LENGTH_LONG).show();
            addQuantityEditText.requestFocus();
        } else if (quantity.equalsIgnoreCase("0")) {
            Snackbar.make(view, "Quantity cannot be zero", Snackbar.LENGTH_LONG).show();
            addQuantityEditText.requestFocus();
        } else {
            if (finalQuantity != null) {
                mAdapter.addItem(getDataA());
            } else {
                Toasty.error(BuildOrder.this, "Error.. Please try again!", Toast.LENGTH_LONG).show();
            }
            productNameAutoCompleteTextView.getText().clear();
            addQuantityEditText.getText().clear();
            productNameAutoCompleteTextView.requestFocus();
            addProductButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            schemeView.setText("");
            viewMrp.setText("");
            showStock.setText("");
            finalQuantity = null;
        }

    }

    public OrderData getDataA() {
        OrderData instance = new OrderData();
        instance.setItemId(finalQuantity);
        String tmpProd = productNameAutoCompleteTextView.getText().toString();
        instance.setProducts(tmpProd.substring(tmpProd.indexOf(" ") + 1));
        instance.setQuantity(addQuantityEditText.getText().toString());
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
        String customerTmp = customerNameAutocompleteTextView.getText().toString();
        final String customerTmp1 = customerTmp.substring(customerTmp.indexOf(" "));
        final String customer = PCode + " " + customerTmp1;
//        final String cs = customer.substring(customer.indexOf(" ") + 1);
        Log.d("CS", customer);

        if (TextUtils.isEmpty(customerTmp)) {
            customerNameAutocompleteTextView.setError(REQUIRED);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy, hh:mm");
        final String date = simpleDateFormat.format(calendar.getTime());
        Log.d("TAG", date);

        // Disable the submitFAB button to prevent multiple orders
        setEditing(false);
        final String eMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//        final String userId = getUid();

        if (expiryCheckbox.isChecked()) {
            adjustExpiry = "Adjust Expiry";
        }
        List<OrderData> products = mAdapter.getItems();
        final StringBuilder builder = new StringBuilder();
        final StringBuilder builder1 = new StringBuilder();

        for (OrderData product : products) {
            builder1.append(String.format("%s %s\n", product.getProducts(), product.getQuantity()));
        }

        for (OrderData product : products) {
            builder.append(String.format("%s %s\n", product.getItemId() + " " + product.getProducts(), product.getQuantity()));
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                StringTokenizer stringTokenizer = new StringTokenizer(eMail, "@");
                String salesmen = stringTokenizer.nextToken().trim();
                // TODO: Switch to actual branch after development
                DatabaseReference reference = database.getReference("").child("salesman").child(salesmen).push();
                Log.d("SM", salesmen);
                reference.setValue(new OrderData(customerTmp1, eMail, date, builder1.toString(), adjustExpiry));
            }
        });
        thread.start();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // TODO: Switch to actual branch after development
                DatabaseReference reference = database.getReference("").child("autoInsOrders").push();
                reference.setValue(new OrderData(customer, eMail, date, builder.toString(), adjustExpiry));

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

        Toasty.success(this, "Order sent to Gayatri Agencies", Toast.LENGTH_LONG).show();
        finish();

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void setEditing(boolean enabled) {
        customerNameAutocompleteTextView.setEnabled(enabled);
        if (enabled) {
            submitFAB.show();
        } else {
            submitFAB.hide();
        }
    }

}