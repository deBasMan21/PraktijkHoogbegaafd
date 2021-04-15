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

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import nl.avans.praktijkhoogbegaafd.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.dal.FeelingEntity;
import nl.avans.praktijkhoogbegaafd.domain.DayFeeling;
import nl.avans.praktijkhoogbegaafd.logic.FeelingsEntityManager;

public class GraphFragment extends Fragment {

    private GraphViewModel graphViewModel;

    private List<FeelingEntity> currentFeeling = new ArrayList<>();
    private List<DayFeeling> dayFeelings = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        graphViewModel =
                new ViewModelProvider(this).get(GraphViewModel.class);
        View root = inflater.inflate(R.layout.fragment_graph, container, false);
        GraphView gv = root.findViewById(R.id.gv_graph);

        new getFeelingForDays().execute();

        if(MainActivity.childrenmode){
            gv.getViewport().setMinX(1);
            gv.getViewport().setMinY(0);
            gv.getViewport().setMaxX(7);
            gv.getViewport().setMaxY(10);
        } else{
            gv.getViewport().setMinX(1);
            gv.getViewport().setMinY(-2);
            gv.getViewport().setMaxX(7);
            gv.getViewport().setMaxY(2);
        }

        gv.getViewport().setYAxisBoundsManual(true);
        gv.getViewport().setXAxisBoundsManual(true);

        gv.getLegendRenderer().setVisible(true);
        gv.getLegendRenderer().setTextSize(25);
        gv.getLegendRenderer().setBackgroundColor(Color.argb(150, 50, 0, 0));
        gv.getLegendRenderer().setTextColor(Color.WHITE);
        gv.getLegendRenderer().setWidth(200);
        gv.getLegendRenderer().setFixedPosition(0, 0);



        String[] categories;
        if(MainActivity.childrenmode){
            String[] categorieschildren = {"Alles", "Emoto", "Fanti", "Intellecto", "Psymo", "Senzo"};
            categories = categorieschildren;
        } else{
            String[] categorieschildren = {"Alles", "Emotionele intensiteit", "Beeldende intensiteit", "Intellectuele intensiteit", "Psychomotorische intensiteit", "Sensorische intensiteit"};
            categories = categorieschildren;
        }
        Spinner spinner = (Spinner) root.findViewById(R.id.sr_graph_category);
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        TextView tv_score_emoto = root.findViewById(R.id.tv_graph_score_emoto);
        TextView tv_score_fanti = root.findViewById(R.id.tv_graph_score_fanti);
        TextView tv_score_intellecto = root.findViewById(R.id.tv_graph_score_intellecto);
        TextView tv_score_psymo = root.findViewById(R.id.tv_graph_score_psymo);
        TextView tv_score_senzo = root.findViewById(R.id.tv_graph_score_senzo);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
                gv.removeAllSeries();
                if(position == 0){
                    LineGraphSeries<DataPoint> emoto = new LineGraphSeries<>();
                    double x = 1;
                    int stats = 0;

                    for (DayFeeling feelings : dayFeelings) {
                        ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
                        double subx = x;
                        for (FeelingEntity feeling : entities) {
                            emoto.appendData(new DataPoint(subx, feeling.getEmoto()), true, 10);
                            stats += feeling.getEmoto();
                            subx += 1.0 / entities.size();
                        }
                        x++;
                    }
                    tv_score_emoto.setText("Score van emotionele intensiteit afgelopen week: " + stats);
                    emoto.setColor(Color.rgb(232, 85, 51));
                    emoto.setDrawDataPoints(true);
                    emoto.setDataPointsRadius(6);
                    if(MainActivity.childrenmode){
                        emoto.setTitle("Emoto");
                        tv_score_emoto.setVisibility(View.INVISIBLE);
                    } else{
                        emoto.setTitle("Emotionele intensiteit");
                        tv_score_emoto.setVisibility(View.VISIBLE);
                    }
                    gv.addSeries(emoto);



                    LineGraphSeries<DataPoint> fanti = new LineGraphSeries<>();
                     x = 1;
                     stats = 0;

                    for (DayFeeling feelings : dayFeelings) {
                        ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
                        double subx = x;
                        for (FeelingEntity feeling : entities) {
                            fanti.appendData(new DataPoint(subx, feeling.getFanti()), true, 10);
                            stats += feeling.getFanti();
                            subx += 1.0 / entities.size();
                        }
                        x++;
                    }
                    tv_score_fanti.setText("Score van beeldende intensiteit afgelopen week: " + stats);
                    fanti.setColor(Color.rgb(98, 176, 74));
                    fanti.setDrawDataPoints(true);
                    fanti.setDataPointsRadius(6);
                    if(MainActivity.childrenmode){
                        fanti.setTitle("Fanti");
                        tv_score_fanti.setVisibility(View.INVISIBLE);
                    } else{
                        fanti.setTitle("Beeldende intensiteit");
                        tv_score_fanti.setVisibility(View.VISIBLE);
                    }
                    gv.addSeries(fanti);




                    LineGraphSeries<DataPoint> intellecto = new LineGraphSeries<>();
                     x = 1;
                     stats = 0;

                    for (DayFeeling feelings : dayFeelings) {
                        ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
                        double subx = x;
                        for (FeelingEntity feeling : entities) {
                            intellecto.appendData(new DataPoint(subx, feeling.getIntellecto()), true, 10);
                            stats += feeling.getIntellecto();
                            subx += 1.0 / entities.size();
                        }
                        x++;
                    }
                    tv_score_intellecto.setText("Score van intellectuele intensiteit afgelopen week: " + stats);
                    intellecto.setColor(Color.rgb(182, 103, 161));
                    intellecto.setDrawDataPoints(true);
                    intellecto.setDataPointsRadius(6);
                    if(MainActivity.childrenmode){
                        intellecto.setTitle("Intellecto");
                        tv_score_intellecto.setVisibility(View.INVISIBLE);
                    } else{
                        intellecto.setTitle("Intellectuele intensiteit");
                        tv_score_intellecto.setVisibility(View.VISIBLE);
                    }
                    gv.addSeries(intellecto);





                    LineGraphSeries<DataPoint> psymo = new LineGraphSeries<>();
                     x = 1;
                     stats = 0;

                    for (DayFeeling feelings : dayFeelings) {
                        ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
                        double subx = x;
                        for (FeelingEntity feeling : entities) {
                            psymo.appendData(new DataPoint(subx, feeling.getPsymo()), true, 10);
                            stats += feeling.getPsymo();
                            subx += 1.0 / entities.size();
                        }
                        x++;
                    }
                    tv_score_psymo.setText("Score van psychomotorische intensiteit afgelopen week: " + stats);
                    psymo.setColor(Color.rgb(81, 102, 169));
                    psymo.setDrawDataPoints(true);
                    psymo.setDataPointsRadius(6);
                    if(MainActivity.childrenmode){
                        psymo.setTitle("Psymo");
                        tv_score_psymo.setVisibility(View.INVISIBLE);
                    } else{
                        psymo.setTitle("Pychomotorische intensiteit");
                        tv_score_psymo.setVisibility(View.VISIBLE);
                    }
                    gv.addSeries(psymo);





                    LineGraphSeries<DataPoint> senzo = new LineGraphSeries<>();
                     x = 1;
                     stats = 0;

                    for (DayFeeling feelings : dayFeelings) {
                        ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
                        double subx = x;
                        for (FeelingEntity feeling : entities) {
                            senzo.appendData(new DataPoint(subx, feeling.getSenzo()), true, 10);
                            stats += feeling.getSenzo();
                            subx += 1.0 / entities.size();
                        }
                        x++;
                    }
                    tv_score_senzo.setText("Score van sensorische intensiteit afgelopen week: " + stats);
                    senzo.setColor(Color.rgb(242, 150, 49));
                    senzo.setDrawDataPoints(true);
                    senzo.setDataPointsRadius(6);
                    if(MainActivity.childrenmode){
                        senzo.setTitle("Senzo");
                        tv_score_senzo.setVisibility(View.INVISIBLE);
                    } else{
                        senzo.setTitle("Sensorische intensiteit");
                        tv_score_senzo.setVisibility(View.VISIBLE);
                    }
                    gv.addSeries(senzo);


                } else if(position == 1){
//                    new getFeelingForDays().execute();

                    LineGraphSeries<DataPoint> emoto = new LineGraphSeries<>();
                    double x = 1;
                    int stats = 0;

                    for (DayFeeling feelings : dayFeelings) {
                        ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
                        double subx = x;
                        for (FeelingEntity feeling : entities) {
                            emoto.appendData(new DataPoint(subx, feeling.getEmoto()), true, 10);
                            stats += feeling.getEmoto();
                            subx += 1.0 / entities.size();
                        }
                        x++;
                    }
                    tv_score_emoto.setText("Score van emotionele intensiteit afgelopen week: " + stats);
                    emoto.setColor(Color.rgb(232, 85, 51));
                    emoto.setDrawDataPoints(true);
                    emoto.setDataPointsRadius(6);
                    if(MainActivity.childrenmode){
                        emoto.setTitle("Emoto");
                        tv_score_emoto.setVisibility(View.INVISIBLE);
                    } else{
                        emoto.setTitle("Emotionele intensiteit");
                        tv_score_emoto.setVisibility(View.VISIBLE);
                    }
                    gv.addSeries(emoto);
                } else if(position == 2){
                    LineGraphSeries<DataPoint> fanti = new LineGraphSeries<>();
                    double x = 1;
                    int stats = 0;

                    for (DayFeeling feelings : dayFeelings) {
                        ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
                        double subx = x;
                        for (FeelingEntity feeling : entities) {
                            fanti.appendData(new DataPoint(subx, feeling.getFanti()), true, 10);
                            stats += feeling.getFanti();
                            subx += 1.0 / entities.size();
                        }
                        x++;
                    }
                    tv_score_fanti.setText("Score van beeldende intensiteit afgelopen week: " + stats);
                    fanti.setColor(Color.rgb(98, 176, 74));
                    fanti.setDrawDataPoints(true);
                    fanti.setDataPointsRadius(6);
                    if(MainActivity.childrenmode){
                        fanti.setTitle("Fanti");
                        tv_score_fanti.setVisibility(View.INVISIBLE);
                    } else{
                        fanti.setTitle("Beeldende intensiteit");
                        tv_score_fanti.setVisibility(View.VISIBLE);
                    }
                    gv.addSeries(fanti);
                } else if (position == 3){
                    LineGraphSeries<DataPoint> intellecto = new LineGraphSeries<>();
                    double x = 1;
                    int stats = 0;

                    for (DayFeeling feelings : dayFeelings) {
                        ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
                        double subx = x;
                        for (FeelingEntity feeling : entities) {
                            intellecto.appendData(new DataPoint(subx, feeling.getIntellecto()), true, 10);
                            stats += feeling.getIntellecto();
                            subx += 1.0 / entities.size();
                        }
                        x++;
                    }
                    tv_score_intellecto.setText("Score van intellectuele intensiteit afgelopen week: " + stats);
                    intellecto.setColor(Color.rgb(182, 103, 161));
                    intellecto.setDrawDataPoints(true);
                    intellecto.setDataPointsRadius(6);
                    if(MainActivity.childrenmode){
                        intellecto.setTitle("Intellecto");
                        tv_score_intellecto.setVisibility(View.INVISIBLE);
                    } else{
                        intellecto.setTitle("Intellectuele intensiteit");
                        tv_score_intellecto.setVisibility(View.VISIBLE);
                    }
                    gv.addSeries(intellecto);
                } else if (position == 4){
                    LineGraphSeries<DataPoint> psymo = new LineGraphSeries<>();
                    double x = 1;
                    int stats = 0;

                    for (DayFeeling feelings : dayFeelings) {
                        ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
                        double subx = x;
                        for (FeelingEntity feeling : entities) {
                            psymo.appendData(new DataPoint(subx, feeling.getPsymo()), true, 10);
                            stats += feeling.getPsymo();
                            subx += 1.0 / entities.size();
                        }
                        x++;
                    }
                    tv_score_psymo.setText("Score van psychomotorische intensiteit afgelopen week: " + stats);
                    psymo.setColor(Color.rgb(81, 102, 169));
                    psymo.setDrawDataPoints(true);
                    psymo.setDataPointsRadius(6);
                    if(MainActivity.childrenmode){
                        psymo.setTitle("Psymo");
                        tv_score_psymo.setVisibility(View.INVISIBLE);
                    } else{
                        psymo.setTitle("Pychomotorische intensiteit");
                        tv_score_psymo.setVisibility(View.VISIBLE);
                    }
                    gv.addSeries(psymo);
                } else if(position == 5){
                    LineGraphSeries<DataPoint> senzo = new LineGraphSeries<>();
                    double x = 1;
                    int stats = 0;

                    for (DayFeeling feelings : dayFeelings) {
                        ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
                        double subx = x;
                        for (FeelingEntity feeling : entities) {
                            senzo.appendData(new DataPoint(subx, feeling.getSenzo()), true, 10);
                            stats += feeling.getSenzo();
                            subx += 1.0 / entities.size();
                        }
                        x++;
                    }
                    tv_score_senzo.setText("Score van sensorische intensiteit afgelopen week: " + stats);
                    senzo.setColor(Color.rgb(242, 150, 49));
                    senzo.setDrawDataPoints(true);
                    senzo.setDataPointsRadius(6);
                    if(MainActivity.childrenmode){
                        senzo.setTitle("Senzo");
                        tv_score_senzo.setVisibility(View.INVISIBLE);
                    } else{
                        senzo.setTitle("Sensorische intensiteit");
                        tv_score_senzo.setVisibility(View.VISIBLE);
                    }
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
                DayFeeling feelings = fem.getFeelingsForDay(LocalDate.now().minusDays(6 - i).toString());
                if(feelings.getFeelingsForDay().size() != 0){
                    dayFeelings.add(feelings);
                }
            }
            return null;
        }
    }
}