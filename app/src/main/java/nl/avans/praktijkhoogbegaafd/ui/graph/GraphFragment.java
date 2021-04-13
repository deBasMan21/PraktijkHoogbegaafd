package nl.avans.praktijkhoogbegaafd.ui.graph;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        return root;
    }
}