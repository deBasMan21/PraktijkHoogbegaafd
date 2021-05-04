package nl.avans.praktijkhoogbegaafd.ui.questionActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.time.LocalDate;

import nl.avans.praktijkhoogbegaafd.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.dal.FeelingEntity;
import nl.avans.praktijkhoogbegaafd.dal.FeelingsDB;
import nl.avans.praktijkhoogbegaafd.logic.FeelingsEntityManager;
import nl.avans.praktijkhoogbegaafd.ui.home.HomeFragment;

public class QuestionSummary extends AppCompatActivity {

    private boolean parent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MainActivity.childrenmode && !getIntent().hasExtra("parent")){
            setContentView(R.layout.activity_question_summary);
            TextView emoto = findViewById(R.id.tv_summary_emoto);
            TextView fanti = findViewById(R.id.tv_summary_fanti);
            TextView intellecto = findViewById(R.id.tv_summary_intellecto);
            TextView psymo = findViewById(R.id.tv_summary_psymo);
            TextView senzo = findViewById(R.id.tv_summary_senzo);

            emoto.setText("Je hebt " + getIntent().getIntExtra("Emoto", 0) + " Emoto's op dit moment");
            fanti.setText("Je hebt " + getIntent().getIntExtra("Fanti", 0) + " Fanti's op dit moment");
            intellecto.setText("Je hebt " + getIntent().getIntExtra("Intellecto", 0) + " Intellecto's op dit moment");
            psymo.setText("Je hebt " + getIntent().getIntExtra("Psymo", 0) + " Psymo's op dit moment");
            senzo.setText("Je hebt " + getIntent().getIntExtra("Senzo", 0) + " Senzo's op dit moment");
        } else {
            setContentView(R.layout.activity_question_summary_adult);
            TextView tv = findViewById(R.id.tv_question_summary);
            String summary = "";
            summary = summary + getIntent().getIntExtra("Emoto", 0);
            summary = summary + "\n" + getIntent().getIntExtra("Fanti", 0);
            summary = summary + "\n" + getIntent().getIntExtra("Intellecto", 0);
            summary = summary + "\n" + getIntent().getIntExtra("Psymo", 0);
            summary = summary + "\n" + getIntent().getIntExtra("Senzo", 0);
            tv.setText(summary);
        }

        if(getIntent().hasExtra("parent")){
            this.parent = true;
        }

        Button bn = findViewById(R.id.bn_question_summary);
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new insertValuesInDB().execute();
            }
        });
    }

    public void startIntent(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public class insertValuesInDB extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            FeelingsEntityManager fem = new FeelingsEntityManager(getApplication());

            int emoto = getIntent().getIntExtra("Emoto", 0);
            int fanti = getIntent().getIntExtra("Fanti", 0);
            int intellecto = getIntent().getIntExtra("Intellecto", 0);
            int psymo = getIntent().getIntExtra("Psymo", 0);
            int senzo = getIntent().getIntExtra("Senzo", 0);

            int id = fem.getHighestId() + 1;
            FeelingEntity feeling = new FeelingEntity(id, LocalDate.now().toString(), !parent, emoto, fanti, intellecto, psymo, senzo);

            FeelingEntity[] fe = {feeling};

            fem.insertFeelings(fe);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startIntent();
        }
    }
}