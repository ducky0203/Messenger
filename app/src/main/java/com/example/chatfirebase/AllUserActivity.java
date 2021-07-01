package com.example.chatfirebase;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatfirebase.Model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class AllUserActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rvAllUser;

    DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        rvAllUser = findViewById(R.id.rvAllUser);

        userDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        rvAllUser.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(userDatabase, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, UserViewHolder> adapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Users users) {
                holder.tvDisplayNameUser.setText(users.getName());
                holder.tvStatusUser.setText(users.getStatus());
                if (users.getImg_avatar().equals("default")){
                    holder.userImage.setImageResource(R.drawable.avatar);
                }else {
                    Picasso.with(AllUserActivity.this).load(users.getImg_avatar()).placeholder(R.drawable.avatar).into(holder.userImage);
                }

                final String user_id = getRef(position).getKey();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentProfile = new Intent(AllUserActivity.this, ProfileUserActivity.class);
                        intentProfile.putExtra("user_id", user_id);
                        startActivity(intentProfile);
                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
                UserViewHolder viewHolder = new UserViewHolder(view);
                return viewHolder;
            }
        };
        rvAllUser.setAdapter(adapter);
        adapter.startListening();

    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        TextView tvDisplayNameUser, tvStatusUser;
        CircleImageView userImage;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            tvDisplayNameUser = itemView.findViewById(R.id.tvDisplayNameUser);
            tvStatusUser = itemView.findViewById(R.id.tvStatusUser);
        }
    }

}
