package com.example.chatfirebase.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatfirebase.GetTimeAgo;
import com.example.chatfirebase.Model.Messages;
import com.example.chatfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    private List<Messages> mMessageList;
    private Context mContext;
    private DatabaseReference mUserDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mAuthCurrentUser;
    int viewType;

    public MessageAdapter(List<Messages> mMessageList, Context mContext) {
        this.mContext = mContext;
        this.mMessageList = mMessageList;

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.viewType = viewType;
        if (viewType == MSG_RIGHT){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_single_layout_right ,parent, false);
            return new MessageViewHolder(v);
        }else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_single_layout ,parent, false);
            return new MessageViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        Messages c = mMessageList.get(position);

        String from_user = c.getFrom();
        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("img_avatar").getValue().toString();

                holder.displayName.setText(name);

                Picasso.with(holder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.avatar).into(holder.profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        long time = c.getTime();
        GetTimeAgo getTimeAgo = new GetTimeAgo();

        String messageTime = getTimeAgo.getTimeAgo(time, mContext);
        if(message_type.equals("text")) {
            holder.timeText.setText(messageTime);
            holder.messageText.setText(c.getMessage());
            holder.messageImage.setVisibility(View.INVISIBLE);
        } else {
            holder.timeText.setText(c.getTime()+"");
            holder.messageText.setVisibility(View.INVISIBLE);
            Picasso.with(holder.profileImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.avatar).into(holder.messageImage);
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText, timeText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_layout);
            displayName = (TextView) itemView.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) itemView.findViewById(R.id.message_image_layout);
            timeText = (TextView) itemView.findViewById(R.id.time_text_layout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        mAuthCurrentUser = mAuth.getCurrentUser();
        String current_user_id = mAuthCurrentUser.getUid();
        if (mMessageList.get(position).getFrom().equals(current_user_id)){
            return MSG_RIGHT;
        }else {
            return MSG_LEFT;
        }
    }
}
