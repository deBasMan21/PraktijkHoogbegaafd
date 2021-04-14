package nl.avans.praktijkhoogbegaafd.ui.settings;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import nl.avans.praktijkhoogbegaafd.R;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        String[] categories = {"Lisanne Bierboom", "Mirthe Zom", "Meghan Kalisvaart", "Tessa van Sluijs", "Noor Vugs", "Lotte Kobossen"};
        Spinner spinner = (Spinner) root.findViewById(R.id.sr_settings_begeleidster);
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        Calendar calendar = Calendar.getInstance();

        EditText bday = root.findViewById(R.id.et_settings_date_of_birth);


        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            bday.setText(new SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(calendar.getTime()));
        };

        bday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(),
                        date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });


        return root;
    }
}