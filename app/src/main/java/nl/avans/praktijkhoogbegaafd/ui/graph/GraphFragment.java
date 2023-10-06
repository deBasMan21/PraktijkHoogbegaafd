package nl.avans.praktijkhoogbegaafd.ui.graph;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nl.avans.praktijkhoogbegaafd.domain.PDFTypes;
import nl.avans.praktijkhoogbegaafd.ui.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.dal.FeelingEntity;
import nl.avans.praktijkhoogbegaafd.domain.DayFeeling;
import nl.avans.praktijkhoogbegaafd.logic.FeelingsEntityManager;
import nl.avans.praktijkhoogbegaafd.ui.ScreenShotActivity;

@SuppressLint("SetTextI18n")
public class GraphFragment extends Fragment {

    private GraphViewModel graphViewModel;

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

    private View root;
    private int position;

    private ProgressBar pb;

    private double emoto = 0;
    private double fanti = 0;
    private double intellecto = 0;
    private double psymo = 0;
    private double senzo = 0;

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

    private EditText et_date;
    private LocalDate selectedDate;

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

        gv = root.findViewById(R.id.gv_graph);

        pb = root.findViewById(R.id.pb_graph_loading);

        et_date = root.findViewById(R.id.et_graph_date);
        et_date.setFocusable(false);

        selectedDate = LocalDate.now().minusDays(MainActivity.childrenmode ? 7 : 14);
        int year = selectedDate.getYear();
        int month = selectedDate.getMonthValue();
        int dayOfMonth = selectedDate.getDayOfMonth();

        String date = String.format("%s-%s-%s%n", dayOfMonth, month, year);
        et_date.setText("Datum vanaf: " + date);

        adapterAdult = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, categoriesAdult);
        adapterChildren = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, categoriesChildren);

        makeGraph(0);

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
                } else {
                    parental = true;
                    spinner.setAdapter(adapterChildren);
                }
                makeGraph(spinner.getSelectedItemPosition());
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
                gv.getLegendRenderer().setVisible(isChecked);
                makeGraph(position);
            }
        });

        Button share = root.findViewById(R.id.bn_graph_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.childrenmode){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.shareMessage).setNeutralButton("Allemaal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getContext().startActivity(new Intent(getContext(), ScreenShotActivity.class).putExtra("type", PDFTypes.ALL).putExtra("date", selectedDate.toString()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                    }).setPositiveButton("Alleen kind", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getContext().startActivity(new Intent(getContext(), ScreenShotActivity.class).putExtra("type", PDFTypes.PARENTONLY).putExtra("date", selectedDate.toString()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                    }).setNegativeButton("Alleen ouder", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getContext().startActivity(new Intent(getContext(), ScreenShotActivity.class).putExtra("type", PDFTypes.CHILDONLY).putExtra("date", selectedDate.toString()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                    }).setTitle("Maak uw keuze en deel!").show();
                } else {
                    getContext().startActivity(new Intent(getContext(), ScreenShotActivity.class).putExtra("type", PDFTypes.PARENTONLY).putExtra("date", selectedDate.toString()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        });



        DatePickerDialog dialog = new DatePickerDialog(root.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = String.format("%s-%s-%s%n", dayOfMonth, month + 1, year);
                et_date.setText("Datum vanaf: " + date);
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                makeGraph(position);
            }
        }, year, month, dayOfMonth);

        LocalDate disabledDates = LocalDate.now().minusDays(MainActivity.childrenmode ? 7 : 14);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(disabledDates.getYear(), disabledDates.getMonthValue() - 1, disabledDates.getDayOfMonth());
        dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        return root;
    }

    public void makeGraph(int position){
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
            gv.getViewport().setMaxX(8);
            gv.getViewport().setMaxY(10);
        } else if (!parental) {
            gv.getViewport().setMinX(1);
            gv.getViewport().setMinY(-2);
            gv.getViewport().setMaxX(8);
            gv.getViewport().setMaxY(2);
        } else {
            gv.getViewport().setMinX(1);
            gv.getViewport().setMinY(-2);
            gv.getViewport().setMaxX(15);
            gv.getViewport().setMaxY(2);
        }

        gv.getViewport().setYAxisBoundsManual(true);
        gv.getViewport().setXAxisBoundsManual(true);

        gv.getLegendRenderer().setTextSize(25);
        gv.getLegendRenderer().setBackgroundColor(Color.argb(150, 50, 0, 0));
        gv.getLegendRenderer().setTextColor(Color.WHITE);
        gv.getLegendRenderer().setWidth(200);
        gv.getLegendRenderer().setFixedPosition(0, 0);
        if(isEmailIntentStarted){
            gv.getLegendRenderer().setVisible(false);
        }

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
    }

    public LineGraphSeries<DataPoint> createSenzo() {
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
        finalStats = 1.0 * stats / amountOfValues;
        this.weekStats[4] = finalStats;
        this.dayStats[4] = 1.0 * lastDayValue / amountOfValuesLastDay;

        this.senzo = finalStats;
        senzo.setColor(Color.rgb(242, 150, 49));
        senzo.setDrawDataPoints(true);
        senzo.setDataPointsRadius(6);
        return senzo;
    }

    public LineGraphSeries<DataPoint> createPsymo() {
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
        finalStats = 1.0 * stats / amountOfValues;
        this.weekStats[3] = finalStats;
        this.dayStats[3] = 1.0 * lastDayValue / amountOfValuesLastDay;

        this.psymo = finalStats;
        psymo.setColor(Color.rgb(81, 102, 169));
        psymo.setDrawDataPoints(true);
        psymo.setDataPointsRadius(6);
        return psymo;
    }

    public LineGraphSeries<DataPoint> createIntellecto() {
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
        finalStats = 1.0 * stats / amountOfValues;
        this.weekStats[2] = finalStats;
        this.dayStats[2] = 1.0 * lastDayValue / amountOfValuesLastDay;

        this.intellecto = finalStats;
        intellecto.setColor(Color.rgb(182, 103, 161));
        intellecto.setDrawDataPoints(true);
        intellecto.setDataPointsRadius(6);
        return intellecto;
    }

    public LineGraphSeries<DataPoint> createFanti() {
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
        finalStats = 1.0 * stats / amountOfValues;
        this.weekStats[1] = finalStats;
        this.dayStats[1] = 1.0 * lastDayValue / amountOfValuesLastDay;

        this.fanti = finalStats;
        fanti.setColor(Color.rgb(98, 176, 74));
        fanti.setDrawDataPoints(true);
        fanti.setDataPointsRadius(6);
        return fanti;
    }

    public LineGraphSeries<DataPoint> createEmoto() {
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
                lastDayValue += feeling.getEmoto();
                amountOfValuesLastDay++;
            }
            x++;

        }
        finalStats = 1.0 * stats / amountOfValues;
        this.weekStats[0] = finalStats;
        this.dayStats[0] = 1.0 * lastDayValue / amountOfValuesLastDay;


        this.emoto = finalStats;
        emoto.setColor(Color.rgb(232, 85, 51));
        emoto.setDrawDataPoints(true);
        emoto.setDataPointsRadius(6);
        return emoto;
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
            int amountOfDays = MainActivity.childrenmode ? 7 : 14;

            for(int i = 0; i < amountOfDays; i++){
                DayFeeling feelings;
                feelings = fem.getFeelingsForDay(selectedDate.plusDays(amountOfDays).minusDays(amountOfDays - 1 - i).toString(), firstTime || parental);
                feelingsForDays.add(feelings);
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
}