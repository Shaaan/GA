package in.shaaan.ga_onlineorders;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by shant on 12-04-2017.
 */

@IgnoreExtraProperties
public class OrderData {

    String uid;
    String custName;
    String date;
    String products;
    String orderBy;
    String expProd;

    public OrderData() {

    }

    public OrderData(String uid, String expProd, String custName, String date, String orderBy, String products) {
        this.uid = uid;
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
    }

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
*/
}
