package nl.avans.praktijkhoogbegaafd.ui.graph;

import android.graphics.Color;
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

import nl.avans.praktijkhoogbegaafd.R;

public class GraphFragment extends Fragment {

    private GraphViewModel graphViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        graphViewModel =
                new ViewModelProvider(this).get(GraphViewModel.class);
        View root = inflater.inflate(R.layout.fragment_graph, container, false);
        GraphView gv = root.findViewById(R.id.gv_graph);


        try{
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {new DataPoint(1, 1), new DataPoint(2, 2), new DataPoint(3, 3), new DataPoint(4,4), new DataPoint(5,5), new DataPoint(6,6), new DataPoint(7,7)});
            gv.addSeries(series);
        } catch (Exception e){
            e.printStackTrace();
        }

        gv.getViewport().setMinX(0);
        gv.getViewport().setMinY(0);
        gv.getViewport().setMaxX(7);
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

                    LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[] {new DataPoint(1, 1), new DataPoint(2, 2), new DataPoint(3, 3), new DataPoint(4,4), new DataPoint(5,5), new DataPoint(6,6), new DataPoint(7,7)});
                    series1.setColor(Color.rgb(232, 85, 51));
                    series1.setDrawDataPoints(true);
                    series1.setDataPointsRadius(6);
                    series1.setTitle("Emoto");
                    gv.addSeries(series1);

                    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {new DataPoint(1, 1), new DataPoint(2, 2), new DataPoint(3, 9), new DataPoint(4,6), new DataPoint(5,5), new DataPoint(6,4), new DataPoint(7,6)});
                    series2.setColor(Color.rgb(98, 176, 74));
                    series2.setDrawDataPoints(true);
                    series2.setDataPointsRadius(6);
                    series2.setTitle("Fanti");
                    gv.addSeries(series2);

                    LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>(new DataPoint[] {new DataPoint(1, 1), new DataPoint(2, 6), new DataPoint(3, 8), new DataPoint(4,1), new DataPoint(5,3), new DataPoint(6,4), new DataPoint(7,9)});
                    series3.setColor(Color.rgb(182,103, 162));
                    series3.setDrawDataPoints(true);
                    series3.setDataPointsRadius(6);
                    series3.setTitle("Intellecto");
                    gv.addSeries(series3);

                    LineGraphSeries<DataPoint> series4 = new LineGraphSeries<>(new DataPoint[] {new DataPoint(1, 6), new DataPoint(2, 8), new DataPoint(3, 9), new DataPoint(4,10), new DataPoint(5,9), new DataPoint(6,8), new DataPoint(7,6)});
                    series4.setColor(Color.rgb(81, 102, 169));
                    series4.setDrawDataPoints(true);
                    series4.setDataPointsRadius(6);
                    series4.setTitle("Psymo");
                    gv.addSeries(series4);

                    LineGraphSeries<DataPoint> series5 = new LineGraphSeries<>(new DataPoint[] {new DataPoint(1, 2), new DataPoint(2, 7), new DataPoint(3, 1), new DataPoint(4,2), new DataPoint(5,1), new DataPoint(6,2), new DataPoint(7,3)});
                    series5.setColor(Color.rgb(242, 150, 49));
                    series5.setDrawDataPoints(true);
                    series5.setDataPointsRadius(6);
                    series5.setTitle("Senzo");
                    gv.addSeries(series5);

                } else if(position == 1){
                    LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[] {new DataPoint(1, 1), new DataPoint(2, 2), new DataPoint(3, 3), new DataPoint(4,4), new DataPoint(5,5), new DataPoint(6,6), new DataPoint(7,7)});
                    series1.setColor(Color.rgb(232, 85, 51));
                    series1.setDrawDataPoints(true);
                    series1.setDataPointsRadius(6);
                    series1.setTitle("Emoto");
                    gv.addSeries(series1);
                } else if(position == 2){
                    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {new DataPoint(1, 1), new DataPoint(2, 2), new DataPoint(3, 9), new DataPoint(4,6), new DataPoint(5,5), new DataPoint(6,4), new DataPoint(7,6)});
                    series2.setColor(Color.rgb(98, 176, 74));
                    series2.setDrawDataPoints(true);
                    series2.setDataPointsRadius(6);
                    series2.setTitle("Fanti");
                    gv.addSeries(series2);
                } else if (position == 3){
                    LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>(new DataPoint[] {new DataPoint(1, 1), new DataPoint(2, 6), new DataPoint(3, 8), new DataPoint(4,1), new DataPoint(5,3), new DataPoint(6,4), new DataPoint(7,9)});
                    series3.setColor(Color.rgb(182,103, 162));
                    series3.setDrawDataPoints(true);
                    series3.setDataPointsRadius(6);
                    series3.setTitle("Intellecto");
                    gv.addSeries(series3);
                } else if (position == 4){
                    LineGraphSeries<DataPoint> series4 = new LineGraphSeries<>(new DataPoint[] {new DataPoint(1, 6), new DataPoint(2, 8), new DataPoint(3, 9), new DataPoint(4,10), new DataPoint(5,9), new DataPoint(6,8), new DataPoint(7,6)});
                    series4.setColor(Color.rgb(81, 102, 169));
                    series4.setDrawDataPoints(true);
                    series4.setDataPointsRadius(6);
                    series4.setTitle("Psymo");
                    gv.addSeries(series4);
                } else if(position == 5){
                    LineGraphSeries<DataPoint> series5 = new LineGraphSeries<>(new DataPoint[] {new DataPoint(1, 2), new DataPoint(2, 7), new DataPoint(3, 1), new DataPoint(4,2), new DataPoint(5,1), new DataPoint(6,2), new DataPoint(7,3)});
                    series5.setColor(Color.rgb(242, 150, 49));
                    series5.setDrawDataPoints(true);
                    series5.setDataPointsRadius(6);
                    series5.setTitle("Senzo");
                    gv.addSeries(series5);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }
}