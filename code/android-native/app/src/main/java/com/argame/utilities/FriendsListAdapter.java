package com.argame.utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.argame.R;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewFriendItem> {
    private String[] mDataset;

    public static class ViewFriendItem extends RecyclerView.ViewHolder {
        public TextView textViewNickName;
        public TextView textViewCompleteName;

        public ViewFriendItem(View view) {
            super(view);
            textViewNickName = view.findViewById(R.id.text_view_friend_nickname);
            textViewCompleteName = view.findViewById(R.id.text_view_friend_complete_name);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendsListAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FriendsListAdapter.ViewFriendItem onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_friend, parent, false);

        ViewFriendItem vh = new ViewFriendItem(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewFriendItem holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textViewNickName.setText(mDataset[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}