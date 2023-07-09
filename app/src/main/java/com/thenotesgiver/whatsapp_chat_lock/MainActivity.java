package com.thenotesgiver.whatsapp_chat_lock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thenotesgiver.whatsapp_chat_lock.ads.AppSettings;
import com.thenotesgiver.whatsapp_chat_lock.fragment.HomeFragment;
import com.thenotesgiver.whatsapp_chat_lock.utils.AccessibilityServiceHandler;


public class MainActivity extends AppCompatActivity {
    public static final String HAS_ACCESS_CONSENT = "ACCESS_CONSENT";
    @SuppressLint("StaticFieldLeak")
    public static Activity activity = null;

    public  Context context = null;
    public static boolean isFromActivity = false;
    FrameLayout content_frame;
    SharedPreferences.Editor editor;
    FloatingActionButton floatingActionButton;
    SharedPreferences sharedPreferences;
    CardView tvPermissions;
    private InterstitialAd mInterstitialAd;
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName () + "/" + AccessibilityServiceHandler.class.getCanonicalName ();
        try {
            accessibilityEnabled = Settings.Secure.getInt (context.getApplicationContext ().getContentResolver (), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException ignored) {  }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter (':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString (context.getApplicationContext ().getContentResolver (), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                colonSplitter.setString (settingValue);
                while (colonSplitter.hasNext ()) {
                    String accessibilityService = colonSplitter.next ();

                    if (accessibilityService.equalsIgnoreCase (service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.editor = this.sharedPreferences.edit();
        if (!isAccessibilitySettingsOn(this)) {
            if (!this.sharedPreferences.getBoolean(HAS_ACCESS_CONSENT, false)) {
                showAccessConsent();
            } else {
                showAccessibilitySettings();
            }
        }
        this.tvPermissions = findViewById(R.id.tvPermissions);



        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,getString(R.string.intMain), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                //   Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                //    Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });


        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId(getString(R.string.bannerEmail));

        MobileAds.initialize(this, initializationStatus -> {
        });

// TODO: Add adView to your view hierarchy.
        AdView mAdView = findViewById(R.id.adView);
        mAdView.loadAd(adRequest);


        this.tvPermissions.setOnClickListener(view -> {
            Intent intent = new Intent();
            String packageName = MainActivity.this.getPackageName();
            if (!((PowerManager) MainActivity.this.getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
                intent.setData(Uri.parse("package:" + packageName));
                MainActivity.this.startActivity(intent);
            }
        });
        batteryOptButton();
        context = this;
        activity = this;


        this.content_frame = findViewById(R.id.content_frame);
        if (bundle == null) {
          HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
            beginTransaction.add(R.id.content_frame, homeFragment);
            beginTransaction.detach(homeFragment);
            beginTransaction.attach(homeFragment);
            beginTransaction.commit();
        }
        this.floatingActionButton = findViewById(R.id.fab);
        this.floatingActionButton.setOnClickListener(view -> {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
            }

            if (MainActivity.isAccessibilitySettingsOn(MainActivity.this)) {
                try {
                    MainActivity.isFromActivity = true;
                    Intent intent = new Intent();
                    intent.setClassName("com.whatsapp", "com.whatsapp.HomeActivity");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    MainActivity.this.startActivityForResult(intent, 9);
                } catch (Exception unused) {
                    MainActivity mainActivity = MainActivity.this;
                    Toast.makeText(mainActivity, mainActivity.getString(R.string.not_installed), Toast.LENGTH_SHORT).show();
                }
            } else if (!MainActivity.this.sharedPreferences.getBoolean(MainActivity.HAS_ACCESS_CONSENT, false)) {
                MainActivity.this.showAccessConsent();
            } else {
                MainActivity.this.showAccessibilitySettings();
            }
        });

        new RatingDialog.Builder(this).threshold(4.0f).session(3).title(getString(R.string.rate_dialog_title)).positiveButtonText(getString(R.string.positive_button)).negativeButtonText(getString(R.string.negative_button)).formTitle(getString(R.string.form_title)).formHint(getString(R.string.form_hint)).formSubmitText(getString(R.string.form_submit)).formCancelText(getString(R.string.form_cancel)).onRatingBarFormSumbit(str -> {
        }).build().show();
    }


    @Override
    public void onResume() {
        batteryOptButton();
        super.onResume();
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,getString(R.string.intMain), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                //   Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                //    Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
    }

    @SuppressLint("ResourceType")
    private void showAccessConsent() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.consent_layout_acs);
        dialog.getWindow().setBackgroundDrawableResource(17170445);
        dialog.getWindow().setLayout(-1, -2);
        dialog.setCancelable(false);
        final CheckBox checkBox = dialog.findViewById(R.id.checkBox);
        final TextView textView = dialog.findViewById(R.id.checkBoxWarning);
        checkBox.setOnCheckedChangeListener((compoundButton, z) -> {
            if (z) {
                textView.setVisibility(8);
            }
        });
        dialog.findViewById(R.id.decline).setOnClickListener(v ->{
            finish();
        });
        dialog.findViewById(R.id.privacyButton).setOnClickListener(view -> MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(AppSettings.privacyPolicy))));
        dialog.findViewById(R.id.confirmButton).setOnClickListener(view -> {
            if (!checkBox.isChecked()) {
                textView.setVisibility(0);
                return;
            }
            MainActivity.this.editor.putBoolean(MainActivity.HAS_ACCESS_CONSENT, true).apply();
            StringBuilder sb = new StringBuilder();
            sb.append("package:");
            sb.append(MainActivity.this.getPackageName());
            Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
            intent.addFlags(1342177280);
            MainActivity.this.startActivity(intent);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void batteryOptButton() {
        new Intent();
        if (!((PowerManager) getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(getPackageName())) {
            this.tvPermissions.setVisibility(View.VISIBLE);
        } else {
            this.tvPermissions.setVisibility(View.GONE);
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // androidx.activity.ComponentActivity
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage("Are you sure you want to exit?").setCancelable(false).setPositiveButton("Yes", (dialogInterface, i) -> MainActivity.this.finish()).setNegativeButton("No", null).show();
    }


    public void showAccessibilitySettings() {
        new AlertDialog.Builder(this).setTitle(getString(R.string.app_name)).setMessage(getString(R.string.accessibility_service_des)).setIcon(R.mipmap.ic_launcher).setCancelable(false).setPositiveButton(getResources().getString(R.string.enable), (dialogInterface, i) -> {
            StringBuilder sb = new StringBuilder();
            sb.append("package:");
            sb.append(MainActivity.this.getPackageName());
            Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            MainActivity.this.startActivity(intent);
        }).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint({"WrongConstant"})
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.main_about){
            startActivity(new Intent(this, AboutActivity.class));
        }
        if (menuItem.getItemId() == R.id.main_setting){
            startActivity(new Intent(this, SettingsActivity.class));
        }

        if (menuItem.getItemId() == R.id.yt){
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + "yb9RNIKmSDw"));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + "yb9RNIKmSDw"));
            try {
                startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                startActivity(webIntent);
            }
        }

        return super.onOptionsItemSelected(menuItem);
    }
}