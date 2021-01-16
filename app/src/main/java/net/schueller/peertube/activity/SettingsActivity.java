/*
 * Copyright (C) 2020 Stefan Schüller <sschueller@techdroid.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.schueller.peertube.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import androidx.preference.SwitchPreference;
import net.schueller.peertube.BuildConfig;
import net.schueller.peertube.R;

public class SettingsActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        // Attaching the layout to the toolbar object
        Toolbar toolbar = findViewById(R.id.tool_bar_settings);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // write Build Time into pref
            Preference pref = findPreference("pref_buildtime");
            assert pref != null;
            pref.setSummary(Long.toString(BuildConfig.BUILD_TIME));

            // double check disabling SSL
            final SwitchPreference insecure = (SwitchPreference) findPreference("pref_accept_insecure");
            if (insecure != null) {
                insecure.setOnPreferenceChangeListener((preference, newValue) -> {

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPref.edit();

                    boolean currentValue = sharedPref.getBoolean("pref_accept_insecure", false);

                    if (newValue instanceof Boolean && ((Boolean) newValue) != currentValue) {
                        final boolean enable = (Boolean) newValue;

                        Log.v("pref", "enable: " + enable);
                        Log.v("pref", "currentValue: " + currentValue);

                        if (enable) {
                            new Builder(preference.getContext())
                                    .setTitle(R.string.pref_insecure_confirm_title)
                                    .setMessage(R.string.pref_insecure_confirm_message)
                                    .setIcon(R.drawable.ic_info_black_24dp)
                                    .setNegativeButton(R.string.pref_insecure_confirm_no, (dialog, whichButton) -> {
                                        // do nothing
                                    })
                                    .setPositiveButton(R.string.pref_insecure_confirm_yes, (dialog, whichButton) -> {
                                        // OK has been pressed => force the new value and update the checkbox display
                                        editor.putBoolean("pref_accept_insecure", true);
                                        editor.apply();
                                        insecure.setChecked(true);
                                    }).create().show();
                            // by default ignore the pref change, which can only be validated when OK is pressed
                            return false;
                        }
                    }
                    return true;
                });
            }
        }
    }
}