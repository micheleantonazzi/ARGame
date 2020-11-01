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
        public ImageView imageViewConnectionStatus;

        public ViewFriendItem(View view) {
            super(view);
            textViewNickName = view.findViewById(R.id.text_view_friend_nickname);
            textViewCompleteName = view.findViewById(R.id.text_view_friend_complete_name);
            imageViewSelected = view.findViewById(R.id.image_view_selected);
            imageViewConnectionStatus = view.findViewById(R.id.image_view_connection_status);
            imageViewSelected.setVisibility(View.INVISIBLE);
            if(itemsSelectable)
                view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (getAdapterPosition() == RecyclerView.NO_POSITION)
                return;

            synchronized (selectedPositionMonitor) {
                notifyItemChanged(selectedPosition);
                selectedPosition = getAdapterPosition();
                notifyItemChanged(selectedPosition);
                if (onItemClick != null)
                    onItemClick.run();
            }
        }
    }

    private final Object selectedPositionMonitor = new Object();
    private Integer selectedPosition = -1;
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
        holder.imageViewConnectionStatus.setBackgroundResource(friend.getOnlineStatus() == 0 ? R.drawable.circle_offline : R.drawable.circle_online);

        if(selectedPosition == position)
            holder.imageViewSelected.setVisibility(View.VISIBLE);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.getCurrentList().size();
    }

    public void setOnItemClick(Runnable onItemClick) {
        this.onItemClick = onItemClick;
    }

    public UserInterface getSelectedItem() {
        if(!this.itemsSelectable || this.selectedPosition == -1)
            return null;

        synchronized (this.selectedPositionMonitor) {
            return this.getItem(this.selectedPosition);
        }
    }
}