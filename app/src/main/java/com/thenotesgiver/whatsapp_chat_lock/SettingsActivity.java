package com.thenotesgiver.whatsapp_chat_lock;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.beautycoder.pflockscreen.viewmodels.PFPinCodeViewModel;
import com.thenotesgiver.whatsapp_chat_lock.utils.LockActivity;

import java.util.Objects;


public class SettingsActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        public static boolean manufacturer() {
            String lowerCase = Build.MANUFACTURER.toLowerCase();
            return lowerCase.contains("oppo") || lowerCase.contains("vivo") || lowerCase.contains("xiaomi") || lowerCase.contains("letv");
        }

        public static void autoStartPermission(Context context) {
            String lowerCase = Build.MANUFACTURER.toLowerCase();
            if (lowerCase.contains("oppo")) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity");
                    if (!getPackageManagerActivity(context, intent)) {
                        intent = new Intent();
                        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");
                        if (!getPackageManagerActivity(context, intent)) {
                            intent = new Intent();
                            intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity");
                            if (!getPackageManagerActivity(context, intent)) {
                                return;
                            }
                        }
                    }
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (lowerCase.contains("vivo")) {
                Intent intent2 = new Intent();
                intent2.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                if (!getPackageManagerActivity(context, intent2)) {
                    intent2 = new Intent();
                    intent2.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
                    if (!getPackageManagerActivity(context, intent2)) {
                        intent2 = new Intent();
                        intent2.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
                        if (!getPackageManagerActivity(context, intent2)) {
                            return;
                        }
                    }
                }
                context.startActivity(intent2);
            } else if (lowerCase.contains("xiaomi")) {
                Intent intent3 = new Intent();
                intent3.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                if (getPackageManagerActivity(context, intent3)) {
                    context.startActivity(intent3);
                }
            } else {
                Intent intent4 = new Intent();
                if (lowerCase.contains("letv")) {
                    intent4.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                    getPackageManagerActivity(context, intent4);
                    return;
                }
                if (lowerCase.contains("honor")) {
                    new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
                }
                if (!getPackageManagerActivity(context, intent4)) {
                    try {
                        context.startActivity(intent4);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }

        @SuppressLint({"WrongConstant"})
        public static boolean getPackageManagerActivity(Context context, Intent intent) {
            return context.getPackageManager().queryIntentActivities(intent, 65536).size() > 0;
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String str) {
            setPreferencesFromResource(R.xml.pref_settings, str);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
            super.onViewCreated(view, bundle);
            setData();
        }

        public void setData() {
            PreferenceCategory preferenceCategory = findPreference("category");
            Preference findPreference = findPreference("auto_start");
            if (!(findPreference == null || preferenceCategory == null || manufacturer())) {
                preferenceCategory.removePreference(findPreference);
            }
            if (findPreference != null) {
                findPreference.setOnPreferenceClickListener(preference -> {
                    SettingsFragment.autoStartPermission(SettingsFragment.this.getActivity());
                    return false;
                });
            }
            // androidx.preference.Preference.OnPreferenceClickListener
            findPreference("battery_optimize").setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent();
                FragmentActivity activity = SettingsFragment.this.getActivity();
                Objects.requireNonNull(activity).getClass();
                String packageName = activity.getPackageName();
                Context context = SettingsFragment.this.getContext();
                context.getClass();
                if (!((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(packageName)) {
                    intent.setAction("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
                    intent.setData(Uri.parse("package:" + packageName));
                    SettingsFragment.this.startActivity(intent);
                    return false;
                }
                SettingsFragment.this.showBatteryDialog();
                return false;
            });
            SwitchPreference switchPreference = findPreference("app_lock_permission");
            switchPreference.setChecked(MainActivity.isAccessibilitySettingsOn(requireActivity()));
            switchPreference.setOnPreferenceChangeListener((preference, obj) -> {
                SettingsFragment.this.showAccessibilitySettings();
                return false;
            });

            findPreference("changepin").setOnPreferenceClickListener(preference -> {
                SettingsFragment.this.showChangePinDialog();
                return false;
            });
        }

        public void showChangePinDialog() {

            new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.app_name)).setMessage(getString(R.string.changepinDialog)).setIcon(R.mipmap.ic_launcher).setCancelable(true).setNegativeButton(getResources().getString(R.string.f69no), new DialogInterface.OnClickListener() {


                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {
                new PFPinCodeViewModel().delete();
                SettingsFragment settingsFragment = SettingsFragment.this;
                settingsFragment.startActivity(new Intent(settingsFragment.getActivity(), LockActivity.class));
            }).show();
        }

        public void showBatteryDialog() {

            new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.app_name)).setMessage("Battery Optimization is already enabled! Do you want to change it?").setIcon(R.mipmap.ic_launcher).setCancelable(true).setNegativeButton(getResources().getString(R.string.f69no), new DialogInterface.OnClickListener() {


                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {
                SettingsFragment.this.startActivity(new Intent("android.settings.SETTINGS"));
                dialogInterface.dismiss();
            }).show();
        }

        public void showAccessibilitySettings() {
            new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.app_name)).setMessage(getString(R.string.accessibility_service_des)).setIcon(R.mipmap.ic_launcher).setCancelable(false).setPositiveButton(getResources().getString(R.string.enable), new DialogInterface.OnClickListener() {

                @SuppressLint("WrongConstant")
                public void onClick(DialogInterface dialogInterface, int i) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("package:");
                    sb.append(SettingsFragment.this.getActivity().getPackageName());
                    Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
                    intent.addFlags(1342177280);
                    SettingsFragment.this.startActivity(intent);
                }
            }).show();
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            Preference findPreference = findPreference(str);
            if (findPreference instanceof ListPreference) {
                findPreference.setSummary(((ListPreference) findPreference).getEntry());
            }
        }
    }
}