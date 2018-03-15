package in.shaaan.ga_onlineorders.pojo;

import android.support.annotation.Keep;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by shant on 12-04-2017.
 */

@IgnoreExtraProperties
@Keep
public class OrderData {

    private String custName;
    private String date;
    private String email;
    private String products;
    private String itemid;
    private String quantity;
    private String expiry;

    public OrderData() {

    }

    public OrderData(String custName, String email, String date, String products, String expiry) {
        this.email = email;
        this.custName = custName;
        this.date = date;
        this.products = products;
        this.expiry = expiry;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getItemId() {
        return itemid;
    }

    public void setItemId(String itemid) {
        this.itemid = itemid;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
