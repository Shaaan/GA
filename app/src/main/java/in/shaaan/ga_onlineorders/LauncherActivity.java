package in.shaaan.ga_onlineorders;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class LauncherActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 7410;
    private static final String TAG = "LauncherActivity";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // signed in. proceed to app
            Intent intent = new Intent(this, AllOrders.class);
            startActivity(intent);
            finish();
        } else {
            // not signed in
            if (isOnline()) {
                startActivityForResult(
                        // Get an instance of AuthUI based on the default app
                        AuthUI.getInstance().createSignInIntentBuilder().setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                                .setIsSmartLockEnabled(true)
                                .setTheme(R.style.AppTheme)
                                .setAllowNewEmailAccounts(true)
                                .build(),
                        RC_SIGN_IN);
            } else {
                Toast.makeText(this, "Please connect to the internet and try again!", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent intent = new Intent(LauncherActivity.this, AllOrders.class);
                    startActivity(intent);
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    // not signed in
                }
            }
        };

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

        /*mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent intent = new Intent(LauncherActivity.this, AllOrders.class);
                    startActivity(intent);
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                    if (auth.getCurrentUser() != null) {
                                        // already signed in
                                        Log.d(TAG, "already logged in");
                                    } else {
                                        // not signed in
                                        startActivityForResult(
                                                // Get an instance of AuthUI based on the default app
                                                AuthUI.getInstance().createSignInIntentBuilder().setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                                                        .setIsSmartLockEnabled(false)
                                                        .setTheme(R.style.AppTheme)
                                                        .setAllowNewEmailAccounts(false)
                                                        .build(),
                                                RC_SIGN_IN);
                                    }
                }
            }
        };*/

    /*@Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }*/

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
