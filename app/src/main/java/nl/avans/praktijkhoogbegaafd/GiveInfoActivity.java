package nl.avans.praktijkhoogbegaafd;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GiveInfoActivity extends AppCompatActivity {

    private boolean parental = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_info);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        TextView tvParent = findViewById(R.id.tv_info_parent);
        EditText etParent = findViewById(R.id.et_info_parent);

        parental = getIntent().getBooleanExtra("parental", false);
        if(parental){
            tvParent.setVisibility(View.VISIBLE);
            etParent.setVisibility(View.VISIBLE);
        }

        String[] categories = {"Lisanne Boerboom", "Mirthe Zom", "Meghan Kalisvaart", "Tessa van Sluijs", "Noor Vugs", "Lotte Kobossen"};
        Spinner spinner = (Spinner) findViewById(R.id.sr_info_behandelaar);
        ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        EditText etName = findViewById(R.id.et_info_name);
        EditText etBirthday = findViewById(R.id.et_info_birthday);

        Button confirm = findViewById(R.id.bn_info_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parental){
                    if(!(etName.getText().toString().equals("") || etBirthday.getText().toString().equals("") || etParent.getText().toString().equals(""))){
                        MainActivity.name = etName.getText().toString();
                        MainActivity.birthDay = etBirthday.getText().toString();
                        MainActivity.childrenmode = true;
                        MainActivity.begeleidster = categories[spinner.getSelectedItemPosition()];
                        MainActivity.parentalName = etParent.getText().toString();

                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("Name", MainActivity.name);
                        editor.putString("Birthday", MainActivity.birthDay);
                        editor.putString("Begeleidster", MainActivity.begeleidster);
                        editor.putString("Parent", MainActivity.parentalName);
                        editor.putBoolean("ParentalControl", MainActivity.childrenmode);
                        editor.putBoolean("Logged", true);
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("info", true);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Controleer of alles ingevuld is", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(!(etName.getText().toString().equals("") || etBirthday.getText().toString().equals(""))){
                        MainActivity.name = etName.getText().toString();
                        MainActivity.birthDay = etBirthday.getText().toString();
                        MainActivity.childrenmode = false;
                        MainActivity.begeleidster = categories[spinner.getSelectedItemPosition()];

                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("Name", MainActivity.name);
                        editor.putString("Birthday", MainActivity.birthDay);
                        editor.putString("Begeleidster", MainActivity.begeleidster);
                        editor.putBoolean("ParentalControl", MainActivity.childrenmode);
                        editor.putBoolean("Logged", true);
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("info", true);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Controleer of alles ingevuld is", Toast.LENGTH_LONG).show();
                    }
                }




            }
        });

    }
}