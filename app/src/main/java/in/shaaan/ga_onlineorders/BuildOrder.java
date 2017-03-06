package in.shaaan.ga_onlineorders;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;


public class BuildOrder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_order);

        int layoutItemId = android.R.layout.simple_dropdown_item_1line;
        String[] drugArr = getResources().getStringArray(R.array.drugList);
        String[] custArr = getResources().getStringArray(R.array.custList);
        List<String> drugList = Arrays.asList(drugArr);
        List<String> custList = Arrays.asList(custArr);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, layoutItemId, drugList);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, layoutItemId, custList);

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocompleteview);
        autoCompleteTextView.setAdapter(adapter);

        AutoCompleteTextView autoCompleteTextView1 = (AutoCompleteTextView) findViewById(R.id.custName);
        autoCompleteTextView1.setAdapter(adapter1);
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

    public void sendOrder(View view) {

    }

}
