package com.argame.activities.main.fragments.fragment_friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.argame.R;
import com.argame.model.data_structures.user_data.UserInterface;

public class FriendsListAdapter extends ListAdapter<UserInterface, FriendsListAdapter.ViewFriendItem> {
    public static class ViewFriendItem extends RecyclerView.ViewHolder {
        public TextView textViewNickName;
        public TextView textViewCompleteName;

        public ViewFriendItem(View view) {
            super(view);
            textViewNickName = view.findViewById(R.id.text_view_friend_nickname);
            textViewCompleteName = view.findViewById(R.id.text_view_friend_complete_name);
        }
    }

    public FriendsListAdapter() {
        super(UserInterface.DIFF_CALLBACK);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FriendsListAdapter.ViewFriendItem onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_friend_layout, parent, false);

        ViewFriendItem vh = new ViewFriendItem(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewFriendItem holder, int position) {
        UserInterface friend = this.getItem(position);
        holder.textViewNickName.setText(friend.getNickname());
        holder.textViewCompleteName.setText(friend.getName() + " " + friend.getSurname());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.getCurrentList().size();
    }
}