package in.shaaan.ga_onlineorders;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.shaaan.ga_onlineorders.pojo.OrderData;
import in.shaaan.ga_onlineorders.pojo.PostViewHolder;

public class AllOrders extends AppCompatActivity {

    private static final String TAG = "ViewActivity";
    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.net_status)
    TextView netStatus;
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    /*@Bind(R.id.not_salesman)
    TextView notSalesman;
    @Bind(R.id.playStore_button)
    Button playButton;*/
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter mAdapter;
    private String checksumC;
    private String checksumD;
    private String checksumS;
    private String providedMD5D;
    private String providedMD5S;
    private String providedMD5C;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        netStatus.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + user.getEmail());
                    dataSync();
//                    Toast.makeText(AllOrders.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    // not signed in
                    Toast.makeText(AllOrders.this, "Please login to continue!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        };

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mManager.setStackFromEnd(true);
        mManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mManager);

        String[] strings = getResources().getStringArray(R.array.salesmen);
        List<String> salesmen = Arrays.asList(strings);
//        String s = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String s = user.getEmail();
        String s1 = salesmen.toString();
        if (s1.contains(s)) {
            floatingActionButton.setVisibility(View.VISIBLE);
//            playButton.setVisibility(View.GONE);
//            notSalesman.setVisibility(View.GONE);
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            firebaseAnalytics.setUserProperty("salesman", "isSalesman");
        } else {
            floatingActionButton.setVisibility(View.GONE);

        }

        Query query = GaFirebase.isCalled().getReference().child("").child("salesman").child(getUid());
        FirebaseRecyclerOptions<OrderData> options = new FirebaseRecyclerOptions.Builder<OrderData>()
                .setQuery(query, OrderData.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<OrderData, PostViewHolder>(options) {
            @Override
            public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_order, parent, false);

                return new PostViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(PostViewHolder postViewHolder, int position, final OrderData orderData) {
                postViewHolder.setCustView(orderData.getCustName());
                postViewHolder.setDateView(orderData.getDate());

                String rootPath = this.getRef(position).getRoot().toString();
                String mainPath = this.getRef(position).toString();
                final String orderRef = mainPath.replace(rootPath, "");

                postViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AllOrders.this, ViewOrder.class);
                        intent.putExtra("custName", orderData.getCustName());
                        intent.putExtra("order", orderData.getProducts());
                        intent.putExtra("date", orderData.getDate());
                        intent.putExtra("orderURL", orderRef);
                        intent.putExtra("by", orderData.getEmail());
                        intent.putExtra("exp", orderData.getExpProducts());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(recyclerView.getContext(), android.support.v7.widget.DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        if (isOnline()) {
            netStatus.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            netStatus.setVisibility(View.VISIBLE);
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void dataSync() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference custRef = storage.getReference().child("custList.xml");
        custRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                try {
                    final File file = File.createTempFile("text", ".xml");
                    custRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("FileManager", file.getAbsolutePath());
                            File from = file.getAbsoluteFile();
                            File to = new File(getFilesDir(), "custList.xml");
                            from.renameTo(to);
                        }
                    });
                } catch (IOException file) {
                    Log.d(TAG, "IOexception when writing file");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Metadata", "Failed to get metadata");
                Snackbar.make(coordinatorLayout, "Update failed. No internet connection?", Snackbar.LENGTH_LONG).show();
            }
        });

        final StorageReference drugRef = storage.getReference().child("drugList.xml");
        drugRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                try {
                    final File file = File.createTempFile("text", ".xml");
                    drugRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("FileManager", file.getAbsolutePath());
                            File from = file.getAbsoluteFile();
                            File to = new File(getFilesDir(), "drugList.xml");
                            from.renameTo(to);
                        }
                    });
                } catch (IOException file) {
                    Log.d(TAG, "IOexception when writing file");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Metadata", "Failed to get metadata");
                Snackbar.make(coordinatorLayout, "Update failed. No internet connection?", Snackbar.LENGTH_LONG).show();
            }
        });

        final StorageReference salesmanRef = storage.getReference().child("salesman.xml");
        salesmanRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                try {
                    final File file = File.createTempFile("text", ".xml");
                    salesmanRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("FileManager", file.getAbsolutePath());
                            File from = file.getAbsoluteFile();
                            File to = new File(getFilesDir(), "salesman.xml");
                            from.renameTo(to);
                        }
                    });
                } catch (IOException file) {
                    Log.d(TAG, "IOexception when writing file");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Metadata", "Failed to get metadata");
                Snackbar.make(coordinatorLayout, "Update failed. No internet connection?", Snackbar.LENGTH_LONG).show();
            }
        });
        Snackbar.make(coordinatorLayout, "Updated customer and products", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        mAdapter.stopListening();
    }


    public void buildOrder(View view) {
        Intent intent = new Intent(this, BuildOrder.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_orders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            return true;
            AuthUI.getInstance()
                    .signOut(this);
            finish();
        }
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
