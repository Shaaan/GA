package in.shaaan.ga_onlineorders.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by shant on 12-04-2017.
 */

@IgnoreExtraProperties
public class OrderData {

    private String custName;
    private String date;
    private String email;
    private String products;
    private String itemid;
    private String quantity;
    private String CompanyId;
    private String YearId;

    public OrderData() {

    }

    public OrderData(String custName, String email, String date, String products, String CompanyId, String YearId) {
        this.email = email;
        this.custName = custName;
        this.date = date;
        this.products = products;
        this.CompanyId = CompanyId;
        this.YearId = YearId;
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

    public String getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(String CompanyId) {
        this.CompanyId = CompanyId;
    }

    public String getYearId() {
        return YearId;
    }

    public void setYearId(String YearId) {
        this.YearId = YearId;
    }
}
