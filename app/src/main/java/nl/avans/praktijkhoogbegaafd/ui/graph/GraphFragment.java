package nl.avans.praktijkhoogbegaafd.ui.graph;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import nl.avans.praktijkhoogbegaafd.ui.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.dal.FeelingEntity;
import nl.avans.praktijkhoogbegaafd.domain.DayFeeling;
import nl.avans.praktijkhoogbegaafd.logic.FeelingsEntityManager;
import nl.avans.praktijkhoogbegaafd.logic.ScreenshotLogic;
import nl.avans.praktijkhoogbegaafd.ui.ShareActivity;
import nl.avans.praktijkhoogbegaafd.ui.home.HomeFragment;

public class GraphFragment extends Fragment {

    private GraphViewModel graphViewModel;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<FeelingEntity> currentFeeling = new ArrayList<>();
    private List<DayFeeling> dayFeelings = new ArrayList<>();

    private boolean parental = true;
    private boolean firstTime = true;
    private GraphView gv;

    private Double[] weekStats = new Double[5];
    private Double[] dayStats = new Double[5];

    private boolean isEmailIntentStarted = false;

    private String[] categoriesChildren = {"Alles", "Emoto", "Fanti", "Intellecto", "Psymo", "Senzo"};
    private String[] categoriesAdult = {"Alles", "Emotionele intensiteit", "Beeldende intensiteit", "Intellectuele intensiteit", "Psychomotorische intensiteit", "Sensorische intensiteit"};

    private ArrayAdapter<String> adapterChildren = null;
    private ArrayAdapter<String> adapterAdult = null;

    private File file;

    private View root;
    private int position;

    private ProgressBar pb;

    private double emoto = 0;
    private double fanti = 0;
    private double intellecto = 0;
    private double psymo = 0;
    private double senzo = 0;

    private String[] names = {"completeView", "Emoto", "Fanti", "Intellecto", "Psymo", "Senzo" };

    private Bitmap ssEmoto = null;
    private Bitmap ssFanti = null;
    private Bitmap ssIntellecto = null;
    private Bitmap ssPsymo = null;
    private Bitmap ssSenzo = null;
    private Bitmap ssTotal = null;

    private TextView tv_score_emoto;
    private TextView tv_score_fanti;
    private TextView tv_score_intellecto;
    private TextView tv_score_psymo;
    private TextView tv_score_senzo;
    private TextView tv_text_emoto;
    private TextView tv_text_fanti;
    private TextView tv_text_intellecto;
    private TextView tv_text_psymo;
    private TextView tv_text_senzo;

    private ScrollView sv;

    private boolean ssDone = false;
    private int name;

    private boolean weekOrDayStats = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        graphViewModel =
                new ViewModelProvider(this).get(GraphViewModel.class);
        if(MainActivity.childrenmode){
            root = inflater.inflate(R.layout.fragment_graph, container, false);
        } else{
            root = inflater.inflate(R.layout.fragment_graph_adult, container, false);
        }

        sv = root.findViewById(R.id.sv);

        gv = root.findViewById(R.id.gv_graph);

        System.out.println(getToolBarHeight(getContext()) + "px heigh");

        pb = root.findViewById(R.id.pb_graph_loading);

        adapterAdult = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, categoriesAdult);
        adapterChildren = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, categoriesChildren);

        makeGraph(0);

        System.out.println("hier maakt hij een nieuwe grafiek");

        ToggleButton tb = root.findViewById(R.id.tb_graph_switch);
        ToggleButton tbStats = root.findViewById(R.id.tb_stats_switch);
        if(MainActivity.childrenmode){
            tb.setVisibility(View.VISIBLE);
        } else {
            tb.setVisibility(View.INVISIBLE);
        }


        Spinner spinner = (Spinner) root.findViewById(R.id.sr_graph_category);
        ArrayAdapter<String> adapter;
        if(MainActivity.childrenmode && parental){
            adapter = this.adapterChildren;
        } else {
            adapter = this.adapterAdult;
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstTime = false;
                makeGraph(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                firstTime = false;
                makeGraph(spinner.getSelectedItemPosition());
            }
        });

        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton button = (ToggleButton) v;
                firstTime = false;
                if(button.isChecked()){
                    parental = false;
                    spinner.setAdapter(adapterAdult);
                    System.out.println(spinner.getSelectedItemPosition());
                    makeGraph(spinner.getSelectedItemPosition());
                } else {
                    parental = true;
                    spinner.setAdapter(adapterChildren);
                    System.out.println(spinner.getSelectedItemPosition());
                    makeGraph(spinner.getSelectedItemPosition());
                }
                setStats(weekOrDayStats);
            }
        });

        tv_score_emoto = root.findViewById(R.id.tv_graph_score_emoto);
        tv_score_fanti = root.findViewById(R.id.tv_graph_score_fanti);
        tv_score_intellecto = root.findViewById(R.id.tv_graph_score_intellecto);
        tv_score_psymo = root.findViewById(R.id.tv_graph_score_psymo);
        tv_score_senzo = root.findViewById(R.id.tv_graph_score_senzo);

        tv_text_emoto = root.findViewById(R.id.tv_graph_text_emoto);
        tv_text_fanti = root.findViewById(R.id.tv_graph_text_fanti);
        tv_text_intellecto = root.findViewById(R.id.tv_graph_text_intellecto);
        tv_text_senzo = root.findViewById(R.id.tv_graph_text_psymo);
        tv_text_psymo = root.findViewById(R.id.tv_graph_text_senzo);



        tbStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton toggleButton = (ToggleButton) v;
                weekOrDayStats = toggleButton.isChecked();
                setStats(toggleButton.isChecked());
            }
        });

        if(firstTime){
            gv.getLegendRenderer().setVisible(true);
        }

        CheckBox cb = root.findViewById(R.id.cb_graph_legend);
        cb.setChecked(true);

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    gv.getLegendRenderer().setVisible(true);
                    makeGraph(position);
                } else{
                    gv.getLegendRenderer().setVisible(false);
                    makeGraph(position);
                }
            }
        });

        Button share = root.findViewById(R.id.bn_graph_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                isEmailIntentStarted = true;
                share.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                cb.setVisibility(View.INVISIBLE);
                ssDone = false;
                name = 0;
                makeGraph(0);


            }
        });


        return root;
    }

    public void makeGraph(int position){
        gv.removeAllSeries();
        this.position = position;
        new getFeelingForDays().execute();
    }

    public void setStats(boolean weekOrDay){
        if(weekOrDay){
            if(MainActivity.childrenmode && parental){
                tv_text_emoto.setText("Gemiddelde emoto afgelopen dag: ");
                tv_score_emoto.setText(round(dayStats[0], 2) + "");

                tv_text_fanti.setText("Gemiddelde fanti afgelopen dag: ");
                tv_score_fanti.setText(round(dayStats[1], 2) + "");

                tv_text_intellecto.setText("Gemiddelde intellecto afgelopen dag: ");
                tv_score_intellecto.setText(round(dayStats[2], 2) + "");

                tv_text_psymo.setText("Gemiddelde psymo afgelopen dag: ");
                tv_score_psymo.setText(round(dayStats[3], 2) + "");

                tv_text_senzo.setText("Gemiddelde senzo afgelopen dag: ");
                tv_score_senzo.setText(round(dayStats[4], 2) + "");
            } else {
                tv_text_emoto.setText("Gemiddelde emotionele intensiteit afgelopen dag: ");
                tv_score_emoto.setText(round(dayStats[0], 2) + "");

                tv_text_fanti.setText("Gemiddelde beeldende intensiteit afgelopen dag: ");
                tv_score_fanti.setText(round(dayStats[1], 2) + "");

                tv_text_intellecto.setText("Gemiddelde intellectuele intensiteit afgelopen dag: ");
                tv_score_intellecto.setText(round(dayStats[2], 2) + "");

                tv_text_psymo.setText("Gemiddelde pyschomotorische intensiteit afgelopen dag: ");
                tv_score_psymo.setText(round(dayStats[3], 2) + "");

                tv_text_senzo.setText("Gemiddelde sensorische intensiteit afgelopen dag: ");
                tv_score_senzo.setText(round(dayStats[4], 2) + "");
            }
        }else {
            if(MainActivity.childrenmode && parental){
                tv_text_emoto.setText("Gemiddelde emoto afgelopen week: ");
                tv_score_emoto.setText(round(weekStats[0],2) + "");

                tv_text_fanti.setText("Gemiddelde fanti afgelopen week: ");
                tv_score_fanti.setText(round(weekStats[1], 2) + "");

                tv_text_intellecto.setText("Gemiddelde intellecto afgelopen week: ");
                tv_score_intellecto.setText(round(weekStats[2], 2) + "");

                tv_text_psymo.setText("Gemiddelde psymo afgelopen week: ");
                tv_score_psymo.setText(round(weekStats[3],2) + "");

                tv_text_senzo.setText("Gemiddelde senzo afgelopen week: ");
                tv_score_senzo.setText(round(weekStats[4], 2) + "");
            } else {
                tv_text_emoto.setText("Gemiddelde emotionele intensiteit afgelopen week: ");
                tv_score_emoto.setText(round(weekStats[0], 2) + "");

                tv_text_fanti.setText("Gemiddelde beeldende intensiteit afgelopen week: ");
                tv_score_fanti.setText(round(weekStats[1], 2) + "");

                tv_text_intellecto.setText("Gemiddelde intellectuele intensiteit afgelopen week: ");
                tv_score_intellecto.setText(round(weekStats[2], 2) + "");

                tv_text_psymo.setText("Gemiddelde pyschomotorische intensiteit afgelopen week: ");
                tv_score_psymo.setText(round(weekStats[3], 2) + "");

                tv_text_senzo.setText("Gemiddelde sensorische intensiteit afgelopen week: ");
                tv_score_senzo.setText(round(weekStats[4], 2) + "");
            }
        }
    }

    public static double round(Double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        if(!Double.isNaN(value)){
            BigDecimal bd = BigDecimal.valueOf(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } else {
            return Double.NaN;
        }
    }

    public void makeGraphView(){

        if(MainActivity.childrenmode && parental){
            gv.getViewport().setMinX(1);
            gv.getViewport().setMinY(0);
            gv.getViewport().setMaxX(7);
            gv.getViewport().setMaxY(10);
        } else if (!parental) {
            gv.getViewport().setMinX(1);
            gv.getViewport().setMinY(-2);
            gv.getViewport().setMaxX(7);
            gv.getViewport().setMaxY(2);
        } else {
            gv.getViewport().setMinX(1);
            gv.getViewport().setMinY(-2);
            gv.getViewport().setMaxX(14);
            gv.getViewport().setMaxY(2);
        }

        gv.getViewport().setYAxisBoundsManual(true);
        gv.getViewport().setXAxisBoundsManual(true);

//        gv.getGridLabelRenderer().setVerticalAxisTitle("Waarde");
//        gv.getGridLabelRenderer().setHorizontalAxisTitle("Dagen");
//        gv.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);



        gv.getLegendRenderer().setTextSize(25);
        gv.getLegendRenderer().setBackgroundColor(Color.argb(150, 50, 0, 0));
        gv.getLegendRenderer().setTextColor(Color.WHITE);
        gv.getLegendRenderer().setWidth(200);
        gv.getLegendRenderer().setFixedPosition(0, 0);
        if(isEmailIntentStarted){
            gv.getLegendRenderer().setVisible(false);
        }








        System.out.println(position);
        gv.removeAllSeries();
        if(position == 0){
            LineGraphSeries<DataPoint> emoto = createEmoto();
            if(MainActivity.childrenmode && parental){
                emoto.setTitle("Emoto");
            } else{
                emoto.setTitle("Emotionele intensiteit");
            }
            gv.addSeries(emoto);



            LineGraphSeries<DataPoint> fanti = createFanti();

            if(MainActivity.childrenmode && parental){
                fanti.setTitle("Fanti");
            } else{
                fanti.setTitle("Beeldende intensiteit");
            }
            gv.addSeries(fanti);




            LineGraphSeries<DataPoint> intellecto = createIntellecto();

            if(MainActivity.childrenmode && parental){
                intellecto.setTitle("Intellecto");
            } else{
                intellecto.setTitle("Intellectuele intensiteit");
            }
            gv.addSeries(intellecto);


            LineGraphSeries<DataPoint> psymo = createPsymo();

            if(MainActivity.childrenmode && parental){
                psymo.setTitle("Psymo");
            } else{
                psymo.setTitle("Pychomotorische intensiteit");
            }
            gv.addSeries(psymo);


            LineGraphSeries<DataPoint> senzo = createSenzo();
            if(MainActivity.childrenmode && parental){
                senzo.setTitle("Senzo");
            } else{
                senzo.setTitle("Sensorische intensiteit");
            }
            gv.addSeries(senzo);


        } else if(position == 1){
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> emoto = createEmoto();
            if(MainActivity.childrenmode && parental){
                emoto.setTitle("Emoto");
            } else{
                emoto.setTitle("Emotionele intensiteit");
            }
            gv.addSeries(emoto);
        } else if(position == 2){
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> fanti = createFanti();

            if(MainActivity.childrenmode && parental){
                fanti.setTitle("Fanti");
            } else{
                fanti.setTitle("Beeldende intensiteit");
            }
            gv.addSeries(fanti);
        } else if (position == 3){
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> intellecto = createIntellecto();

            if(MainActivity.childrenmode && parental){
                intellecto.setTitle("Intellecto");
            } else{
                intellecto.setTitle("Intellectuele intensiteit");
            }
            gv.addSeries(intellecto);
        } else if (position == 4){
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> psymo = createPsymo();

            if(MainActivity.childrenmode && parental){
                psymo.setTitle("Psymo");
            } else{
                psymo.setTitle("Pychomotorische intensiteit");
            }
            gv.addSeries(psymo);
        } else if(position == 5){
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> senzo = createSenzo();
            if(MainActivity.childrenmode && parental){
                senzo.setTitle("Senzo");
            } else{
                senzo.setTitle("Sensorische intensiteit");
            }
            gv.addSeries(senzo);
        }

        setStats(weekOrDayStats);

        if(isEmailIntentStarted){
            sv.smoothScrollTo(0,0);
            storeScreenshot(ScreenshotLogic.takescreenshotOfRootView(root), names[name]);
            if(name < 5){
                name++;
                makeGraph(name);

            } else{
                startEmail();
            }
        }

    }

    public LineGraphSeries<DataPoint> createSenzo(){
        LineGraphSeries<DataPoint> senzo = new LineGraphSeries<>();
        double x = 1;
        int stats = 0;
        int amountOfValues = 0;
        double finalStats = 0;
        int lastDayValue = 0;
        int amountOfValuesLastDay = 0;

        for (DayFeeling feelings : dayFeelings) {
            ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
            lastDayValue = 0;
            amountOfValuesLastDay = 0;
            double subx = x;
            for (FeelingEntity feeling : entities) {
                senzo.appendData(new DataPoint(subx, feeling.getSenzo()), true, 14);
                stats += feeling.getSenzo();
                subx += 1.0 / entities.size();
                amountOfValues++;
                lastDayValue += feeling.getSenzo();
                amountOfValuesLastDay++;
            }
            x++;
        }
        if(MainActivity.childrenmode && parental){
            finalStats = 1.0 * stats / amountOfValues;
            this.weekStats[4] = finalStats;
            this.dayStats[4] = 1.0 * lastDayValue / amountOfValuesLastDay;
        } else {
            finalStats = 1.0 * stats;
            this.weekStats[4] = finalStats;
            this.dayStats[4] = 1.0 * lastDayValue;
        }
        this.senzo = finalStats;
        senzo.setColor(Color.rgb(242, 150, 49));
        senzo.setDrawDataPoints(true);
        senzo.setDataPointsRadius(6);
        Log.d(getClass().getSimpleName() + ": " + LocalDate.now().toString(), this.weekStats[4].toString());
        Log.d(getClass().getSimpleName() + ": " + LocalDate.now().toString(), this.dayStats[4].toString());
        return senzo;
    }

    public LineGraphSeries<DataPoint> createPsymo(){
        LineGraphSeries<DataPoint> psymo = new LineGraphSeries<>();
        double x = 1;
        int stats = 0;
        int amountOfValues = 0;
        double finalStats = 0;
        int lastDayValue = 0;
        int amountOfValuesLastDay = 0;

        for (DayFeeling feelings : dayFeelings) {
            ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
            double subx = x;
            lastDayValue = 0;
            amountOfValuesLastDay = 0;
            for (FeelingEntity feeling : entities) {
                psymo.appendData(new DataPoint(subx, feeling.getPsymo()), true, 14);
                stats += feeling.getPsymo();
                subx += 1.0 / entities.size();
                amountOfValues++;
                lastDayValue += feeling.getPsymo();
                amountOfValuesLastDay++;
            }
            x++;
        }
        if(MainActivity.childrenmode && parental){
            finalStats = 1.0 * stats / amountOfValues;
            this.weekStats[3] = finalStats;
            this.dayStats[3] = 1.0 * lastDayValue / amountOfValuesLastDay;
        } else {
            finalStats = stats;
            this.weekStats[3] = finalStats;
            this.dayStats[3] = 1.0 * lastDayValue;
        }
        this.psymo = finalStats;
        psymo.setColor(Color.rgb(81, 102, 169));
        psymo.setDrawDataPoints(true);
        psymo.setDataPointsRadius(6);
        return psymo;
    }

    public LineGraphSeries<DataPoint> createIntellecto(){
        LineGraphSeries<DataPoint> intellecto = new LineGraphSeries<>();
        double x = 1;
        int stats = 0;
        int amountOfValues = 0;
        double finalStats = 0;
        int lastDayValue = 0;
        int amountOfValuesLastDay = 0;

        for (DayFeeling feelings : dayFeelings) {
            ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
            double subx = x;
            lastDayValue = 0;
            amountOfValuesLastDay = 0;
            for (FeelingEntity feeling : entities) {
                intellecto.appendData(new DataPoint(subx, feeling.getIntellecto()), true, 14);
                stats += feeling.getIntellecto();
                subx += 1.0 / entities.size();
                amountOfValues++;
                lastDayValue += feeling.getIntellecto();
                amountOfValuesLastDay++;
            }
            x++;
        }
        if(MainActivity.childrenmode && parental){
            finalStats = 1.0 * stats / amountOfValues;
            this.weekStats[2] = finalStats;
            this.dayStats[2] = 1.0 * lastDayValue / amountOfValuesLastDay;
        } else {
            finalStats = 1.0 * stats;
            this.weekStats[2] = finalStats;
            this.dayStats[2] = 1.0 * lastDayValue;
        }
        this.intellecto = finalStats;
        intellecto.setColor(Color.rgb(182, 103, 161));
        intellecto.setDrawDataPoints(true);
        intellecto.setDataPointsRadius(6);
        return intellecto;
    }

    public LineGraphSeries<DataPoint> createFanti(){
        LineGraphSeries<DataPoint> fanti = new LineGraphSeries<>();
        double x = 1;
        int stats = 0;
        int amountOfValues = 0;
        double finalStats = 0;
        int lastDayValue = 0;
        int amountOfValuesLastDay = 0;

        for (DayFeeling feelings : dayFeelings) {
            ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
            double subx = x;
            lastDayValue = 0;
            amountOfValuesLastDay = 0;
            for (FeelingEntity feeling : entities) {
                fanti.appendData(new DataPoint(subx, feeling.getFanti()), true, 14);
                stats += feeling.getFanti();
                subx += 1.0 / entities.size();
                amountOfValues++;
                lastDayValue += feeling.getFanti();
                amountOfValuesLastDay++;
            }
            x++;
        }
        if(MainActivity.childrenmode && parental){
            finalStats = 1.0 * stats / amountOfValues;
            this.weekStats[1] = finalStats;
            this.dayStats[1] = 1.0 * lastDayValue / amountOfValuesLastDay;
        } else {
            finalStats = 1.0 * stats;
            this.weekStats[1] = finalStats;
            this.dayStats[1] = 1.0 * lastDayValue;
        }
        this.fanti = finalStats;
        fanti.setColor(Color.rgb(98, 176, 74));
        fanti.setDrawDataPoints(true);
        fanti.setDataPointsRadius(6);
        return fanti;
    }

    public LineGraphSeries<DataPoint> createEmoto(){
        LineGraphSeries<DataPoint> emoto = new LineGraphSeries<>();
        double x = 1;
        int stats = 0;
        int amountOfValues = 0;
        double finalStats = 0;
        int lastDayValue = 0;
        int amountOfValuesLastDay = 0;

        for (DayFeeling feelings : dayFeelings) {
            ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
            double subx = x;
            lastDayValue = 0;
            amountOfValuesLastDay = 0;
            for (FeelingEntity feeling : entities) {
                emoto.appendData(new DataPoint(subx, feeling.getEmoto()), true, 14);
                stats += feeling.getEmoto();
                subx += 1.0 / entities.size();
                amountOfValues++;
                lastDayValue = feeling.getEmoto();
                amountOfValuesLastDay++;
            }
            x++;

        }
        if(MainActivity.childrenmode && parental){
             finalStats = 1.0 * stats / amountOfValues;
            this.weekStats[0] = finalStats;
            this.dayStats[0] = 1.0 * lastDayValue / amountOfValuesLastDay;
        } else {
            finalStats = 1.0 * stats;
            this.weekStats[0] = finalStats;
            this.dayStats[0] = 1.0 * lastDayValue;
        }
        this.emoto = finalStats;
        emoto.setColor(Color.rgb(232, 85, 51));
        emoto.setDrawDataPoints(true);
        emoto.setDataPointsRadius(6);
        return emoto;
    }

    public void storeScreenshot(Bitmap bitmap, String filename) {
        if(filename.equals("Emoto")){
            this.ssEmoto = bitmap;
        } else if (filename.equals("Fanti")){
            this.ssFanti = bitmap;
        } else if(filename.equals("Intellecto")){
            this.ssIntellecto = bitmap;
        } else if (filename.equals("Psymo")){
            this.ssPsymo = bitmap;
        } else if (filename.equals("Senzo")){
            this.ssSenzo = bitmap;
        } else if(filename.equals("completeView")){
            this.ssTotal = bitmap;
        }
    }

    private static int toolBarHeight = -1;

    public static int getToolBarHeight(Context context) {
        if (toolBarHeight > 0) {
            return toolBarHeight;
        }
        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("action_bar_size", "dimen", "android");
        toolBarHeight = resourceId > 0 ?
                resources.getDimensionPixelSize(resourceId) :
                (int) convertDpToPixel(context, 56);
        return toolBarHeight;
    }

    public static float convertDpToPixel(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public File createPDF(){
        PdfDocument doc = new PdfDocument();
        int width = (gv.getWidth()) * 3;
        int height = (gv.getHeight()) * 2 + 600;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height + 500, 1).create();
        gv.getWidth();
        gv.getHeight();

        int differenceWidth = ssTotal.getWidth() - gv.getWidth();

        int headerHeight = getToolBarHeight(getContext()) + 280;


        PdfDocument.Page page = doc.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        Bitmap totalGraph = this.ssTotal;
        Bitmap total = Bitmap.createBitmap(totalGraph, differenceWidth / 2, headerHeight, totalGraph.getWidth() - differenceWidth, totalGraph.getHeight() - (totalGraph.getHeight() - gv.getHeight()));
        Bitmap emotoSS = this.ssEmoto;
        Bitmap emoto = Bitmap.createBitmap(emotoSS, differenceWidth / 2, headerHeight, totalGraph.getWidth() - differenceWidth, totalGraph.getHeight() - (totalGraph.getHeight() - gv.getHeight()));
        Bitmap fantiSS = this.ssFanti;
        Bitmap fanti = Bitmap.createBitmap(fantiSS, differenceWidth / 2, headerHeight, totalGraph.getWidth() - differenceWidth, totalGraph.getHeight() - (totalGraph.getHeight() - gv.getHeight()));
        Bitmap intellectoSS = this.ssIntellecto;
        Bitmap intellecto = Bitmap.createBitmap(intellectoSS,  differenceWidth / 2, headerHeight, totalGraph.getWidth() - differenceWidth, totalGraph.getHeight() - (totalGraph.getHeight() - gv.getHeight()));
        Bitmap psymoSS = this.ssPsymo;
        Bitmap psymo = Bitmap.createBitmap(psymoSS, differenceWidth / 2, headerHeight, totalGraph.getWidth() - differenceWidth, totalGraph.getHeight() - (totalGraph.getHeight() - gv.getHeight()));
        Bitmap senzoSS = this.ssSenzo;
        Bitmap senzo = Bitmap.createBitmap(senzoSS, differenceWidth / 2, headerHeight, totalGraph.getWidth() - differenceWidth, totalGraph.getHeight() - (totalGraph.getHeight() - gv.getHeight()));

        Bitmap legend = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_phr_legenda_foreground);
        Bitmap logo = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_phr_logo_large_foreground);
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, 800, 800, false);


        canvas.drawBitmap(total, 0, logo.getHeight() + 210, paint);
        canvas.drawBitmap(emoto, total.getWidth(), logo.getHeight() + 210, paint);
        canvas.drawBitmap(fanti, emoto.getWidth() * 2, logo.getHeight() + 210, paint);
        canvas.drawBitmap(intellecto, 0, total.getHeight() + logo.getHeight() + 210, paint);
        canvas.drawBitmap(psymo, intellecto.getWidth(), total.getHeight() + logo.getHeight() + 210, paint);
        canvas.drawBitmap(senzo, intellecto.getWidth() * 2, total.getHeight() + logo.getHeight() + 210, paint);
        canvas.drawBitmap(scaledLogo, (width - scaledLogo.getWidth()) / 2, -200, paint);
        canvas.drawBitmap(legend, width - legend.getWidth(), logo.getHeight() - 100, paint);
        if(MainActivity.childrenmode){
            canvas.drawText(MainActivity.name + " " + MainActivity.birthDay + " (Ouder: " + MainActivity.parentalName + ")", 20, logo.getHeight(), paint);
        } else{
            canvas.drawText(MainActivity.name + " " + MainActivity.birthDay , 20, logo.getHeight(), paint);
        }
        canvas.drawText("Datum vanaf: " + LocalDate.now().minusDays(6).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) ,20, logo.getHeight() + 60, paint);
        canvas.drawText("Datum tot en met: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), 20, logo.getHeight() + 110, paint);
        canvas.drawText(MainActivity.begeleidster, 20, logo.getHeight() + 160, paint);

        canvas.drawText("Weekstatistieken:", 20, height + 60, paint);
        canvas.drawText("Gemiddelde emotionele intensiteit afgelopen week:", 20, height + 120, paint);
        canvas.drawText("Gemiddelde beeldende intensiteit afgelopen week:", 20, height + 180, paint);
        canvas.drawText("Gemiddelde intellectuele- intensiteit afgelopen week:", 20, height + 240, paint);
        canvas.drawText("Gemiddelde psychomotorische intensiteit afgelopen week:", 20, height + 300, paint);
        canvas.drawText("Gemiddelde sensorische intensiteit afgelopen week:", 20, height + 360, paint);

        canvas.drawText(round(weekStats[0], 2) + "", 1400, height + 120, paint);
        canvas.drawText(round(weekStats[1], 2) + "", 1400, height + 180, paint);
        canvas.drawText(round(weekStats[2], 2) + "", 1400, height + 240, paint);
        canvas.drawText(round(weekStats[3], 2) + "", 1400, height + 300, paint);
        canvas.drawText(round(weekStats[4], 2) + "", 1400, height + 360, paint);


        canvas.drawText("Dagstatistieken van " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ":", 1800, height + 60, paint);
        canvas.drawText("Gemiddelde emotionele intensiteit afgelopen dag:", 1800, height + 120, paint);
        canvas.drawText("Gemiddelde beeldende intensiteit afgelopen dag:", 1800, height + 180, paint);
        canvas.drawText("Gemiddelde intellectuele- intensiteit afgelopen dag:", 1800, height + 240, paint);
        canvas.drawText("Gemiddelde psychomotorische intensiteit afgelopen dag:", 1800, height + 300, paint);
        canvas.drawText("Gemiddelde sensorische intensiteit afgelopen dag:", 1800, height + 360, paint);

        canvas.drawText(round(dayStats[0], 2) + "", 3200, height + 120, paint);
        canvas.drawText(round(dayStats[1], 2) + "", 3200, height + 180, paint);
        canvas.drawText(round(dayStats[2], 2) + "", 3200, height + 240, paint);
        canvas.drawText(round(dayStats[3], 2) + "", 3200, height + 300, paint);
        canvas.drawText(round(dayStats[4], 2) + "", 3200, height + 360, paint);



        doc.finishPage(page);


        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, MainActivity.name + "(" + LocalDate.now() + ").pdf");
        file.setReadable(true);
        this.file = file;
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            doc.writeTo(fos);
            fos.flush();
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void startEmail(){
        File file = createPDF();

        Intent i = new Intent(getContext(), ShareActivity.class);
        i.putExtra("pdf", file);
        startActivity(i);
    }

    public class getFeelingForDays extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FeelingsEntityManager fem = MainActivity.fem;
            ArrayList<DayFeeling> feelingsForDays = new ArrayList<>();
            int amountOfDays = 7;
            if(MainActivity.childrenmode){
                amountOfDays = 7;
            } else {
                amountOfDays = 14;
            }
            for(int i = 0; i < amountOfDays; i++){
                DayFeeling feelings;
                if(firstTime){
                    feelings = fem.getFeelingsForDay(LocalDate.now().minusDays(amountOfDays - 1 - i).toString(), true);
                } else {
                    feelings = fem.getFeelingsForDay(LocalDate.now().minusDays(amountOfDays - 1 - i).toString(), parental);
                }
                if(feelings.getFeelingsForDay().size() != 0){
                    feelingsForDays.add(feelings);
                }
            }
            dayFeelings = feelingsForDays;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            makeGraphView();
            pb.setVisibility(View.INVISIBLE);
        }
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
}