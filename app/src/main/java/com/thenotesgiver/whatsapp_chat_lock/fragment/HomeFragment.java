package com.thenotesgiver.whatsapp_chat_lock.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thenotesgiver.whatsapp_chat_lock.R;
import com.thenotesgiver.whatsapp_chat_lock.utils.DatabaseHandler;
import com.thenotesgiver.whatsapp_chat_lock.utils.HomeModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ActionMode.Callback {
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static List<HomeModel> ITEMS = new ArrayList<>();
    private ActionMode actionMode;
    private MyHomeRecyclerViewAdapter adapter;

    /* renamed from: db */
    private DatabaseHandler f79db;
    private boolean isMultiSelect = false;
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    private List<Integer> selectedIds = new ArrayList<>();

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(HomeModel homeModel);
    }

    public boolean onPrepareActionMode(ActionMode actionMode2, Menu menu) {
        return false;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_home_list, viewGroup, false);
        if (inflate instanceof RecyclerView) {
            Context context = inflate.getContext();
            this.recyclerView = (RecyclerView) inflate;
            int i = this.mColumnCount;
            if (i <= 1) {
                this.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                this.recyclerView.setLayoutManager(new GridLayoutManager(context, i));
            }
            ITEMS.clear();
            this.f79db = new DatabaseHandler(getActivity());
            ITEMS.addAll(this.f79db.getAllChatLock());
            this.adapter = new MyHomeRecyclerViewAdapter(ITEMS, this.mListener);
            this.recyclerView.setAdapter(this.adapter);
            this.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                /* class HomeFragment.C07731 */

                @Override 
                public void onItemClick(View view, int i) {
                    if (HomeFragment.this.isMultiSelect) {
                        HomeFragment.this.multiSelect(i);
                    }
                }

                @Override 
                public void onItemLongClick(View view, int i) {
                    if (!HomeFragment.this.isMultiSelect) {
                        HomeFragment.this.selectedIds = new ArrayList<>();
                        HomeFragment.this.isMultiSelect = true;
                        if (HomeFragment.this.actionMode == null) {
                            HomeFragment homeFragment = HomeFragment.this;
                            homeFragment.actionMode = homeFragment.getActivity().startActionMode(HomeFragment.this);
                        }
                    }
                    HomeFragment.this.multiSelect(i);
                }
            }));
        }
        return inflate;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyNewData() {
        ITEMS.clear();
        ITEMS.addAll(this.f79db.getAllChatLock());
        this.adapter.notifyDataSetChanged();
    }

  
    private void multiSelect(int i) {
        HomeModel item = this.adapter.getItem(i);
        if (item != null && this.actionMode != null) {
            if (this.selectedIds.contains(item.getId())) {
                this.selectedIds.remove(Integer.valueOf(item.getId()));
            } else {
                this.selectedIds.add(item.getId());
            }
            if (this.selectedIds.size() > 0) {
                this.actionMode.setTitle(String.valueOf(this.selectedIds.size()));
            } else {
                this.actionMode.setTitle("");
                this.actionMode.finish();
            }
            this.adapter.setChats(this.selectedIds);
        }
    }



    @Override // android.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            this.mListener = (OnListFragmentInteractionListener) context;
        }
    }

    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

    public boolean onCreateActionMode(ActionMode actionMode2, Menu menu) {
        actionMode2.getMenuInflater().inflate(R.menu.menu_select, menu);
        return true;
    }

    public boolean onActionItemClicked(ActionMode actionMode2, MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.action_delete) {
            return false;
        }
        AskOption().show();
        return true;
    }

    private AlertDialog AskOption() {
        /* class HomeFragment.DialogInterface$OnClickListenerC07742 */
        return new AlertDialog.Builder(getActivity()).setTitle("Delete").setMessage("Do you want to Delete").setPositiveButton("Delete", (dialogInterface, i) -> {
            for (HomeModel homeModel : HomeFragment.this.f79db.getAllChatLock()) {
                if (HomeFragment.this.selectedIds.contains(homeModel.getId())) {
                    HomeFragment.this.f79db.deleteChatInfo(homeModel);
                }
            }
            if (HomeFragment.this.actionMode != null) {
                HomeFragment.this.actionMode.setTitle("");
                HomeFragment.this.actionMode.finish();
            }
            HomeFragment.this.notifyNewData();
            dialogInterface.dismiss();
        }).setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();
    }

    public void onDestroyActionMode(ActionMode actionMode2) {
        this.actionMode = null;
        this.isMultiSelect = false;
        this.selectedIds = new ArrayList<>();
        this.adapter.setChats(new ArrayList<>());
    }
}