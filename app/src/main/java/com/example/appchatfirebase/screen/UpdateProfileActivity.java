package com.example.appchatfirebase.screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appchatfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText edtUsername, edtPhone, edtAddress;
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        initView();
        setData();

    }

    private void initView() {
        edtUsername = (EditText) findViewById(R.id.username_id);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtAddress = (EditText) findViewById(R.id.edtAddress);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
    }

    private void setData() {

        Intent intent = getIntent();

        String name = intent.getStringExtra("username");
        String address = intent.getStringExtra("address");
        String phone = intent.getStringExtra("phone");

        edtUsername.setText(name);
        edtAddress.setText(address);
        edtPhone.setText(phone);

       // update(name, address, phone);

    }

    private void update () {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        String name = edtUsername.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", name);
        hashMap.put("address", address);
        hashMap.put("phone", phone);

        ref.updateChildren(hashMap);

        // set null edt
        edtUsername.setText("");
        edtAddress.setText("");
        edtPhone.setText("");

        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }

}
