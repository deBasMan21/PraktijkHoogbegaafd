package nl.avans.praktijkhoogbegaafd.ui.graph;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import nl.avans.praktijkhoogbegaafd.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.dal.FeelingEntity;
import nl.avans.praktijkhoogbegaafd.logic.FeelingsEntityManager;

public class GraphFragment extends Fragment {

    private GraphViewModel graphViewModel;

    private List<FeelingEntity> currentFeeling = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        graphViewModel =
                new ViewModelProvider(this).get(GraphViewModel.class);
        View root = inflater.inflate(R.layout.fragment_graph, container, false);
        GraphView gv = root.findViewById(R.id.gv_graph);

        new getFeelingForDays().execute();

        try{
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {new DataPoint(1, 1), new DataPoint(2, 2), new DataPoint(3, 3), new DataPoint(4,4), new DataPoint(5,5), new DataPoint(6,6), new DataPoint(7,7)});
            gv.addSeries(series);
        } catch (Exception e){
            e.printStackTrace();
        }

        gv.getViewport().setMinX(0);
        gv.getViewport().setMinY(0);
        gv.getViewport().setMaxX(6);
        gv.getViewport().setMaxY(10);

        gv.getViewport().setYAxisBoundsManual(true);
        gv.getViewport().setXAxisBoundsManual(true);

        gv.getLegendRenderer().setVisible(true);
        gv.getLegendRenderer().setTextSize(25);
        gv.getLegendRenderer().setBackgroundColor(Color.argb(150, 50, 0, 0));
        gv.getLegendRenderer().setTextColor(Color.WHITE);
        gv.getLegendRenderer().setWidth(200);
        gv.getLegendRenderer().setFixedPosition(0, 0);



        String[] categories = {"Alles", "Emoto", "Fanti", "Intellecto", "Psymo", "Senzo"};
        Spinner spinner = (Spinner) root.findViewById(R.id.sr_graph_category);
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
                gv.removeAllSeries();
                if(position == 0){

                    LineGraphSeries<DataPoint> emoto = new LineGraphSeries<>();
                    int x = 0;
                    for (FeelingEntity fe : currentFeeling) {
                        emoto.appendData(new DataPoint(x, fe.getEmoto()), true, 10);
                        x++;
                    }
                    emoto.setColor(Color.rgb(232, 85, 51));
                    emoto.setDrawDataPoints(true);
                    emoto.setDataPointsRadius(6);
                    emoto.setTitle("Emoto");
                    gv.addSeries(emoto);


                    LineGraphSeries<DataPoint> fanti = new LineGraphSeries<>();
                    x = 0;
                    for (FeelingEntity fe : currentFeeling) {
                        fanti.appendData(new DataPoint(x, fe.getFanti()), true, 10);
                        x++;
                    }
                    fanti.setColor(Color.rgb(98, 176, 74));
                    fanti.setDrawDataPoints(true);
                    fanti.setDataPointsRadius(6);
                    fanti.setTitle("Fanti");
                    gv.addSeries(fanti);

                    LineGraphSeries<DataPoint> intellecto = new LineGraphSeries<>();
                    x = 0;
                    for (FeelingEntity fe : currentFeeling) {
                        intellecto.appendData(new DataPoint(x, fe.getIntellecto()), true, 10);
                        x++;
                    }
                    intellecto.setColor(Color.rgb(182,103, 162));
                    intellecto.setDrawDataPoints(true);
                    intellecto.setDataPointsRadius(6);
                    intellecto.setTitle("Intellecto");
                    gv.addSeries(intellecto);

                    LineGraphSeries<DataPoint> psymo = new LineGraphSeries<>();
                    x = 0;
                    for (FeelingEntity fe : currentFeeling) {
                        psymo.appendData(new DataPoint(x, fe.getPsymo()), true, 10);
                        x++;
                    }
                    psymo.setColor(Color.rgb(81, 102, 169));
                    psymo.setDrawDataPoints(true);
                    psymo.setDataPointsRadius(6);
                    psymo.setTitle("Psymo");
                    gv.addSeries(psymo);

                    LineGraphSeries<DataPoint> senzo = new LineGraphSeries<>();
                    x = 0;
                    for (FeelingEntity fe : currentFeeling) {
                        senzo.appendData(new DataPoint(x, fe.getSenzo()), true, 10);
                        x++;
                    }
                    senzo.setColor(Color.rgb(242, 150, 49));
                    senzo.setDrawDataPoints(true);
                    senzo.setDataPointsRadius(6);
                    senzo.setTitle("Senzo");
                    gv.addSeries(senzo);

                } else if(position == 1){
                    LineGraphSeries<DataPoint> emoto = new LineGraphSeries<>();
                    int x = 0;
                    for (FeelingEntity fe : currentFeeling) {
                        emoto.appendData(new DataPoint(x, fe.getEmoto()), true, 10);
                        x++;
                    }
                    emoto.setColor(Color.rgb(232, 85, 51));
                    emoto.setDrawDataPoints(true);
                    emoto.setDataPointsRadius(6);
                    emoto.setTitle("Emoto");
                    gv.addSeries(emoto);
                } else if(position == 2){
                    LineGraphSeries<DataPoint> fanti = new LineGraphSeries<>();
                    int x = 0;
                    for (FeelingEntity fe : currentFeeling) {
                        fanti.appendData(new DataPoint(x, fe.getFanti()), true, 10);
                        x++;
                    }
                    fanti.setColor(Color.rgb(98, 176, 74));
                    fanti.setDrawDataPoints(true);
                    fanti.setDataPointsRadius(6);
                    fanti.setTitle("Fanti");
                    gv.addSeries(fanti);
                } else if (position == 3){
                    LineGraphSeries<DataPoint> intellecto = new LineGraphSeries<>();
                    int x = 0;
                    for (FeelingEntity fe : currentFeeling) {
                        intellecto.appendData(new DataPoint(x, fe.getIntellecto()), true, 10);
                        x++;
                    }
                    intellecto.setColor(Color.rgb(182,103, 162));
                    intellecto.setDrawDataPoints(true);
                    intellecto.setDataPointsRadius(6);
                    intellecto.setTitle("Intellecto");
                    gv.addSeries(intellecto);
                } else if (position == 4){
                    LineGraphSeries<DataPoint> psymo = new LineGraphSeries<>();
                    int x = 0;
                    for (FeelingEntity fe : currentFeeling) {
                        psymo.appendData(new DataPoint(x, fe.getPsymo()), true, 10);
                        x++;
                    }
                    psymo.setColor(Color.rgb(81, 102, 169));
                    psymo.setDrawDataPoints(true);
                    psymo.setDataPointsRadius(6);
                    psymo.setTitle("Psymo");
                    gv.addSeries(psymo);
                } else if(position == 5){
                    LineGraphSeries<DataPoint> senzo = new LineGraphSeries<>();
                    int x = 0;
                    for (FeelingEntity fe : currentFeeling) {
                        senzo.appendData(new DataPoint(x, fe.getSenzo()), true, 10);
                        x++;
                    }
                    senzo.setColor(Color.rgb(242, 150, 49));
                    senzo.setDrawDataPoints(true);
                    senzo.setDataPointsRadius(6);
                    senzo.setTitle("Senzo");
                    gv.addSeries(senzo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }

    public class getFeelingForDays extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            FeelingsEntityManager fem = MainActivity.fem;
            for(int i = 0; i < 7; i++){
                FeelingEntity fe = fem.getFeelingsForDay(LocalDate.now().minusDays(6).plusDays(i).toString());
                if(fe != null){
                    currentFeeling.add(fe);
                }
            }
            return null;
        }
    }
}