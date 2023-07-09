package com.thenotesgiver.whatsapp_chat_lock.utils;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;


import com.thenotesgiver.whatsapp_chat_lock.MainActivity;

import java.util.ArrayList;

public class AccessibilityServiceHandler extends AccessibilityService {
    public ArrayList<HomeModel> ITEMS = new ArrayList<>();
    Context context;
    public String currentAccessibilityPackage;

    /* renamed from: db */
    private DatabaseHandler databaseHandler;
    public boolean isWhatsappUnLockedLocked = false;

    public void onServiceConnected() {
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return START_STICKY;
    }

    @SuppressLint({"WrongConstant"})
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (accessibilityEvent != null && accessibilityEvent.getPackageName() != null) {
            try {
                this.currentAccessibilityPackage = accessibilityEvent.getPackageName().toString();
                if (accessibilityEvent.getPackageName().toString().equalsIgnoreCase("com.whatsapp") && accessibilityEvent.getPackageName().toString().equalsIgnoreCase("com.whatsapp") && accessibilityEvent.getClassName().toString().equalsIgnoreCase("android.widget.RelativeLayout")) {
                    String obj = accessibilityEvent.getText().toString();
                    if (obj.contains("[")) {
                        obj = obj.replace("[", "");
                    }
                    if (obj.contains(",")) {
                        String[] split = obj.split(",");
                        StringBuilder sb = new StringBuilder();
                        sb.append(split[0]);
                        this.ITEMS.clear();
                        this.ITEMS.addAll(this.databaseHandler.getAllChatLock());
                        if (MainActivity.isFromActivity) {
                            if (!contains(this.ITEMS, sb.toString())) {
                                HomeModel homeModel = new HomeModel();
                                homeModel.setUsername(sb.toString());
                                homeModel.setIsToCheckLock(0);
                                homeModel.setIsLock(1);
                                this.databaseHandler.addChatLockInfo(homeModel);
                                Toast.makeText(this.context, "Chat/Group added successfully.", 0).show();
                                startActivity(new Intent(this.context, MainActivity.class).addFlags(268435456).addFlags(335577088));
                                MainActivity.isFromActivity = false;
                            } else {
                                Toast.makeText(this.context, "Already exist", 0).show();
                                startActivity(new Intent(this.context, MainActivity.class).addFlags(268435456).addFlags(335577088));
                                MainActivity.isFromActivity = false;
                            }
                        } else if (contains(this.ITEMS, sb.toString()) && isLocked(this.ITEMS, sb.toString()) == 1) {
                            startActivity(new Intent(this.context, LockActivity.class).putExtra("fromService", "Chat").addFlags(335577088));
                        }
                    }
                }
                if (accessibilityEvent.getEventType() != 32 || !accessibilityEvent.getPackageName().toString().equalsIgnoreCase("com.whatsapp")) {
                    if (!accessibilityEvent.getPackageName().toString().equalsIgnoreCase(this.context.getPackageName()) && !accessibilityEvent.getPackageName().toString().equalsIgnoreCase("com.whatsapp")) {
                        this.isWhatsappUnLockedLocked = false;
                    }
                } else if (defaultSharedPreferences.getBoolean("app_lock", false) && !this.isWhatsappUnLockedLocked) {
                    this.isWhatsappUnLockedLocked = true;
                    startActivity(new Intent(this.context, LockActivity.class).putExtra("fromService", "Whatsapp").addFlags(335577088));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        this.databaseHandler = new DatabaseHandler(getApplicationContext());
    }

    /* access modifiers changed from: package-private */
    public boolean contains(ArrayList<HomeModel> arrayList, String str) {
        for (HomeModel homeModel : arrayList) {
            if (homeModel.getUsername().equals(str)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public int isLocked(ArrayList<HomeModel> arrayList, String str) {
        for (HomeModel next : arrayList) {
            if (next.getUsername().equals(str)) {
                Log.e("TAG", next.getIsLock() + "");
                return next.getIsLock();
            }
        }
        return 0;
    }

    public void onInterrupt() {
        Log.e("TAG", "onInterrupt");
    }
}