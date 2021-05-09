package nl.avans.praktijkhoogbegaafd.ui.questionActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.slider.RangeSlider;

import java.util.List;

import nl.avans.praktijkhoogbegaafd.ui.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;

public class QuestionPsymo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_psymo);

        RangeSlider rs = findViewById(R.id.rs_questions_emoto);
        TextView title = findViewById(R.id.tv_emoto_title);
        ImageView header = findViewById(R.id.ig_emoto_header);
        TextView max = findViewById(R.id.tv_emoto_max);
        TextView min = findViewById(R.id.tv_emoto_min);
        TextView neutral = findViewById(R.id.tv_emoto_neutral);
        TextView tips = findViewById(R.id.tv_emoto_tips);
        ImageView figure = findViewById(R.id.iv_questions_emoto);

        if(MainActivity.childrenmode && !getIntent().hasExtra("parent")){
            title.setText("Hoeveel Psymo's heb je?");
            title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            title.setTextColor(Color.BLUE);
            header.setImageResource(R.mipmap.ic_phr_billies_psymo_foreground);
            tips.setText("blabla");
            max.setText("10");
            min.setText("0");
            neutral.setText("5");
            rs.setValueFrom(0);
            rs.setValueTo(10);
            rs.setTickVisible(true);
            rs.setStepSize(1);
            figure.setVisibility(View.VISIBLE);
            tips.setVisibility(View.INVISIBLE);
            figure.setImageResource(R.mipmap.ic_phr_psymo_text_foreground);
        } else{
            title.setText("Hoe aanwezig is je psychomotorische intensiteit?");
            title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            header.setImageResource(R.mipmap.ic_phr_stars_foreground);
            max.setText("Aanwezig");
            neutral.setText("Neutraal");
            min.setText("Afwezig");
            tips.setText("De psychomotorische intensiteit kan het best omschreven worden door een constante psychomotorische activiteit, hoge energielevels, snel en veel praten, veel beweging nodig hebben en een drang naar actie. Dit kan naar voren komen in het niet goed stil kunnen zitten, het repetitief friemelen aan voorwerpen of last hebben van motorische of verbale impulsiviteit.");
            rs.setValueFrom(-2);
            rs.setValueTo(2);
            rs.setTickVisible(true);
            rs.setStepSize(1);
            figure.setVisibility(View.INVISIBLE);
            tips.setVisibility(View.VISIBLE);
        }

        Button bnNext = findViewById(R.id.bn_questions_emoto);
        bnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Float> values = rs.getValues();
                System.out.println(values.get(0));

                Intent intent = new Intent(getApplicationContext(), QuestionSenzo.class);
                if(getIntent().hasExtra("parent")){
                    intent.putExtra("parent", true);
                }
                intent.putExtra("Emoto", getIntent().getIntExtra("Emoto", 0));
                intent.putExtra("Fanti", getIntent().getIntExtra("Fanti", 0));
                intent.putExtra("Intellecto", getIntent().getIntExtra("Intellecto", 0));
                int value = Math.round(values.get(0));
                intent.putExtra("Psymo", value);
                startActivity(intent);
            }
        });
    }
}