package com.example.chatfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextView edtFirtName, edtLastName, edtEmail, edtPassword, edtPassword1;
    Button btnRegister;
    Toolbar toolbar;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AnhXa();
        mAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setTitle("Loading");
        dialog.setMessage("Pleas waiting for register..");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firtname = edtFirtName.getText().toString();
                final String lastname = edtLastName.getText().toString();
                final String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString().trim();
                String comfimpassword1 = edtPassword1.getText().toString().trim();
                if (firtname.isEmpty()||lastname.isEmpty()||email.isEmpty()||password.isEmpty()||comfimpassword1.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Can not be empty!", Toast.LENGTH_SHORT).show();
                }else if (password.length()<6||comfimpassword1.length()<6){
                    Toast.makeText(RegisterActivity.this, "Password must be greater than 6 characters!", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(comfimpassword1)){
                    Toast.makeText(RegisterActivity.this, "Password does not match!", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.show();
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                dialog.dismiss();
                                FirebaseUser fuser = mAuth.getCurrentUser();
                                String uid = fuser.getUid();
                                mDatabase = FirebaseDatabase.getInstance().getReference("users").child(uid);
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("name", lastname + " " + firtname);
                                hashMap.put("email", email);
                                hashMap.put("status", "Hi, I am new use ChatFirebase!");
                                hashMap.put("img_avatar", "default");
                                hashMap.put("img_thumnail", "default");
                                hashMap.put("online", "true");
                                mDatabase.setValue(hashMap);
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                dialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Can not register!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void AnhXa() {
        edtFirtName = findViewById(R.id.edtFistName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtPassword1 = findViewById(R.id.edtPassword1);
        btnRegister = findViewById(R.id.btnRegister);
    }
}
