package nl.avans.praktijkhoogbegaafd.ui.settings;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import nl.avans.praktijkhoogbegaafd.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.dal.InfoEntity;
import nl.avans.praktijkhoogbegaafd.logic.InfoEntityManager;
import nl.avans.praktijkhoogbegaafd.ui.home.HomeFragment;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;


    private InfoEntityManager iem;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        iem = MainActivity.iem;

        String[] categories = {"Lisanne Bierboom", "Mirthe Zom", "Meghan Kalisvaart", "Tessa van Sluijs", "Noor Vugs", "Lotte Kobossen"};
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

        TextView parentName = root.findViewById(R.id.et_settings_parent);
        parentName.setText(MainActivity.parentalName);

        EditText parent = root.findViewById(R.id.et_settings_parent);
        if(MainActivity.childrenmode){
            parent.setVisibility(View.VISIBLE);
        } else {
            parent.setVisibility(View.INVISIBLE);
        }

        Calendar calendar = Calendar.getInstance();

        EditText bday = root.findViewById(R.id.et_settings_date_of_birth);
        bday.setText(MainActivity.birthDay);


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

        EditText etName = root.findViewById(R.id.et_settings_name);
        etName.setText(MainActivity.name);

        Button bnBack = root.findViewById(R.id.bn_settings_confirm);
        bnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldName = MainActivity.name;
                MainActivity.begeleidster = categories[spinner.getSelectedItemPosition()];
                MainActivity.name = etName.getText().toString();
                MainActivity.birthDay = bday.getText().toString();
                MainActivity.parentalName = parentName.getText().toString();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                iem.updateInfo(oldName, new InfoEntity(MainActivity.name, MainActivity.birthDay, MainActivity.parentalName, MainActivity.begeleidster, MainActivity.childrenmode));

                startActivity(intent);
            }
        });


        return root;
    }


}