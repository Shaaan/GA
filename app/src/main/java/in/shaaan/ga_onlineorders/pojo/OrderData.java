package in.shaaan.ga_onlineorders.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by shant on 12-04-2017.
 */

@IgnoreExtraProperties
public class OrderData {

    String uid;
    String products;
    String orderBy;
    String expProd;
    private String custName;
    private String date;
    private String email;

    public OrderData() {

    }

    public OrderData(String uid, String expProd, String custName, String date, String orderBy, String products, String email) {
        this.uid = uid;
        this.email = email;
        this.custName = custName;
        this.date = date;
        this.products = products;
        this.orderBy = orderBy;
        this.expProd = expProd;
    }

    /*@Override
    public String toString() {
        return  "custName='" + custName + '\'' +
                "expProducts='" + expProd + '\'' +
                "products='" + products + '\'' +
                "]";
    }*/

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getOrder() {
        return products;
    }

    public void setOrder(String order) {
        this.products = order;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

}
