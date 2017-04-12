package in.shaaan.ga_onlineorders;

/**
 * Created by shant on 12-04-2017.
 */

public class OrderData {

    String uid;
    String custName;
    String date;
    String order;
    String orderBy;

    public OrderData() {

    }

    public OrderData(String uid, String custName, String date, String orderBy, String order) {
        this.uid = uid;
        this.custName = custName;
        this.date = date;
        this.order = order;
        this.orderBy = orderBy;
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
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
