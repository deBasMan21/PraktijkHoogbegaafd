package nl.avans.praktijkhoogbegaafd.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;

import nl.avans.praktijkhoogbegaafd.R;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);



        HashMap<String, String> emailList = new HashMap<>();
        emailList.put("Lisanne Boerboom", "lisanne@praktijkhoogbegaafd.nl");
        emailList.put("Eda Canikli", "eda@praktijkhoogbegaafd.nl");
        emailList.put("Eveline Eulderink", "eveline@praktijkhoogbegaafd.nl");
        emailList.put("Hanneke van de Sanden", "hanneke@praktijkhoogbegaafd.nl");
        emailList.put("Imke van der Velden", "imke@praktijkhoogbegaafd.nl");
        emailList.put("Lotte Kobossen", "lotte@praktijkhoogbegaafd.nl");
        emailList.put("Maud van Hoving", "maud@praktijkhoogbegaafd.nl");
        emailList.put("Meghan Kalisvaart", "meghan@praktijkhoogbegaafd.nl");
        emailList.put("Milou van Beijsterveldt", "milou@praktijkhoogbegaafd.nl");
        emailList.put("Mirthe Zom", "mirthe@praktijkhoogbegaafd.nl");
        emailList.put("Noor Vugs", "noor@praktijkhoogbegaafd.nl");
        emailList.put("Tessa van Sluijs", "tessa@praktijkhoogbegaafd.nl");
        emailList.put("Yvonne Buijsen", "yvonne@praktijkhoogbegaafd.nl");
        emailList.put("Leah Keijzer", "leah@praktijkhoogbegaafd.nl");
        emailList.put("Sjarai Gelissen", "sjarai@praktijkhoogbegaafd.nl");
        emailList.put("Dev", "bbuijsen@gmail.com");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File pdf = (File) getIntent().getSerializableExtra("pdf");

        if(pdf.canRead() && pdf.exists()){
            System.out.println("GELUKT");
        }

        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", pdf);

        Intent i = new Intent(Intent.ACTION_SEND);
//        i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailList.get(MainActivity.begeleidster)});
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailList.get("Dev")});
        i.putExtra(Intent.EXTRA_SUBJECT,"Verslag uit Praktijkhoogbegaafd app van " + MainActivity.name);
        i.putExtra(Intent.EXTRA_TEXT,"Dit verslag bestaat uit de data van " + LocalDate.now().minusDays(6).toString() + " tot en met " + LocalDate.now().toString() + ".");
        i.putExtra(Intent.EXTRA_STREAM,uri);
        i.setType("application/pdf");
        startActivity(Intent.createChooser(i,"Share your progression..."));

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });

    }

    public void goBack(View v){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}