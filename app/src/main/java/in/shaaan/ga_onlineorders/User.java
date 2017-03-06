package in.shaaan.ga_onlineorders;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by shaaan on 06/03/2017 for GAO.
 */

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;

    public User() {

    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
