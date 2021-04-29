package nl.avans.praktijkhoogbegaafd;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class StartupActivity extends AppCompatActivity {

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
        builder.setMessage("Welkom bij de Praktijk Hoogbegaafd app waarmee je jouw intensiteiten en X-citabillies kan meten. Door elke dag in te vullen of de intensiteiten en X-citabillies bij jou aanwezig waren, kunnen we samen kijken hoe we jou het beste kunnen helpen. Voorafgaand aan je afspraak, kun je eenvoudig je scores delen met jouw behandelaar. Mocht je hier nog vragen over hebben, kun je contact opnemen met jouw behandelaar. Veel plezier met invullen. \n\n LET OP! Dit is een testversie van de app en deze dient alleen gebruikt te worden als het is aanbevolen door uw begeleider.").setPositiveButton("Ga door", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setTitle("Welkom!").show();


        ImageButton imageAlone = findViewById(R.id.ib_startup_alone);
        ImageButton imageParental = findViewById(R.id.ib_startup_parental);

        imageAlone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GiveInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        imageParental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GiveInfoActivity.class);
                intent.putExtra("parental", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }
}