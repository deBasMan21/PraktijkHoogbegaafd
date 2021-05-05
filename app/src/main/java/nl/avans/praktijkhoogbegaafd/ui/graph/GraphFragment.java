package nl.avans.praktijkhoogbegaafd.ui.graph;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import nl.avans.praktijkhoogbegaafd.MainActivity;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        graphViewModel =
                new ViewModelProvider(this).get(GraphViewModel.class);
        root = inflater.inflate(R.layout.fragment_graph, container, false);

        gv = root.findViewById(R.id.gv_graph);

        pb = root.findViewById(R.id.pb_graph_loading);

        adapterAdult = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, categoriesAdult);
        adapterChildren = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, categoriesChildren);

        makeGraph(0);
        System.out.println("hier maakt hij een nieuwe grafiek");

        ToggleButton tb = root.findViewById(R.id.tb_graph_switch);
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

    public void makeGraphView(){

        if(MainActivity.childrenmode && parental){
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



        gv.getLegendRenderer().setTextSize(25);
        gv.getLegendRenderer().setBackgroundColor(Color.argb(150, 50, 0, 0));
        gv.getLegendRenderer().setTextColor(Color.WHITE);
        gv.getLegendRenderer().setWidth(200);
        gv.getLegendRenderer().setFixedPosition(0, 0);
        if(isEmailIntentStarted){
            gv.getLegendRenderer().setVisible(false);
        }




        TextView tv_score_emoto = root.findViewById(R.id.tv_graph_score_emoto);
        TextView tv_score_fanti = root.findViewById(R.id.tv_graph_score_fanti);
        TextView tv_score_intellecto = root.findViewById(R.id.tv_graph_score_intellecto);
        TextView tv_score_psymo = root.findViewById(R.id.tv_graph_score_psymo);
        TextView tv_score_senzo = root.findViewById(R.id.tv_graph_score_senzo);
        tv_score_emoto.setText("");
        tv_score_fanti.setText("");
        tv_score_intellecto.setText("");
        tv_score_psymo.setText("");
        tv_score_senzo.setText("");



        System.out.println(position);
        gv.removeAllSeries();
        if(position == 0){
            LineGraphSeries<DataPoint> emoto = createEmoto();
            if(MainActivity.childrenmode && parental){
                emoto.setTitle("Emoto");
                tv_score_emoto.setText("Emoto: " + Math.round(this.emoto));
            } else{
                emoto.setTitle("Emotionele intensiteit");
                tv_score_emoto.setText("Emotionele intensiteit: " + this.emoto);
                tv_score_emoto.setVisibility(View.VISIBLE);
            }
            gv.addSeries(emoto);



            LineGraphSeries<DataPoint> fanti = createFanti();

            if(MainActivity.childrenmode && parental){
                fanti.setTitle("Fanti");
                tv_score_fanti.setText("Fanti: " + Math.round(this.fanti));
            } else{
                fanti.setTitle("Beeldende intensiteit");
                tv_score_fanti.setText("Beeldende intensiteit: " + this.fanti);
                tv_score_fanti.setVisibility(View.VISIBLE);
            }
            gv.addSeries(fanti);




            LineGraphSeries<DataPoint> intellecto = createIntellecto();

            if(MainActivity.childrenmode && parental){
                intellecto.setTitle("Intellecto");
                tv_score_intellecto.setText("Intellecto: " + Math.round(this.intellecto));
            } else{
                intellecto.setTitle("Intellectuele intensiteit");
                tv_score_intellecto.setText("Intellectuele intensiteit: " + this.intellecto);
                tv_score_intellecto.setVisibility(View.VISIBLE);
            }
            gv.addSeries(intellecto);


            LineGraphSeries<DataPoint> psymo = createPsymo();

            if(MainActivity.childrenmode && parental){
                psymo.setTitle("Psymo");
                tv_score_psymo.setText("Psymo: " + Math.round(this.psymo));
            } else{
                psymo.setTitle("Pychomotorische intensiteit");
                tv_score_psymo.setText("Psychomotorische intensiteit: " + this.psymo);
                tv_score_psymo.setVisibility(View.VISIBLE);
            }
            gv.addSeries(psymo);


            LineGraphSeries<DataPoint> senzo = createSenzo();
            if(MainActivity.childrenmode && parental){
                senzo.setTitle("Senzo");
                tv_score_senzo.setText("Senzo: " + Math.round(this.senzo));
            } else{
                senzo.setTitle("Sensorische intensiteit");
                tv_score_senzo.setText("Sensorische intensiteit: " + this.senzo);
                tv_score_senzo.setVisibility(View.VISIBLE);
            }
            gv.addSeries(senzo);


        } else if(position == 1){
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> emoto = createEmoto();
            if(MainActivity.childrenmode && parental){
                emoto.setTitle("Emoto");
                tv_score_emoto.setText("Emoto: " + Math.round(this.emoto));
            } else{
                emoto.setTitle("Emotionele intensiteit");
                tv_score_emoto.setText("Emotionele intensiteit: " + this.emoto);
                tv_score_emoto.setVisibility(View.VISIBLE);
            }
            gv.addSeries(emoto);
        } else if(position == 2){
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> fanti = createFanti();

            if(MainActivity.childrenmode && parental){
                fanti.setTitle("Fanti");
                tv_score_emoto.setText("Fanti: " + Math.round(this.fanti));
            } else{
                fanti.setTitle("Beeldende intensiteit");
                tv_score_emoto.setText("Beeldende intensiteit: " + this.fanti);
                tv_score_fanti.setVisibility(View.VISIBLE);
            }
            gv.addSeries(fanti);
        } else if (position == 3){
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> intellecto = createIntellecto();

            if(MainActivity.childrenmode && parental){
                intellecto.setTitle("Intellecto");
                tv_score_emoto.setText("Intellecto: " + Math.round(this.intellecto));
            } else{
                intellecto.setTitle("Intellectuele intensiteit");
                tv_score_emoto.setText("Intellectuele intensiteit: " + this.intellecto);
                tv_score_intellecto.setVisibility(View.VISIBLE);
            }
            gv.addSeries(intellecto);
        } else if (position == 4){
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> psymo = createPsymo();

            if(MainActivity.childrenmode && parental){
                psymo.setTitle("Psymo");
                tv_score_emoto.setText("Psymo: " + Math.round(this.psymo));
            } else{
                psymo.setTitle("Pychomotorische intensiteit");
                tv_score_emoto.setText("Psychomotorische intensiteit: " + this.psymo);
                tv_score_psymo.setVisibility(View.VISIBLE);
            }
            gv.addSeries(psymo);
        } else if(position == 5){
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> senzo = createSenzo();
            if(MainActivity.childrenmode && parental){
                senzo.setTitle("Senzo");
                tv_score_emoto.setText("Senzo: " + Math.round(this.senzo));
            } else{
                senzo.setTitle("Sensorische intensiteit");
                tv_score_emoto.setText("Sensorische intensiteit: " + Math.round(this.senzo));
                tv_score_senzo.setVisibility(View.VISIBLE);
            }
            gv.addSeries(senzo);
        }

    }

    public LineGraphSeries<DataPoint> createSenzo(){
        LineGraphSeries<DataPoint> senzo = new LineGraphSeries<>();
        double x = 1;
        int stats = 0;
        int amountOfValues = 0;
        double finalStats = 0;

        for (DayFeeling feelings : dayFeelings) {
            ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
            double subx = x;
            for (FeelingEntity feeling : entities) {
                senzo.appendData(new DataPoint(subx, feeling.getSenzo()), true, 10);
                stats += feeling.getSenzo();
                subx += 1.0 / entities.size();
                amountOfValues++;
            }
            x++;
        }
        if(MainActivity.childrenmode && parental){
            finalStats = 1.0 * stats / amountOfValues;
        } else {
            finalStats = 1.0 * stats;
        }
        this.senzo = finalStats;
        senzo.setColor(Color.rgb(242, 150, 49));
        senzo.setDrawDataPoints(true);
        senzo.setDataPointsRadius(6);
        return senzo;
    }

    public LineGraphSeries<DataPoint> createPsymo(){
        LineGraphSeries<DataPoint> psymo = new LineGraphSeries<>();
        double x = 1;
        int stats = 0;
        int amountOfValues = 0;
        double finalStats = 0;

        for (DayFeeling feelings : dayFeelings) {
            ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
            double subx = x;
            for (FeelingEntity feeling : entities) {
                psymo.appendData(new DataPoint(subx, feeling.getPsymo()), true, 10);
                stats += feeling.getPsymo();
                subx += 1.0 / entities.size();
                amountOfValues++;
            }
            x++;
        }
        if(MainActivity.childrenmode && parental){
            finalStats = 1.0 * stats / amountOfValues;
        } else {
            finalStats = stats;
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

        for (DayFeeling feelings : dayFeelings) {
            ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
            double subx = x;
            for (FeelingEntity feeling : entities) {
                intellecto.appendData(new DataPoint(subx, feeling.getIntellecto()), true, 10);
                stats += feeling.getIntellecto();
                subx += 1.0 / entities.size();
                amountOfValues++;
            }
            x++;
        }
        if(MainActivity.childrenmode && parental){
            finalStats = 1.0 * stats / amountOfValues;
        } else {
            finalStats = 1.0 * stats;
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

        for (DayFeeling feelings : dayFeelings) {
            ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
            double subx = x;
            for (FeelingEntity feeling : entities) {
                fanti.appendData(new DataPoint(subx, feeling.getFanti()), true, 10);
                stats += feeling.getFanti();
                subx += 1.0 / entities.size();
                amountOfValues++;
            }
            x++;
        }
        if(MainActivity.childrenmode && parental){
            finalStats = 1.0 * stats / amountOfValues;
        } else {
            finalStats = 1.0 * stats;
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

        for (DayFeeling feelings : dayFeelings) {
            ArrayList<FeelingEntity> entities = feelings.getFeelingsForDay();
            double subx = x;
            for (FeelingEntity feeling : entities) {
                emoto.appendData(new DataPoint(subx, feeling.getEmoto()), true, 10);
                stats += feeling.getEmoto();
                subx += 1.0 / entities.size();
                amountOfValues++;
            }
            x++;

        }
        if(MainActivity.childrenmode && parental){
             finalStats = 1.0 * stats / amountOfValues;
        } else {
            finalStats = 1.0 * stats;
        }
        this.emoto = finalStats;
        emoto.setColor(Color.rgb(232, 85, 51));
        emoto.setDrawDataPoints(true);
        emoto.setDataPointsRadius(6);
        return emoto;
    }

    public void storeScreenshot(Bitmap bitmap, String filename) {
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, filename + ".jpg");
        file.setReadable(true);
        this.file = file;
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getScreenshot(String filename){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
        int heightRatio = (int)Math.ceil(options.outHeight/(float) 2000);
        int widthRatio = (int)Math.ceil(options.outWidth/(float) 1000);

        if (heightRatio > 1 || widthRatio > 1)
        {
            if (heightRatio > widthRatio)
            {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }

        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file.getPath(), options);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        //this gives the size of the compressed image in kb
        long lengthbmp = imageInByte.length / 1024;

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file.getPath()));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    public File createPDF(){
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1000, 1200, 1).create();

        PdfDocument.Page page = doc.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(25);
        canvas.drawBitmap(getScreenshot("screenshot"), 0, 0, paint);
        canvas.drawBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_phr_legenda_foreground), 55, 70, paint);
        if(MainActivity.childrenmode){
            canvas.drawText(MainActivity.name + " " + MainActivity.birthDay + " (Ouder: " + MainActivity.parentalName + ")", 0, 950, paint);
        } else{
            canvas.drawText(MainActivity.name + " " + MainActivity.birthDay , 0, 950, paint);
        }
        canvas.drawText("Datum vanaf: " + LocalDate.now().minusDays(6) ,0, 975, paint);
        canvas.drawText("Datum tot en met: " + LocalDate.now(), 0, 1000, paint);
        canvas.drawText(MainActivity.begeleidster, 0, 1025, paint);

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
        Bitmap b = ScreenshotLogic.takescreenshotOfRootView(root);
        storeScreenshot(b, "screenshot");

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
            for(int i = 0; i < 7; i++){
                DayFeeling feelings;
                if(firstTime){
                    feelings = fem.getFeelingsForDay(LocalDate.now().minusDays(6 - i).toString(), !false);
                } else {
                    feelings = fem.getFeelingsForDay(LocalDate.now().minusDays(6 - i).toString(), parental);
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
            if(isEmailIntentStarted){
                startEmail();
            }
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