package com.example.danie_000.locationproject.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.danie_000.locationproject.R;
import com.example.danie_000.locationproject.controller.PlacesDB;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private SharedPreferences settings;
    private CharSequence[] enteris;
    private CharSequence[] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set up required values
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        addPreferencesFromResource(R.xml.prefernces);
        Preference delete = findPreference("delete");
        final ListPreference mk = (ListPreference) findPreference("mk");
        final ListPreference radius = (ListPreference) findPreference("radius");
        final ListPreference type = (ListPreference) findPreference("type");
        final Preference q = findPreference("q");

        //Set type preference entry
        if (type.getEntry() != null)
        type.setSummary(type.getEntry());
        else
        type.setSummary(getString(R.string.full_search));
        type.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                switch ((String) newValue) {
                    case "2":
                        type.setSummary(getString(R.string.full_search));
                        return true;
                    case "0":
                        type.setSummary(getString(R.string.name));
                        return true;
                    case "3":
                        type.setSummary(getString(R.string.full_search_wl));
                        return true;
                }
                return true;
            }
        });

        //set mk summery
        mk.setSummary(settings.getString("mk", "km"));
        mk.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = (String) newValue;
                preference.setSummary(value);
                //Set up radius preference
                setRadiusSetting(radius, value);
                return true;
            }
        });

        //On delete preference clicked, set&show dialog
        delete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);
                dialog.setTitle(getString(R.string.delete_all_favorites));
                dialog.setMessage(getString(R.string.delete_all_massage));

                //On positive button clicked, delete all from places table (in DB)
                dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PlacesDB dbManager = new PlacesDB(SettingsActivity.this, 1);
                        dbManager.deleteTable(dbManager.PLACE_TABLE_REQUEST);
                    }
                });
                dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                return true;
            }
        });


        final String value = settings.getString("mk", "km");
        //Set up radius preference
        setRadiusSetting(radius, value);

        //Set up radius preference summery
        radius.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(settings.getString("mk", "km").equals("km"))
                    radius.setSummary(newValue + " " + getString(R.string.meters));
                else
                    radius.setSummary((int)(Integer.parseInt((String)newValue) * 1.09361) + " " + getString(R.string.yards));
                return true;
            }
        });

        q.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (q.getSummary() == null) {
                    q.setSummary("Examples:\n" +
                            "full-search=hot dogs (Must have the word hot and the word dogs, but not necesarily in that order, or in a single field.)\n" +
                            "full-search=sushi, sashimi (Must have either the word sushi or or the word sashimi.)\n" +
                            "full-search=\"santa monica\" (Must have the phrase santa monica with both terms in that order, in a single field.)");
                }
                else {
                    q.setSummary(null);
                }
                return true;
            }
        });

    }

    //Method to set up radius's enteris, enterisValues and summery
    public void setRadiusSetting(ListPreference radius, String Dvalue){
        if (Dvalue.equals("km")) {
            enteris = new CharSequence[4];
            enteris[0] = "0.5 " + getString(R.string.km);
            enteris[1] = "1 " + getString(R.string.km);
            enteris[2] = "2.5 " + getString(R.string.km);
            enteris[3] = "5 " + getString(R.string.km);

            values = new CharSequence[4];
            values[0] = "500";
            values[1] = "1000";
            values[2] = "2500";
            values[3] = "5000";
            radius.setEntries(enteris);
            radius.setEntryValues(values);
            radius.setSummary(settings.getString("radius", "5000") + " " + getString(R.string.meters));
        }
        else {
            enteris = new CharSequence[4];
            enteris[0] = "0.5 " + getString(R.string.miles);
            enteris[1] = "1 " + getString(R.string.miles);
            enteris[2] = "2.5 " + getString(R.string.miles);
            enteris[3] = "5 " + getString(R.string.miles);

            values = new CharSequence[4];
            values[0] = "804";
            values[1] = "1609";
            values[2] = "4023";
            values[3] = "8046";
            radius.setEntries(enteris);
            radius.setEntryValues(values);
            radius.setSummary((int)(Integer.parseInt(settings.getString("radius", "5000")) * 1.09361) + " " + getString(R.string.yards));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }
}
