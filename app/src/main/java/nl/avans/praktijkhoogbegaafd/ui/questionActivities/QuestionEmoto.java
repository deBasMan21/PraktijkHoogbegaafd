package nl.avans.praktijkhoogbegaafd.ui.questionActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.slider.RangeSlider;

import java.util.List;

import nl.avans.praktijkhoogbegaafd.R;

public class QuestionEmoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_emoto);

        RangeSlider rs = findViewById(R.id.rs_questions_emoto);
        rs.setValueFrom(0);
        rs.setValueTo(10);
        rs.setTickVisible(true);
        rs.setStepSize(1);

        Button bnNext = findViewById(R.id.bn_questions_emoto);
        bnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Float> values = rs.getValues();
                System.out.println(values.get(0));

                Intent intent = new Intent(getApplicationContext(), QuestionFanti.class);
                int value = Math.round(values.get(0));
                intent.putExtra("Emoto", value);
                startActivity(intent);
            }
        });

    }
}