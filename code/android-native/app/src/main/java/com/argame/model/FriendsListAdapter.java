package com.argame.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.argame.R;
import com.argame.model.data_structures.user_data.UserInterface;

public class FriendsListAdapter extends ListAdapter<UserInterface, FriendsListAdapter.ViewFriendItem> {
    public class ViewFriendItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textViewNickName;
        public TextView textViewCompleteName;
        public ImageView imageViewSelected;

        public ViewFriendItem(View view) {
            super(view);
            textViewNickName = view.findViewById(R.id.text_view_friend_nickname);
            textViewCompleteName = view.findViewById(R.id.text_view_friend_complete_name);
            imageViewSelected = view.findViewById(R.id.image_view_selected);
            imageViewSelected.setVisibility(View.INVISIBLE);
            if(itemsSelectable)
                view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (getAdapterPosition() == RecyclerView.NO_POSITION)
                return;

            notifyItemChanged(selected_position);
            selected_position = getAdapterPosition();
            notifyItemChanged(selected_position);
            onItemClick.run();
        }
    }

    private int selected_position = -1;
    private boolean itemsSelectable;
    private Runnable onItemClick;

    public FriendsListAdapter() {
        this(false, null);
    }

    public FriendsListAdapter(boolean itemsSelectable, Runnable onItemClick) {
        super(UserInterface.DIFF_CALLBACK);
        this.itemsSelectable = itemsSelectable;
        this.onItemClick = onItemClick;

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

        if(selected_position == position)
            holder.imageViewSelected.setVisibility(View.VISIBLE);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.getCurrentList().size();
    }
}