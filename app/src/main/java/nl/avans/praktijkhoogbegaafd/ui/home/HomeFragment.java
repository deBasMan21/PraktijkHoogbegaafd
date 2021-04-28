package nl.avans.praktijkhoogbegaafd.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import nl.avans.praktijkhoogbegaafd.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;
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
                Intent intent = new Intent(getContext(), QuestionEmoto.class);
                startActivity(intent);
            }
        });

        emotesParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), QuestionEmoto.class);
                intent.putExtra("parent", true);
                startActivity(intent);
            }
        });

        return root;
    }
}