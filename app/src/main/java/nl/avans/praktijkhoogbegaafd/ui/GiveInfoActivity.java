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
    private boolean withPhr = false;

    private InfoEntityManager iem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_info);

        parental = getIntent().getBooleanExtra("parental", false);
        withPhr = getIntent().getBooleanExtra("withPhr", false);

        String[] categories = getResources().getStringArray(R.array.begeleidsters);

        Spinner spinner = (Spinner) findViewById(R.id.sr_info_behandelaar);
        ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        TextView tv_spinner_text = findViewById(R.id.tv_info_behandelaar);

        spinner.setVisibility(withPhr ? View.VISIBLE : View.GONE);
        tv_spinner_text.setVisibility(withPhr ? View.VISIBLE : View.GONE);

        EditText etName = findViewById(R.id.et_info_name);

        Button confirm = findViewById(R.id.bn_info_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etName.getText().toString().equals("")){
                    SharedPreferences prefs = getSharedPreferences("info", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getResources().getString(R.string.PREFS_NAME), etName.getText().toString());
                    editor.putBoolean(getResources().getString(R.string.PREFS_CHILDRENMODE), parental);
                    editor.putString(getResources().getString(R.string.PREFS_BEGELEIDSTER), categories[spinner.getSelectedItemPosition()]);
                    editor.putBoolean(getResources().getString(R.string.PREFS_WITHPHR), withPhr);
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("info", true);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Controleer of alles ingevuld is", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}