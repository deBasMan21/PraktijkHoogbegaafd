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

import nl.avans.praktijkhoogbegaafd.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;

public class QuestionSenzo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_senzo);

        RangeSlider rs = findViewById(R.id.rs_questions_emoto);
        TextView title = findViewById(R.id.tv_emoto_title);
        ImageView header = findViewById(R.id.ig_emoto_header);
        TextView max = findViewById(R.id.tv_emoto_max);
        TextView min = findViewById(R.id.tv_emoto_min);
        TextView neutral = findViewById(R.id.tv_emoto_neutral);
        TextView tips = findViewById(R.id.tv_emoto_tips);

        if(MainActivity.childrenmode){
            title.setText("Hoeveel Senzo's heb je?");
            title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            title.setTextColor(Color.YELLOW);
            header.setImageResource(R.mipmap.ic_phr_billies_senzo_foreground);
            tips.setText("blabla");
            max.setText("10");
            min.setText("0");
            neutral.setText("5");
            rs.setValueFrom(0);
            rs.setValueTo(10);
            rs.setTickVisible(true);
            rs.setStepSize(1);
        } else{
            title.setText("Hoe aanwezig is je sensorische intensiteit?");
            title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            header.setImageResource(R.mipmap.ic_phr_stars_foreground);
            max.setText("Aanwezig");
            min.setText("Neutraal");
            neutral.setText("Afwezig");
            tips.setText("-snel huilen\n-vaak boos worden\n-je heel vrolijk voelen");
            rs.setValueFrom(-2);
            rs.setValueTo(2);
            rs.setTickVisible(true);
            rs.setStepSize(1);
        }

        Button bnNext = findViewById(R.id.bn_questions_emoto);
        bnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Float> values = rs.getValues();
                System.out.println(values.get(0));

                Intent intent = new Intent(getApplicationContext(), QuestionSummary.class);
                intent.putExtra("Emoto", getIntent().getIntExtra("Emoto", 0));
                intent.putExtra("Fanti", getIntent().getIntExtra("Fanti", 0));
                intent.putExtra("Intellecto", getIntent().getIntExtra("Intellecto", 0));
                intent.putExtra("Psymo", getIntent().getIntExtra("Psymo", 0));
                int value = Math.round(values.get(0));
                intent.putExtra("Senzo", value);
                startActivity(intent);
            }
        });
    }
}