package in.shaaan.ga_onlineorders;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class About extends AppCompatActivity {
    final String TAG = "AboutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

    }

    /*public void sync(View view) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference reference = storage.getReference().child("customers/custList.xml");
        Toast.makeText(About.this, "Sync Started", Toast.LENGTH_SHORT).show();
        reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                if (storageMetadata.getUpdatedTimeMillis() != 0) {
                    String updTime = Long.toString(storageMetadata.getUpdatedTimeMillis());
                    Log.d("Metadata", updTime);
                    try {
                        final File file = File.createTempFile("text", ".xml");
                        reference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.d("FileManager", file.getAbsolutePath());
                                String path = file.getAbsolutePath();
                                File from = file.getAbsoluteFile();
                                File to = new File(getFilesDir(), "custList.xml");
                                from.renameTo(to);
                                Toast.makeText(About.this, "Customers updated successfully" + taskSnapshot, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }catch (IOException file) {
                        Log.d(TAG, "IOexception when writing file");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Metadata", "Failed to get metadata");
                Toast.makeText(About.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}
