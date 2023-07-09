package com.thenotesgiver.whatsapp_chat_lock;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.multidex.BuildConfig;
import androidx.preference.Preference;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.thenotesgiver.whatsapp_chat_lock.ads.AppSettings;


public class AboutActivity extends AppCompatActivity {

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

    public static class SettingsFragment extends PreferenceFragmentCompat {

        public static void shareApp(Context context) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            String text = context.getString(R.string.share_app_message);
            text = String.format(text, BuildConfig.APPLICATION_ID);
            intent.putExtra("android.intent.extra.TEXT",text);
            intent.setType("text/plain");
            context.startActivity(intent);
        }

        @Override // androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            setPreferencesFromResource(R.xml.pref_about, str);
            setVersionAsSummary();
            //setVersionAsSummary((Preference) findPreference(getContext().getString(R.string.version_key)));
        }

        @Override // androidx.preference.PreferenceManager.OnPreferenceTreeClickListener, androidx.preference.PreferenceFragmentCompat
        public boolean onPreferenceTreeClick(Preference preference) {
            String key = preference.getKey();
            if (key.equals(getString(R.string.rate_key))) {
                showRate(getActivity());
                return true;
            } else if (key.equals(getString(R.string.more_key))) {
                showGP();
                return true;
            } else if (key.equals(getString(R.string.privacy_key))) {
                showPrivacy();
                return true;
            } else if (key.equals(getString(R.string.email_key))) {
                sendmail();
                return true;
            } else if (key.equals(getString(R.string.share_key))) {
                shareApp(getActivity());
                return true;
            } else if (key.equals(getString(R.string.consent_key))) {
                return true;
            } else {
                return super.onPreferenceTreeClick(preference);
            }
        }

        private void showPrivacy() {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(AppSettings.privacyPolicy));
            startActivity(intent);
        }

        private void showRate(Activity activity) {

            try {
                activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + activity.getPackageName())));
            } catch (ActivityNotFoundException unused) {
                activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));

            }
        }

        private void sendmail() {
            Intent intent = new Intent("android.intent.action.SENDTO");
            intent.setData(Uri.parse(AppSettings.mail));
            intent.setData(new Uri.Builder().scheme("mailto").build());
            intent.putExtra("android.intent.extra.EMAIL", new String[]{"thenotesgiver@gmail.com"});
            intent.putExtra("android.intent.extra.SUBJECT", "Message Recovery] Feedback");
            startActivity(intent);
        }

        private void showGP() {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://search?q=pub:" + AppSettings.moreApps)));
        }

        private void setVersionAsSummary() {
            findPreference(getContext().getString(R.string.version_key)).setSummary(BuildConfig.VERSION_NAME);
        }
    }
}