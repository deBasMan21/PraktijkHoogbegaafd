package nl.avans.praktijkhoogbegaafd.ui;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.Calendar;

import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.dal.InfoEntity;
import nl.avans.praktijkhoogbegaafd.logic.InfoEntityManager;

public class GiveInfoActivity extends AppCompatActivity {

    private boolean parental = false;

    private InfoEntityManager iem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_info);

        iem = new InfoEntityManager(getApplication());

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        TextView tvParent = findViewById(R.id.tv_info_parent);
        EditText etParent = findViewById(R.id.et_info_parent);

        parental = getIntent().getBooleanExtra("parental", false);
        if(parental){
            tvParent.setVisibility(View.VISIBLE);
            etParent.setVisibility(View.VISIBLE);
        }

        String[] categories = {"Eda Canikli", "Eveline Eulderink", "Hanneke van de Sanden", "Imke", "Lisanne Boerboom", "Leah",
                "Lotte Kobossen", "Maud van Hoving", "Meghan Kalisvaart", "Milou van Beijsterveldt", "Mirthe Zom", "Noor Vugs",
                "Sjarai Gelissen", "Tessa van Sluijs", "Yvonne Buijsen"};
        Spinner spinner = (Spinner) findViewById(R.id.sr_info_behandelaar);
        ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        EditText etName = findViewById(R.id.et_info_name);
        EditText etBirthday = findViewById(R.id.et_info_birthday);

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);


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

                        iem.insertInfo(new InfoEntity(MainActivity.name, MainActivity.birthDay, MainActivity.begeleidster, MainActivity.parentalName, MainActivity.childrenmode));


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
                        MainActivity.parentalName = "";
                        MainActivity.begeleidster = categories[spinner.getSelectedItemPosition()];

                        iem.insertInfo(new InfoEntity(MainActivity.name, MainActivity.birthDay, MainActivity.begeleidster, MainActivity.parentalName, MainActivity.childrenmode));

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