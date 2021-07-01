package com.example.chatfirebase.Fragment;


import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chatfirebase.Model.Request;
import com.example.chatfirebase.ProfileUserActivity;
import com.example.chatfirebase.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    RecyclerView mRecyclerView;
    View mView;

    private DatabaseReference mFriendReference;
    FirebaseUser fUser;
    String online_user_id;
    private DatabaseReference mUserReference;
    private DatabaseReference mFriendRequest;
    private DatabaseReference mRootRef;
    private DatabaseReference mFriendReqDatabase;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_request, container, false);
        mRecyclerView = mView.findViewById(R.id.rvRequest);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        online_user_id = fUser.getUid();

        mFriendReference = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(online_user_id);

        mUserReference = FirebaseDatabase.getInstance().getReference().child("users");

        //_____________ACCEPT___OR___CANCEL
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mRootRef = FirebaseDatabase.getInstance().getReference();

        // Inflate the layout for this fragment
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(mFriendReference, Request.class)
                .build();

        FirebaseRecyclerAdapter<Request, RequestViewHolder> adapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder requestViewHolder, int i, @NonNull Request request) {
                final String list_user_id = getRef(i).getKey();
                mFriendRequest = getRef(i).child("request_type").getRef();
                mFriendRequest.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String req_type = dataSnapshot.getValue().toString();
                            if (req_type.equals("received")){
                                mUserReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final String name = dataSnapshot.child("name").getValue().toString();
                                        String image_avatar = dataSnapshot.child("img_avatar").getValue().toString();
                                        String status = dataSnapshot.child("status").getValue().toString();

                                        requestViewHolder.mDisplayname.setText(name);
                                        requestViewHolder.mStatus.setText(status);
                                        if (image_avatar.equals("default")){
                                            requestViewHolder.mProfileImage.setImageResource(R.drawable.avatar);
                                        }else {
                                            Picasso.with(getContext()).load(image_avatar).placeholder(R.drawable.avatar).into(requestViewHolder.mProfileImage);
                                        }
                                        requestViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getContext(), ProfileUserActivity.class);
                                                intent.putExtra("user_id",list_user_id);
                                                startActivity(intent);
                                            }
                                        });
                                        //_________________ACCEPT_REQUEST_________________//
                                        requestViewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                                                Map friendsMap = new HashMap();
                                                friendsMap.put("Friends/" + online_user_id + "/" + list_user_id + "/date", currentDate);
                                                friendsMap.put("Friends/" + list_user_id + "/" + online_user_id + "/date", currentDate);

                                                friendsMap.put("Friend_req/" + online_user_id + "/" + list_user_id, null);
                                                friendsMap.put("Friend_req/" + list_user_id + "/" + online_user_id, null);

                                                mRootRef.updateChildren(friendsMap);
                                            }
                                        });

                                        //_________________CANCEL_REQUEST_________________//
                                        requestViewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mFriendReqDatabase.child(online_user_id).child(list_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mFriendReqDatabase.child(list_user_id).child(online_user_id).removeValue();
                                                    }
                                                });

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }else if (req_type.equals("sent")){
                                mUserReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final String name = dataSnapshot.child("name").getValue().toString();
                                        String image_avatar = dataSnapshot.child("img_avatar").getValue().toString();
                                        String status = dataSnapshot.child("status").getValue().toString();

                                        requestViewHolder.mDisplayname.setText(name);
                                        requestViewHolder.mStatus.setText(status);
                                        if (image_avatar.equals("default")){
                                            requestViewHolder.mProfileImage.setImageResource(R.drawable.avatar);
                                        }else {
                                            Picasso.with(getContext()).load(image_avatar).placeholder(R.drawable.avatar).into(requestViewHolder.mProfileImage);
                                        }
                                        requestViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getContext(), ProfileUserActivity.class);
                                                intent.putExtra("user_id",list_user_id);
                                                startActivity(intent);
                                            }
                                        });
                                        requestViewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mFriendReqDatabase.child(online_user_id).child(list_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mFriendReqDatabase.child(list_user_id).child(online_user_id).removeValue();
                                                    }
                                                });

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                requestViewHolder.btnAccept.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_all_user_layout, parent, false);
                RequestViewHolder viewHolder = new RequestViewHolder(view);
                return viewHolder;
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView mDisplayname, mStatus;
        CircleImageView mProfileImage;
        Button btnAccept, btnCancel;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mDisplayname = mView.findViewById(R.id.textviewDisplayName);
            mStatus = mView.findViewById(R.id.textviewStatus);
            mProfileImage = mView.findViewById(R.id.imageProfile);
            btnAccept = mView.findViewById(R.id.buttonAccept);
            btnCancel = mView.findViewById(R.id.buttonCancel);
        }
    }
}
