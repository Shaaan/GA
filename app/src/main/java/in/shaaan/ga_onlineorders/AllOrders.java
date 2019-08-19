package in.shaaan.ga_onlineorders;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.shaaan.ga_onlineorders.pojo.OrderData;
import in.shaaan.ga_onlineorders.pojo.PostViewHolder;

public class AllOrders extends AppCompatActivity {

    private static final String TAG = "ViewActivity";
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.net_status)
    TextView netStatus;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        netStatus.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + user.getEmail());
                    syncCustList();
                    syncDrugList();
                    syncSalesmanList();
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String s = user.getEmail();
        String s1 = salesmen.toString();
        if (s1.contains(s)) {
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            firebaseAnalytics.setUserProperty("salesman", "isSalesman");
        }

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        StringTokenizer stringTokenizer = new StringTokenizer(email, "@");
        String salesmanP = stringTokenizer.nextToken().trim();

        Query query = GaFirebase.isCalled().getReference().child("").child("salesman").child(salesmanP);
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
                        intent.putExtra("expiry", orderData.getExpiry());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
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

    public void syncCustList() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference custRef = storage.getReference().child("custList.txt");
        custRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {

                File custFile = new File(getFilesDir().getPath(), "/custList.txt");
                if (!custFile.canRead()) {
                    try {
                        final File file = File.createTempFile("text", ".txt");
                        custRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.d("FileManager", file.getAbsolutePath());
                                File from = file.getAbsoluteFile();
                                File to = new File(getFilesDir(), "custList.txt");
                                from.renameTo(to);
                                Snackbar.make(coordinatorLayout, "Customer list not found. Downloading", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    } catch (IOException e) {
                        Log.e(TAG, "IOexception when writing file");
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String custListString = custFile.toString();
                        FileInputStream fileInputStream = new FileInputStream(custListString);
                        String checksum = MD5.md5(fileInputStream);
                        if (!checksum.equalsIgnoreCase(storageMetadata.getMd5Hash())) {
                            try {
                                final File file = File.createTempFile("text", ".txt");
                                custRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Log.d("FileManager", file.getAbsolutePath());
                                        File from = file.getAbsoluteFile();
                                        File to = new File(getFilesDir(), "custList.txt");
                                        from.renameTo(to);
                                        Snackbar.make(coordinatorLayout, "Customer list outdated. Downloading..", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            } catch (IOException e) {
                                Log.e(TAG, "IOexception when writing file");
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("OOPS", "Fatal...");
                        e.printStackTrace();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Metadata", "Failed to get metadata of custList");
                Snackbar.make(coordinatorLayout, "Update failed. No internet connection?", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void syncDrugList() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference drugRef = storage.getReference().child("drugList.txt");
        drugRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {

                File drugFile = new File(getFilesDir().getPath() + "/drugList.txt");
                if (!drugFile.canRead()) {
                    try {
                        final File file = File.createTempFile("text", ".txt");
                        drugRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.d("FileManager", file.getAbsolutePath());
                                File from = file.getAbsoluteFile();
                                File to = new File(getFilesDir(), "drugList.txt");
                                from.renameTo(to);
                                Snackbar.make(coordinatorLayout, "Drug list not found. Downloading", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    } catch (IOException e) {
                        Log.e(TAG, "IOexception when writing file");
                        e.printStackTrace();
                    }
                } else {
                    try {
                        try {
                            String drugListString = drugFile.toString();
                            FileInputStream fileInputStream = new FileInputStream(drugListString);
                            String checksum = MD5.md5(fileInputStream);
                            if (!checksum.equalsIgnoreCase(storageMetadata.getMd5Hash())) {
                                try {
                                    final File file = File.createTempFile("text", ".txt");
                                    drugRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Log.d("FileManager", file.getAbsolutePath());
                                            File from = file.getAbsoluteFile();
                                            File to = new File(getFilesDir(), "drugList.txt");
                                            from.renameTo(to);
                                            Snackbar.make(coordinatorLayout, "Drug list outdated. Downloading..", Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                } catch (IOException e) {
                                    Log.e(TAG, "IOexception when writing file");
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("OOPS", "Fatal...");
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Metadata", "Failed to get metadata");
//                Snackbar.make(coordinatorLayout, "Update failed. No internet connection?", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void syncSalesmanList() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference salesmanRef = storage.getReference().child("salesman.txt");
        salesmanRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {

                File salesmanFile = new File(getFilesDir().getPath(), "/salesman.txt");
                if (!salesmanFile.canRead()) {
                    try {
                        final File file = File.createTempFile("text", ".txt");
                        salesmanRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.d("FileManager", file.getAbsolutePath());
                                File from = file.getAbsoluteFile();
                                File to = new File(getFilesDir(), "salesman.txt");
                                from.renameTo(to);
                                Snackbar.make(coordinatorLayout, "Saleman list not found. Downloading", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    } catch (IOException e) {
                        Log.e(TAG, "IOexception when writing file");
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String salesmanListString = salesmanFile.toString();
                        FileInputStream fileInputStream = new FileInputStream(salesmanListString);
                        String checksum = MD5.md5(fileInputStream);
                        if (!checksum.equalsIgnoreCase(storageMetadata.getMd5Hash())) {
                            try {
                                final File file = File.createTempFile("text", ".txt");
                                salesmanRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Log.d("FileManager", file.getAbsolutePath());
                                        File from = file.getAbsoluteFile();
                                        File to = new File(getFilesDir(), "salesman.txt");
                                        from.renameTo(to);
                                        Snackbar.make(coordinatorLayout, "Salesman list outdated. Downloading..", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            } catch (IOException e) {
                                Log.e(TAG, "IOexception when writing file");
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Metadata", "Failed to get salesman metadata");
                Snackbar.make(coordinatorLayout, "Update failed. No internet connection?", Snackbar.LENGTH_LONG).show();
            }
        });
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
            Intent intent = new Intent(this, LauncherActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
