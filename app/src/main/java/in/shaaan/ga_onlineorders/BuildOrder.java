package in.shaaan.ga_onlineorders;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.Bind;
import butterknife.ButterKnife;


public class BuildOrder extends AppCompatActivity {

    private static final String TAG = "BuildOrder";
    private static final String REQUIRED = "This is required";
    @Bind(R.id.expProdList)
    TextView textView;

    private TextView mTextView;
    private AutoCompleteTextView mActv;
    private Button mButton;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_order);
        ButterKnife.bind(this);

/*
                int[] custCode = getResources().getIntArray(R.array.custCode);
                String[] custName = getResources().getStringArray(R.array.custName);
                List<Integer> cCode = new ArrayList<Integer>();
                List<String> cName = new ArrayList<String>(Arrays.asList(custName));
                HashMap<Integer, String> hashMap = new HashMap<>();
                for (int i = 0; i < custCode.length; i++) {
                    hashMap.put(custCode[i], custName[i]);
                }*/

        int layoutItemId = android.R.layout.simple_dropdown_item_1line;
        String[] drugArr = getResources().getStringArray(R.array.drugList);
        String[] custArr = getResources().getStringArray(R.array.custList);
        String[] strings = getResources().getStringArray(R.array.salesmen);
        List<String> salesmen = Arrays.asList(strings);
        List<String> drugList = Arrays.asList(drugArr);
        final List<String> custList = Arrays.asList(custArr);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, layoutItemId, drugList);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, layoutItemId, custList);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, layoutItemId, salesmen);

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocompleteview);
        AutoCompleteTextView autoCompleteTextView1 = (AutoCompleteTextView) findViewById(R.id.autocompleteviewExp);
        autoCompleteTextView1.setAdapter(adapter);
        autoCompleteTextView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        database.setPersistenceEnabled(true);

        mTextView = (TextView) findViewById(R.id.prodList);
        mActv = (AutoCompleteTextView) findViewById(R.id.custName);
        mActv.setAdapter(adapter1);
        mButton = (Button) findViewById(R.id.submit);

        String s = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String bh = custList.toString();
        String s1 = salesmen.toString();
        StringTokenizer stringTokenizer = new StringTokenizer(s, "@");
        String partyTemp = stringTokenizer.nextToken().trim();
        /*int i = Integer.parseInt(partyTemp.replaceAll("[\\D]", ""));
            if (custList.contains(i)) {
                mActv.setText(i);
                mActv.setEnabled(false);
            }*/

        if (s1.contains(s)) {
            mActv.setEnabled(true);
            firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            firebaseAnalytics.setUserProperty("salesman", "isSalesman");
        } else if (bh.contains(partyTemp)) {
            mActv.setText(partyTemp);
            mActv.setEnabled(false);
        } else {
            mActv.setText("Not a valid user");
            mActv.setEnabled(false);
        }
    }

    public void addProduct(View view) {
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocompleteview);
        EditText editText = (EditText) findViewById(R.id.quantity);
        String quantity = editText.getText().toString();
        String drug = autoCompleteTextView.getText().toString();
        TextView textView = (TextView) findViewById(R.id.prodList);
        if (drug.matches("")) {
            Snackbar.make(view, "You did not enter the product", Snackbar.LENGTH_LONG).show();
        } else if (quantity.matches("")) {
            Snackbar.make(view, "You did not add the quantity", Snackbar.LENGTH_LONG).show();
        } else
            textView.append("" + drug + "     " + quantity + "\n");
        autoCompleteTextView.getText().clear();
        editText.getText().clear();
        autoCompleteTextView.requestFocus();
    }

    public void addExpiry(View view) {
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocompleteviewExp);
        EditText editText = (EditText) findViewById(R.id.quantityExp);
        String quantity = editText.getText().toString();
        String drugExp = autoCompleteTextView.getText().toString();
        TextView textView = (TextView) findViewById(R.id.expProdList);
        if (drugExp.matches("")) {
            Snackbar.make(view, "You did not enter the product", Snackbar.LENGTH_LONG).show();
        } else if (quantity.matches("")) {
            Snackbar.make(view, "You did not add the quantity", Snackbar.LENGTH_LONG).show();
        } else
            textView.append(drugExp + "     " + quantity + "\n");
        autoCompleteTextView.getText().clear();
        editText.getText().clear();
        autoCompleteTextView.requestFocus();
    }

    public void sendOrder(View view) {
        submitOrder();
    }

    private void submitOrder() {
        final String customer = mActv.getText().toString();
        final String expProduct = textView.getText().toString();
        final String product = mTextView.getText().toString();

        if (TextUtils.isEmpty(customer)) {
            mActv.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(product)) {
            mTextView.setError(REQUIRED);
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
                reference.child("email").setValue(eMail);
                String key = reference.child("salesman").child(userId).child("orders").push().getKey();
                reference.child(key).child("custName").setValue(customer);
                reference.child(key).child("products").setValue(product);
                reference.child(key).child("expProducts").setValue(expProduct);
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
                reference.child(key).child("products").setValue(product);
                reference.child(key).child("expProducts").setValue(expProduct);
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
        mActv.setEnabled(enabled);
        mTextView.setEnabled(enabled);
        if (enabled) {
            mButton.setVisibility(View.VISIBLE);
        } else {
            mButton.setVisibility(View.GONE);
        }
    }

}
