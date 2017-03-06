package in.shaaan.ga_onlineorders;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shaaan on 06/03/2017 for GAO.
 */

public class Post {

    public String uid;
    public String salesman;
    public String customerName;
    public String orderProducts;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {

    }
    public Post(String uid, String salesman, String custName, String order) {
        this.uid = uid;
        this.salesman = salesman;
        this.customerName = custName;
        this.orderProducts = order;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("salesman", salesman);
        result.put("custName", customerName);
        result.put("order", orderProducts);

        return result;
    }
}
