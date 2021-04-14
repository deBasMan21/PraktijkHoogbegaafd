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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_summary);



        TextView tv = findViewById(R.id.tv_question_summary);
        String summary = "Values: ";
        summary = summary + "Emoto: " + getIntent().getIntExtra("Emoto", 0);
        summary = summary + "Fanti: " + getIntent().getIntExtra("Fanti", 0);
        summary = summary + "Intellecto: " + getIntent().getIntExtra("Intellecto", 0);
        summary = summary + "Psymo: " + getIntent().getIntExtra("Psymo", 0);
        summary = summary + "Senzo: " + getIntent().getIntExtra("Senzo", 0);
        tv.setText(summary);

        Button bn = findViewById(R.id.bn_question_summary);
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new insertValuesInDB().execute();
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
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

            FeelingEntity feeling = new FeelingEntity(LocalDate.now().toString(), emoto, fanti, intellecto, psymo, senzo);

            FeelingEntity[] fe = {feeling};

            fem.insertFeelings(fe);
            return null;
        }
    }
}