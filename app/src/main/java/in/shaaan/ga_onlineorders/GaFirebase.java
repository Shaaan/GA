package in.shaaan.ga_onlineorders;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by shant on 14-04-2017.
 */

public class GaFirebase extends Application {
    public static final int RC_SIGN_IN_HANDLER = 4211;
    public static final String SALESMEN = "salesmen";
    public static final String ALL_ORDERS = "allorders";
    private static FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseHandler;
    private FirebaseAuth mAuthHandler;
    private boolean isAuthenticated = false;

    private String mFbEmail = null;
    private String mFbUid = null;

    public static FirebaseDatabase isCalled() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }

        return mDatabase;
    }

    public void initFirebaseAuth() {
        if (!isAuthenticated) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                isAuthenticated = true;
            } else {
                authenticate();
            }
        }
    }

    private void authenticate() {
        if (isOnline()) {
//            startA
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        Toast.makeText(GaFirebase.this, "App connected to server", Toast.LENGTH_SHORT).show();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void initFirebaseData() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabaseHandler = FirebaseDatabase.getInstance().getReference();

    }

}
