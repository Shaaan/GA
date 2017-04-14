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
    @Bind(R.id.prodList)
    TextView prodList;
    @Bind(R.id.submit)
    Button submit;
    @Bind(R.id.custName)
    AutoCompleteTextView completeTextView;
    @Bind(R.id.autocompleteview)
    AutoCompleteTextView autoCompleteTextView;
    @Bind(R.id.autocompleteviewExp)
    AutoCompleteTextView autoCompleteTextView1;
    @Bind(R.id.quantity)
    EditText editText;
    @Bind(R.id.quantityExp)
    EditText editText1;

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

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView1.setAdapter(adapter);

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
    }

    public void addProduct(View view) {
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
    }

    public void addExpiry(View view) {
        String quantity = editText1.getText().toString();
        String drugExp = autoCompleteTextView1.getText().toString();
        if (drugExp.matches("")) {
            Snackbar.make(view, "You did not enter the product", Snackbar.LENGTH_LONG).show();
        } else if (quantity.matches("")) {
            Snackbar.make(view, "You did not add the quantity", Snackbar.LENGTH_LONG).show();
        } else
            textView.append(drugExp + "     " + quantity + "\n");
        autoCompleteTextView1.getText().clear();
        editText1.getText().clear();
        autoCompleteTextView1.requestFocus();
    }

    public void sendOrder(View view) {
        submitOrder();
    }

    private void submitOrder() {
        final String customer = completeTextView.getText().toString();
        final String expProduct = textView.getText().toString();
        final String product = prodList.getText().toString();

        if (TextUtils.isEmpty(customer)) {
            completeTextView.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(product)) {
            prodList.setError(REQUIRED);
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
                reference.child(key).child("products").setValue(product);
                reference.child(key).child("email").setValue(eMail);
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
        completeTextView.setEnabled(enabled);
        prodList.setEnabled(enabled);
        if (enabled) {
            submit.setVisibility(View.VISIBLE);
        } else {
            submit.setVisibility(View.GONE);
        }
    }

}
