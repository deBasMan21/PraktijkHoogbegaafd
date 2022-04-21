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

        boolean withPhr = MainActivity.withPhr;

        HashMap<String, String> emailList = new HashMap<>();

        String[] begeleidsters = getResources().getStringArray(R.array.begeleidsters);

        for(String begeleidster : begeleidsters){
            String email = begeleidster.split(" ")[0] + "@praktijkhoogbegaafd.nl";
            if (begeleidster.split(" ")[0].equals("Denise")){
                email = begeleidster.split(" ")[0] + "_" + begeleidster.split(" ")[1].charAt(0) + "@praktijkhoogbegaafd.nl";
            }
            System.out.println(email);
            emailList.put(begeleidster, email);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File pdf = (File) getIntent().getSerializableExtra("pdf");

        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", pdf);

        Intent i = new Intent(Intent.ACTION_SEND);
        if(withPhr){
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailList.get(MainActivity.begeleidster)});
        }
        i.putExtra(Intent.EXTRA_SUBJECT,"Verslag uit Praktijkhoogbegaafd app van " + MainActivity.name);
        i.putExtra(Intent.EXTRA_TEXT,"Dit verslag bestaat uit de data van " + LocalDate.now().minusDays(6).toString() + " tot en met " + LocalDate.now().toString() + ".");
        i.putExtra(Intent.EXTRA_STREAM,uri);
        i.setType("application/pdf");
        startActivity(Intent.createChooser(i,"Share your progression..."));
    }

    public void goBack(View v){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}