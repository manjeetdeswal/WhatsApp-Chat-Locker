package com.thenotesgiver.whatsapp_chat_lock;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class PermissionsActivity extends AppCompatActivity {
    Button btnGrantPermissions;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Permissions");
        setContentView(R.layout.activity_permissions);
        this.btnGrantPermissions = findViewById(R.id.btnGrantPermissions);
        this.btnGrantPermissions.setOnClickListener(view -> {
            Intent intent = new Intent();
            String packageName = PermissionsActivity.this.getPackageName();
            if (!((PowerManager) PermissionsActivity.this.getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
                intent.setData(Uri.parse("package:" + packageName));
                PermissionsActivity.this.startActivity(intent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        onBackPressed();
        return true;
    }
}