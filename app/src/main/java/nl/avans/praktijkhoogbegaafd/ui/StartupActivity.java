package nl.avans.praktijkhoogbegaafd.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import nl.avans.praktijkhoogbegaafd.R;

public class StartupActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String name = sharedPref.getString("Name", "?");
        if(sharedPref.contains("Name") || sharedPref.contains("Birthday") || sharedPref.contains("Begeleidster")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.welcomeMessage).setNegativeButton("Ga door", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setTitle("Welkom!").show();


        ConstraintLayout cl_select_phr = findViewById(R.id.cl_startup_select_phr);
        LinearLayout ll_select_mode = findViewById(R.id.ll_startup_select_mode);

        Switch s_withphr = findViewById(R.id.s_startup_withphr);
        Button bn_continue = findViewById(R.id.bn_startup_continue);

        s_withphr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView tv_code = findViewById(R.id.tv_startup_code);
                EditText et_code = findViewById(R.id.et_startup_code);

                tv_code.setVisibility(!isChecked ? View.GONE : View.VISIBLE);
                et_code.setVisibility(!isChecked ? View.GONE : View.VISIBLE);
            }
        });

        bn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(s_withphr.isChecked()) {
                    EditText et_code = findViewById(R.id.et_startup_code);

                    if(et_code.getText().toString().equals("0165")){
                        cl_select_phr.setVisibility(View.GONE);
                        ll_select_mode.setVisibility(View.VISIBLE);
                    } else {
                        builder.setMessage("Ingevoerde code is onjuist").setNegativeButton("Oké", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setTitle("Oeps...").show();
                    }
                } else {
                    cl_select_phr.setVisibility(View.GONE);
                    ll_select_mode.setVisibility(View.VISIBLE);
                }
            }
        });

        ImageButton imageAlone = findViewById(R.id.ib_startup_alone);
        ImageButton imageParental = findViewById(R.id.ib_startup_parental);

        imageAlone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GiveInfoActivity.class);
                intent.putExtra("withPhr", s_withphr.isChecked());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        imageParental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GiveInfoActivity.class);
                intent.putExtra("parental", true);
                intent.putExtra("withPhr", s_withphr.isChecked());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }
}