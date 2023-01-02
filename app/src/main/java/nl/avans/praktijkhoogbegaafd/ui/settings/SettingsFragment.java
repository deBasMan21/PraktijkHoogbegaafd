package nl.avans.praktijkhoogbegaafd.ui.settings;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import nl.avans.praktijkhoogbegaafd.logic.NotificationUtils;
import nl.avans.praktijkhoogbegaafd.ui.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.dal.InfoEntity;
import nl.avans.praktijkhoogbegaafd.logic.InfoEntityManager;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;
    private int resetCount = 0;
    private boolean notificationsEnabled = true;
    private String notificationsKey = "notificationsEnabled";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        String[] categories = getResources().getStringArray(R.array.begeleidsters);
        Spinner spinner = (Spinner) root.findViewById(R.id.sr_settings_begeleidster);
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int selectedBegeleidster = 0;
        for(int i = 0; i < categories.length; i++){
            if(categories[i].equals(MainActivity.begeleidster)){
                selectedBegeleidster = i;
            }
        }
        spinner.setSelection(selectedBegeleidster);

        TextView tv_spinner_text = root.findViewById(R.id.tv_settings_begeleidster);

        SharedPreferences prefs = getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        boolean withPhr = prefs.getBoolean(getResources().getString(R.string.PREFS_WITHPHR), false);
        spinner.setVisibility(withPhr ? View.VISIBLE : View.GONE);
        tv_spinner_text.setVisibility(withPhr ? View.VISIBLE : View.GONE);
        notificationsEnabled = prefs.getBoolean(notificationsKey, true);

        EditText etName = root.findViewById(R.id.et_settings_name);
        etName.setText(MainActivity.name);

        Button bnBack = root.findViewById(R.id.bn_settings_confirm);
        bnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldName = MainActivity.name;
                MainActivity.begeleidster = categories[spinner.getSelectedItemPosition()];
                MainActivity.name = etName.getText().toString();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                SharedPreferences prefs = getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getResources().getString(R.string.PREFS_NAME), etName.getText().toString());
                editor.putString(getResources().getString(R.string.PREFS_BEGELEIDSTER), categories[spinner.getSelectedItemPosition()]);
                editor.apply();

                startActivity(intent);
            }
        });

        TextView tv_version = root.findViewById(R.id.tv_settings_version);
        tv_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCount++;
                if(resetCount == 5){
                    resetCount = 0;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Je staat op het punt om de app te resetten. Dit kan niet teruggedraaid worden.").setNegativeButton("Ga door", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.fem.deleteAll();

                            SharedPreferences prefs = getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.remove(getResources().getString(R.string.PREFS_WITHPHR));
                            editor.remove(getResources().getString(R.string.PREFS_CHILDRENMODE));
                            editor.remove(getResources().getString(R.string.PREFS_BEGELEIDSTER));
                            editor.remove(getResources().getString(R.string.PREFS_NAME));
                            editor.remove(notificationsKey);
                            editor.apply();

                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }).setPositiveButton("Annuleren", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setTitle("App resetten").show();
                }
            }
        });

        ToggleButton tbNotifications = root.findViewById(R.id.tb_settings_notifications);
        tbNotifications.setChecked(notificationsEnabled);
        tbNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notificationsEnabled) {
                    cancelReminderNotification();
                    System.out.println("debug: disabled notifications");
                } else {
                    setReminderNotification();
                    System.out.println("debug: enabled notifications");
                }
                prefs.edit().putBoolean(notificationsKey, !notificationsEnabled).apply();
                notificationsEnabled = !notificationsEnabled;
            }
        });

        Button privacyBtn = root.findViewById(R.id.tv_settings_privacy);
        privacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openPrivacyPolicy = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/gview?embedded=true&url=https://www.praktijkhoogbegaafd.nl/wp-content/uploads/2021/01/Privacy-policy-jan-2021.pdf"));
                startActivity(openPrivacyPolicy);
            }
        });

        return root;
    }

    public void setReminderNotification()
    {
        NotificationUtils _notificationUtils = new NotificationUtils(getContext());
        _notificationUtils.setReminders();
    }

    public void cancelReminderNotification()
    {
        NotificationUtils _notificationUtils = new NotificationUtils(getContext());
        _notificationUtils.cancelReminders();
    }
}