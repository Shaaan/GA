package in.shaaan.ga_onlineorders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.shaaan.ga_onlineorders.pojo.OrderData;
import in.shaaan.ga_onlineorders.pojo.OrderViewHolder;


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
    /*@Bind(R.id.prod_del)
    Button productDelete;*/
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private String k;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_order);
        ButterKnife.bind(this);
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar3);
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
                Log.d("Signed in?", "We logged in" + user.getEmail());
            }
        };


        recyclerView.setHasFixedSize(false);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        final DatabaseReference databaseReference = GaFirebase.isCalled().getReference().child("tempTree").child(getUid());

        FirebaseRecyclerAdapter<OrderData, OrderViewHolder> adapter2 = new FirebaseRecyclerAdapter<OrderData, OrderViewHolder>(OrderData.class, R.layout.item_order1, OrderViewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, OrderData model, int position) {
                viewHolder.setProdName(model.getProduct());
                viewHolder.setQuantity(model.getQuantity());
                viewHolder.setScheme(model.getScheme());

                String string = this.getRef(position).getRoot().toString();
                String s2 = this.getRef(position).toString();
                final String s3 = s2.replace(string, "");

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(BuildOrder.this, "Long press a product to remove it", Toast.LENGTH_LONG).show();
                    }
                });

                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        DatabaseReference databaseReference1 = GaFirebase.isCalled().getReference(s3);
                        databaseReference1.removeValue();
                        return true;
                    }
                });
            }
        };
        recyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter2);
        final String uid = getUid();

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(BuildOrder.this, "Change this", Toast.LENGTH_SHORT).show();
                DatabaseReference databaseReference1 = GaFirebase.isCalled().getReference("salesman");
                final String prodName = autoCompleteTextView.toString();
                /*databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String prodQuantity = dataSnapshot.getValue(String.class);
                        if (prodName != null) {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
//                Query query = databaseReference1.orderByChild("nodeData").equalTo(prodName);
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        String prodQuantity = dataSnapshot.getValue(String.class);
//                        int quant = Integer.parseInt(prodQuantity);
//                        if (quant > 3) {
//                            addProduct.setBackgroundColor(getResources().getColor(R.color.com_facebook_button_background_color));
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

            }
        });

        final ArrayList<String> prodList = new ArrayList<String>();

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String quantity = editText.getText().toString();
                String drug = autoCompleteTextView.getText().toString();
                mDatabaseReference = FirebaseDatabase.getInstance().getReference("tempTree").child(uid);
                String k = mDatabaseReference.child("tempTree").child(uid).child("order").push().getKey();
                mDatabaseReference.keepSynced(true);
                if (checkBox.isChecked()) {
                    mDatabaseReference.child(k).child("scheme").setValue("With Scheme");
                }
                if (drug.matches("")) {
                    Snackbar.make(v, "You did not enter the product", Snackbar.LENGTH_LONG).show();
                } else if (quantity.matches("")) {
                    Snackbar.make(v, "You did not add the quantity", Snackbar.LENGTH_LONG).show();
                } else {
                    /*mDatabaseReference.child(k).child("product").setValue(drug);
                    mDatabaseReference.child(k).child("quantity").setValue(quantity);*/
                    autoCompleteTextView.getText().clear();
                    editText.getText().clear();
                    checkBox.setChecked(false);
                    autoCompleteTextView.requestFocus();

                }
                Log.d("I am pressed", k);
            }
        });

        /*productDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DatabaseReference database = GaFirebase.isCalled().getReference(k);
                Log.d("Value", k);
            }
        });*/

    }

    /*public void addProduct(View view) {
        String quantity = editText.getText().toString();
        String drug = autoCompleteTextView.getText().toString();
        if (drug.matches("")) {
            Snackbar.make(view, "You did not enter the product", Snackbar.LENGTH_LONG).show();
        } else if (quantity.matches("")) {
            Snackbar.make(view, "You did not add the quantity", Snackbar.LENGTH_LONG).show();
        } else
            prodList.append("" + drug + "     " + quantity + "\n");
        autoCompleteTextView.getText().clear();
        editText.getText().clear();
        autoCompleteTextView.requestFocus();
    }*/


   /* public void productDel(View view) {
        String uid = getUid();
        String rem = mDatabaseReference.child("tempTree").child(uid).toString();
//        DatabaseReference databaseReference1 = GaFirebase.isCalled().getReference("tempTree").child(uid);
        Log.d("correct?", rem);
//        databaseReference1.removeValue();
    }*/

    public void sendOrder(View view) {
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

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("salesman").child(userId);
                reference.keepSynced(true);
                String key = reference.child("salesman").child(userId).child("orders").push().getKey();
                reference.child(key).child("custName").setValue(customer);
//                reference.child(key).child("products").setValue(product);
                reference.child(key).child("email").setValue(eMail);
//                reference.child(key).child("expProducts").setValue(expProduct);
                reference.child(key).child("date").setValue(date);
            }
        });
        thread.start();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("allOrders");
                reference.keepSynced(true);
                String key = reference.push().getKey();
                reference.child(key).child("email").setValue(eMail);
                reference.child(key).child("date").setValue(date);
                reference.child(key).child("custName").setValue(customer);
//                reference.child(key).child("products").setValue(product);
//                reference.child(key).child("expProducts").setValue(expProduct);
            }
        });
        t.start();

        setEditing(true);
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