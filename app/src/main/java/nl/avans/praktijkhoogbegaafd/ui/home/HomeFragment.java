package nl.avans.praktijkhoogbegaafd.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.util.Date;
import java.time.LocalDateTime;

import nl.avans.praktijkhoogbegaafd.dal.FeelingEntity;
import nl.avans.praktijkhoogbegaafd.domain.DayFeeling;
import nl.avans.praktijkhoogbegaafd.domain.PDFTypes;
import nl.avans.praktijkhoogbegaafd.logic.FeelingsEntityManager;
import nl.avans.praktijkhoogbegaafd.ui.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.ui.ScreenShotActivity;
import nl.avans.praktijkhoogbegaafd.ui.questionActivities.QuestionEmoto;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Button startEmotes = root.findViewById(R.id.bn_home_start);
        Button emotesParent = root.findViewById(R.id.bn_home_kid);

        if(MainActivity.childrenmode){
            startEmotes.setText("Vul je x-Citabillies in");
            emotesParent.setVisibility(View.VISIBLE);
        } else {
            startEmotes.setText("Vul je intensiteiten in");
            emotesParent.setVisibility(View.INVISIBLE);
        }

        startEmotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeelingsEntityManager fem = MainActivity.fem;
                boolean feelings = fem.getFeelingsForDay(LocalDate.now().toString(), true).getFeelingsForDay().size() >= 3;
                if(!feelings){
                    Intent intent = new Intent(getContext(), QuestionEmoto.class);
                    startActivity(intent);
                } else {
                    Toast t = Toast.makeText(getContext(), "Je hebt vandaag al 3 keer ingevuld", Toast.LENGTH_LONG);
                    t.show();
                }
            }
        });

        emotesParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeelingsEntityManager fem = MainActivity.fem;
                boolean feelings = fem.getFeelingsForDay(LocalDate.now().toString(), false).getFeelingsForDay().size() >= 3;
                if(!feelings){
                    Intent intent = new Intent(getContext(), QuestionEmoto.class);
                    intent.putExtra("parent", true);
                    startActivity(intent);
                } else {
                    Toast t = Toast.makeText(getContext(), "Je hebt vandaag al 3 keer ingevuld", Toast.LENGTH_LONG);
                    t.show();
                }

            }
        });

        return root;
    }

}