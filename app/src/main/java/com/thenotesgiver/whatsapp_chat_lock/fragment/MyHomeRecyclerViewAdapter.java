package com.thenotesgiver.whatsapp_chat_lock.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.thenotesgiver.whatsapp_chat_lock.R;
import com.thenotesgiver.whatsapp_chat_lock.utils.DatabaseHandler;
import com.thenotesgiver.whatsapp_chat_lock.utils.HomeModel;

import java.util.ArrayList;
import java.util.List;

public class MyHomeRecyclerViewAdapter extends RecyclerView.Adapter<MyHomeRecyclerViewAdapter.ViewHolder> {
    Context context;
    DatabaseHandler databaseHandler;
    private final HomeFragment.OnListFragmentInteractionListener mListener;
    private final List<HomeModel> mValues;
    private List<Integer> selectedIds = new ArrayList<>();

    public MyHomeRecyclerViewAdapter(List<HomeModel> list,HomeFragment.OnListFragmentInteractionListener onListFragmentInteractionListener) {
        this.mValues = list;
        this.mListener = onListFragmentInteractionListener;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        this.databaseHandler = new DatabaseHandler(viewGroup.getContext());
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_home, viewGroup, false));
    }

    public void setChats(List<Integer> list) {
        this.selectedIds = list;
        notifyDataSetChanged();
    }

    public void onBindViewHolder(final ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        viewHolder.mItem = this.mValues.get(i);
        boolean z = true;
        if (this.mValues.get(i).getUsername().substring(0, 1) != null) {
            viewHolder.apphabet_text.setText(this.mValues.get(i).getUsername().substring(0, 1));
        }
        if (this.mValues.get(i).getUsername() != null) {
            viewHolder.textView_name.setText(this.mValues.get(i).getUsername());
        }
        CheckBox checkBox = viewHolder.lock;
        if (this.mValues.get(i).getIsLock() > 0) {
            z = false;
        }
        checkBox.setChecked(z);
        viewHolder.lock.setTag(i);

        viewHolder.lock.setOnCheckedChangeListener((compoundButton, z1) -> {
            HomeModel homeModel = new HomeModel();
            homeModel.setId(MyHomeRecyclerViewAdapter.this.mValues.get(i).getId());
            if (z1) {
                Toast.makeText(MyHomeRecyclerViewAdapter.this.context, "Chat UnLocked", Toast.LENGTH_SHORT).show();
                homeModel.setIsLock(0);
            } else {
                Toast.makeText(MyHomeRecyclerViewAdapter.this.context, "Chat Locked", Toast.LENGTH_SHORT).show();
                homeModel.setIsLock(1);
            }
            homeModel.setUsername(MyHomeRecyclerViewAdapter.this.mValues.get(i).getUsername());
            homeModel.setIsToCheckLock(MyHomeRecyclerViewAdapter.this.mValues.get(i).getIsToCheckLock());
            MyHomeRecyclerViewAdapter.this.databaseHandler.updateChatLock(homeModel);
        });
        if (this.selectedIds.contains(this.mValues.get(i).getId())) {
            viewHolder.rlMain.setForeground(new ColorDrawable(ContextCompat.getColor(this.context, R.color.hint_color)));
        } else {
            viewHolder.rlMain.setForeground(new ColorDrawable(ContextCompat.getColor(this.context, R.color.transpherent)));
        }

        viewHolder.mView.setOnClickListener(view -> {
            if (MyHomeRecyclerViewAdapter.this.mListener != null) {
                MyHomeRecyclerViewAdapter.this.mListener.onListFragmentInteraction(viewHolder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mValues.size();
    }

    public HomeModel getItem(int i) {
        return this.mValues.get(i);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView apphabet_text;
        public final CheckBox lock;
        public HomeModel mItem;
        public final View mView;
        public LinearLayout rlMain;
        public final TextView textView_name;

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            this.apphabet_text =  view.findViewById(R.id.apphabet_text);
            this.textView_name = view.findViewById(R.id.textView_name);
            this.lock = view.findViewById(R.id.lock);
            this.rlMain = view.findViewById(R.id.llMain);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + (this.textView_name.getText()) + "'";
        }
    }
}