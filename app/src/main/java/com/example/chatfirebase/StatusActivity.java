package com.example.chatfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    FirebaseUser fUser;

    Intent intent;
    EditText edtStatus;
    Button btnEditStatus;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Editing Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Waiting for edit your status");

        edtStatus = findViewById(R.id.edtStatus);
        btnEditStatus =findViewById(R.id.btnEditStatus);

        Intent intent = getIntent();
        edtStatus.setText(intent.getStringExtra("status"));

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = fUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        btnEditStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String status = edtStatus.getText().toString();
                if(status.isEmpty()){
                    dialog.dismiss();
                    Toast.makeText(StatusActivity.this, "Can not null", Toast.LENGTH_SHORT).show();
                }else {
                    mDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                dialog.dismiss();
                                Toast.makeText(StatusActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            }else {
                                dialog.dismiss();
                                Toast.makeText(StatusActivity.this, "There was some error in saving Changes.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
