package com.example.appchatfirebase.screen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchatfirebase.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgSetting;
    private CircleImageView imgAvt;
    private TextView tvUsername, tvAddress, tvPhone, tvEmail;

    private ProgressDialog progressDialog;
    private DatabaseReference reference;
    private FirebaseUser user;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private String name, address, phone, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

         user = FirebaseAuth.getInstance().getCurrentUser();

        initView();
        event();
        setData();

    }

    private void initView() {

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imgSetting = (ImageView) findViewById(R.id.img_setting_id);

        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setMessage("Loading...");
        registerForContextMenu(imgSetting);

        // avt user current
        imgAvt = (CircleImageView) findViewById(R.id.img_avt_id);
        tvUsername = (TextView) findViewById(R.id.tv_name);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvEmail = (TextView) findViewById(R.id.tv_email);

    }

    private void event() {
        imgAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
    }

    private void openCamera () {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Context Menu");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
    }

    private void setData() {
        // get info user from intent
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        String img = intent.getStringExtra("img");
        address = intent.getStringExtra("address");
        phone = intent.getStringExtra("phone");
        email = intent.getStringExtra("email");


        Picasso.get().load(img).into(imgAvt);
        tvUsername.setText(name);
        tvEmail.setText(email);
        tvAddress.setText(address);
        tvPhone.setText(phone);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_id) {
            MainActivity.loginEditor = MainActivity.loginSharedPreferences.edit();
            MainActivity.loginEditor.clear();
            MainActivity.loginEditor.apply();

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return true;
        }else {
            Intent intent = new Intent(getApplicationContext(), UpdateProfileActivity.class);
            intent.putExtra("username", name);
            intent.putExtra("address", address);
            intent.putExtra("phone", phone);
            startActivity(intent);
            return true;
        }
    }


    private void upLoadImages () {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                            downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("img", url);

                                    reference.updateChildren(hashMap);

                                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "that bai", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            upLoadImages();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
