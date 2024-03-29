package nl.avans.praktijkhoogbegaafd.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.ViewTreeObserver;

import com.jjoe64.graphview.GraphView;
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

import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.dal.FeelingEntity;
import nl.avans.praktijkhoogbegaafd.domain.DayFeeling;
import nl.avans.praktijkhoogbegaafd.domain.PDFTypes;
import nl.avans.praktijkhoogbegaafd.logic.FeelingsEntityManager;
import nl.avans.praktijkhoogbegaafd.logic.ScreenshotLogic;

public class ScreenShotActivity extends AppCompatActivity {

    private List<FeelingEntity> currentFeeling = new ArrayList<>();
    private List<DayFeeling> dayFeelings = new ArrayList<>();

    private boolean isChild = true;
    private GraphView gv;

    private Double[] weekStats = new Double[5];
    private Double[] dayStats = new Double[5];

    private Double[] weekStatsChild = new Double[5];
    private Double[] dayStatsChild = new Double[5];

    private boolean isEmailIntentStarted = false;

    private File file;

    private int position;

    private double emoto = 0;
    private double fanti = 0;
    private double intellecto = 0;
    private double psymo = 0;
    private double senzo = 0;

    private String[] names = {"completeView", "Emoto", "Fanti", "Intellecto", "Psymo", "Senzo"};
    private String[] parentNames = {"completeViewParent", "EmotoParent", "FantiParent", "IntellectoParent", "PsymoParent", "SenzoParent"};

    private Bitmap ssEmoto = null;
    private Bitmap ssFanti = null;
    private Bitmap ssIntellecto = null;
    private Bitmap ssPsymo = null;
    private Bitmap ssSenzo = null;
    private Bitmap ssTotal = null;

    private Bitmap ssEmotoParent = null;
    private Bitmap ssFantiParent = null;
    private Bitmap ssIntellectoParent = null;
    private Bitmap ssPsymoParent = null;
    private Bitmap ssSenzoParent = null;
    private Bitmap ssTotalParent = null;

    private PDFTypes type;

    private int name = 0;

    private LocalDate selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot);

        gv = findViewById(R.id.gv_graph_ss);
        gv.getLegendRenderer().setVisible(false);

        selectedDate = LocalDate.parse(getIntent().getStringExtra("date"));
        type = (PDFTypes) getIntent().getSerializableExtra("type");

        isChild = type == PDFTypes.PARENTONLY || type == PDFTypes.ALL;

        makeGraph(0);

        gv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                isEmailIntentStarted = true;
                makeGraph(name);
            }
        });
    }

    public void makeGraph(int position) {
        this.position = position;
        if(position == 0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FeelingsEntityManager fem = MainActivity.fem;
                    ArrayList<DayFeeling> feelingsForDays = new ArrayList<>();
                    int amountOfDays = MainActivity.childrenmode ? 7 : 14;

                    for(int i = 0; i < amountOfDays; i++){
                        DayFeeling feelings;
                        feelings = fem.getFeelingsForDay(selectedDate.plusDays(amountOfDays).minusDays(amountOfDays - 1 - i).toString(), isChild);
                        feelingsForDays.add(feelings);
                    }
                    dayFeelings = feelingsForDays;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            makeGraphView();
                        }
                    });
                }
            }).start();
        } else {
            makeGraphView();
        }
    }

    public static double round(Double value, int places) {
        System.out.println("debug: rouding " + value);
        if (places < 0) throw new IllegalArgumentException();
        if (value == null) { return 0; }
        if (!Double.isNaN(value)) {
            BigDecimal bd = BigDecimal.valueOf(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            System.out.println("debug: rounded to " + bd);
            return bd.doubleValue();
        } else {
            return 0;
        }
    }

    public void makeGraphView() {

        if (MainActivity.childrenmode && isChild) {
            gv.getViewport().setMinX(1);
            gv.getViewport().setMinY(0);
            gv.getViewport().setMaxX(8);
            gv.getViewport().setMaxY(10);
        } else if (MainActivity.childrenmode) {
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
        if (isEmailIntentStarted) {
            gv.getLegendRenderer().setVisible(false);
        }

        gv.removeAllSeries();
        if (position == 0) {
            LineGraphSeries<DataPoint> emoto = createEmoto();
            if (MainActivity.childrenmode && isChild) {
                emoto.setTitle("Emoto");
            } else {
                emoto.setTitle("Emotionele intensiteit");
            }
            gv.addSeries(emoto);


            LineGraphSeries<DataPoint> fanti = createFanti();

            if (MainActivity.childrenmode && isChild) {
                fanti.setTitle("Fanti");
            } else {
                fanti.setTitle("Beeldende intensiteit");
            }
            gv.addSeries(fanti);

            LineGraphSeries<DataPoint> intellecto = createIntellecto();

            if (MainActivity.childrenmode && isChild) {
                intellecto.setTitle("Intellecto");
            } else {
                intellecto.setTitle("Intellectuele intensiteit");
            }
            gv.addSeries(intellecto);


            LineGraphSeries<DataPoint> psymo = createPsymo();

            if (MainActivity.childrenmode && isChild) {
                psymo.setTitle("Psymo");
            } else {
                psymo.setTitle("Pychomotorische intensiteit");
            }
            gv.addSeries(psymo);


            LineGraphSeries<DataPoint> senzo = createSenzo();
            if (MainActivity.childrenmode && isChild) {
                senzo.setTitle("Senzo");
            } else {
                senzo.setTitle("Sensorische intensiteit");
            }
            gv.addSeries(senzo);


        } else if (position == 1) {
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> emoto = createEmoto();
            if (MainActivity.childrenmode && isChild) {
                emoto.setTitle("Emoto");
            } else {
                emoto.setTitle("Emotionele intensiteit");
            }
            gv.addSeries(emoto);
        } else if (position == 2) {
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> fanti = createFanti();

            if (MainActivity.childrenmode && isChild) {
                fanti.setTitle("Fanti");
            } else {
                fanti.setTitle("Beeldende intensiteit");
            }
            gv.addSeries(fanti);
        } else if (position == 3) {
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> intellecto = createIntellecto();

            if (MainActivity.childrenmode && isChild) {
                intellecto.setTitle("Intellecto");
            } else {
                intellecto.setTitle("Intellectuele intensiteit");
            }
            gv.addSeries(intellecto);
        } else if (position == 4) {
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> psymo = createPsymo();

            if (MainActivity.childrenmode && isChild) {
                psymo.setTitle("Psymo");
            } else {
                psymo.setTitle("Pychomotorische intensiteit");
            }
            gv.addSeries(psymo);
        } else if (position == 5) {
            gv.removeAllSeries();
            LineGraphSeries<DataPoint> senzo = createSenzo();
            if (MainActivity.childrenmode && isChild) {
                senzo.setTitle("Senzo");
            } else {
                senzo.setTitle("Sensorische intensiteit");
            }
            gv.addSeries(senzo);
        }

        if(isEmailIntentStarted){
            makeScreenshots();
            if(name < 5){
                name++;
                makeGraph(name);
            } else if (name == 5 && isChild){
                name = 0;
                isChild = false;
                makeGraph(name);
            } else{
                startEmail();
            }
        }
    }

    public void makeScreenshots() {
        Bitmap b = ScreenshotLogic.takeScreenshot(gv);
        if (!isChild) {
            storeScreenshot(b, names[name]);
        } else {
            storeScreenshot(b, parentNames[name]);
        }
    }

    public LineGraphSeries<DataPoint> createSenzo() {
        LineGraphSeries<DataPoint> senzo = new LineGraphSeries<>();
        double x = 1;
        int stats = 0;
        int amountOfValues = 0;
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

        double finalStats = 1.0 * stats / amountOfValues;
        double dayStat = 1.0 * lastDayValue / amountOfValuesLastDay;

        if (!Double.isNaN(finalStats)) {
            System.out.println("debug: here with stats " + finalStats + " " + isChild + " " + MainActivity.childrenmode);
            if(isChild && MainActivity.childrenmode){
                this.weekStatsChild[4] = finalStats;
            } else {
                this.weekStats[4] = finalStats;
            }
        }

        if (!Double.isNaN(dayStat)) {
            if(isChild && MainActivity.childrenmode){
                this.dayStatsChild[4] = dayStat;
            } else {
                this.dayStats[4] = dayStat;
            }
        }

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

        double finalStats = 1.0 * stats / amountOfValues;
        double dayStat = 1.0 * lastDayValue / amountOfValuesLastDay;

        if (!Double.isNaN(finalStats)) {
            if(isChild && MainActivity.childrenmode){
                this.weekStatsChild[3] = finalStats;
            } else {
                this.weekStats[3] = finalStats;
            }
        }

        if (!Double.isNaN(dayStat)) {
            if(isChild && MainActivity.childrenmode){
                this.dayStatsChild[3] = dayStat;
            } else {
                this.dayStats[3] = dayStat;
            }
        }

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

        double finalStats = 1.0 * stats / amountOfValues;
        double dayStat = 1.0 * lastDayValue / amountOfValuesLastDay;

        if (!Double.isNaN(finalStats)) {
            if(isChild && MainActivity.childrenmode){
                this.weekStatsChild[2] = finalStats;
            } else {
                this.weekStats[2] = finalStats;
            }
        }

        if (!Double.isNaN(dayStat)) {
            if(isChild && MainActivity.childrenmode){
                this.dayStatsChild[2] = dayStat;
            } else {
                this.dayStats[2] = dayStat;
            }
        }

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

        double finalStats = 1.0 * stats / amountOfValues;
        double dayStat = 1.0 * lastDayValue / amountOfValuesLastDay;

        if (!Double.isNaN(finalStats)) {
            if(isChild && MainActivity.childrenmode){
                this.weekStatsChild[1] = finalStats;
            } else {
                this.weekStats[1] = finalStats;
            }
        }

        if (!Double.isNaN(dayStat)) {
            if(isChild && MainActivity.childrenmode){
                this.dayStatsChild[1] = dayStat;
            } else {
                this.dayStats[1] = dayStat;
            }
        }

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

        double finalStats = 1.0 * stats / amountOfValues;
        double dayStat = 1.0 * lastDayValue / amountOfValuesLastDay;

        if (!Double.isNaN(finalStats)) {
            if(isChild && MainActivity.childrenmode){
                this.weekStatsChild[0] = finalStats;
            } else {
                this.weekStats[0] = finalStats;
            }
        }

        if (!Double.isNaN(dayStat)) {
            if(isChild && MainActivity.childrenmode){
                this.dayStatsChild[0] = dayStat;
            } else {
                this.dayStats[0] = dayStat;
            }
        }

        this.emoto = finalStats;
        emoto.setColor(Color.rgb(232, 85, 51));
        emoto.setDrawDataPoints(true);
        emoto.setDataPointsRadius(6);
        return emoto;
    }

    public void storeScreenshot(Bitmap bitmap, String filename) {
        switch (filename) {
            case "Emoto":
                this.ssEmoto = bitmap;
                break;
            case "Fanti":
                this.ssFanti = bitmap;
                break;
            case "Intellecto":
                this.ssIntellecto = bitmap;
                break;
            case "Psymo":
                this.ssPsymo = bitmap;
                break;
            case "Senzo":
                this.ssSenzo = bitmap;
                break;
            case "completeView":
                this.ssTotal = bitmap;
                break;
            case "EmotoParent":
                this.ssEmotoParent = bitmap;
                break;
            case "FantiParent":
                this.ssFantiParent = bitmap;
                break;
            case "IntellectoParent":
                this.ssIntellectoParent = bitmap;
                break;
            case "PsymoParent":
                this.ssPsymoParent = bitmap;
                break;
            case "SenzoParent":
                this.ssSenzoParent = bitmap;
                break;
            case "completeViewParent":
                this.ssTotalParent = bitmap;
                break;
        }
    }

    public File createPDF(PDFTypes type) {
        PdfDocument doc = new PdfDocument();
        int width = (gv.getWidth()) * 3;
        int height = type == PDFTypes.ALL ? (gv.getHeight()) * 4 : (gv.getHeight()) * 2;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height + 1000, 1).create();

        PdfDocument.Page page = doc.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);

        Bitmap legend = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_phr_legenda_new_foreground);
        Bitmap logo = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_phr_logo_large_foreground);
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, 400, 400, false);

        if (type == PDFTypes.PARENTONLY) {
            canvas = drawParent(canvas, logo, paint);
            canvas = drawStats(canvas, height + 300, paint);
        } else if (type == PDFTypes.CHILDONLY) {
            canvas = drawChildren(canvas, logo, paint);
            canvas = drawStats(canvas, height + 300, paint);
        } else {
            canvas = drawAll(canvas, logo, paint);
            canvas = drawStats(canvas, height + 300, paint);
            canvas = drawStatsForChild(canvas, height + 300, paint);
        }

        canvas.drawBitmap(scaledLogo, (width - scaledLogo.getWidth()) / 2, -100, paint);
        canvas.drawBitmap(legend, width - legend.getWidth(), 0, paint);

        doc.finishPage(page);

        ContextWrapper cw = new ContextWrapper(this);
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

    public Canvas drawParent(Canvas canvas, Bitmap logo, Paint paint) {
        canvas.drawBitmap(ssTotalParent, 0, logo.getHeight(), paint);
        canvas.drawBitmap(ssEmotoParent, ssTotalParent.getWidth(), logo.getHeight(), paint);
        canvas.drawBitmap(ssFantiParent, ssEmotoParent.getWidth() * 2, logo.getHeight(), paint);
        canvas.drawBitmap(ssIntellectoParent, 0, ssTotalParent.getHeight() + logo.getHeight(), paint);
        canvas.drawBitmap(ssPsymoParent, ssIntellectoParent.getWidth(), ssTotalParent.getHeight() + logo.getHeight(), paint);
        canvas.drawBitmap(ssSenzoParent, ssIntellectoParent.getWidth() * 2, ssTotalParent.getHeight() + logo.getHeight(), paint);
        return canvas;
    }

    public Canvas drawChildren(Canvas canvas, Bitmap logo, Paint paint) {
        canvas.drawBitmap(ssTotal, 0, logo.getHeight(), paint);
        canvas.drawBitmap(ssEmoto, ssTotal.getWidth(), logo.getHeight(), paint);
        canvas.drawBitmap(ssFanti, ssEmoto.getWidth() * 2, logo.getHeight(), paint);
        canvas.drawBitmap(ssIntellecto, 0, ssTotal.getHeight() + logo.getHeight(), paint);
        canvas.drawBitmap(ssPsymo, ssIntellecto.getWidth(), ssTotal.getHeight() + logo.getHeight(), paint);
        canvas.drawBitmap(ssSenzo, ssIntellecto.getWidth() * 2, ssTotal.getHeight() + logo.getHeight(), paint);
        return canvas;
    }

    public Canvas drawAll(Canvas canvas, Bitmap logo, Paint paint) {
        Paint bold = new Paint();
        bold.setColor(getResources().getColor(R.color.prhPurpleDark));
        bold.setTextSize(40);
        bold.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText("Ouder:", 20, logo.getHeight(), bold);

        canvas.drawBitmap(ssTotal, 0, logo.getHeight() + 20, paint);
        canvas.drawBitmap(ssEmoto, ssTotal.getWidth(), logo.getHeight() + 20, paint);
        canvas.drawBitmap(ssFanti, ssEmoto.getWidth() * 2, logo.getHeight() + 20, paint);
        canvas.drawBitmap(ssIntellecto, 0, ssTotal.getHeight() + logo.getHeight() + 20, paint);
        canvas.drawBitmap(ssPsymo, ssIntellecto.getWidth(), ssTotal.getHeight() + logo.getHeight() + 20, paint);
        canvas.drawBitmap(ssSenzo, ssIntellecto.getWidth() * 2, ssTotal.getHeight() + logo.getHeight() + 20, paint);

        canvas.drawText("Kind:", 20, logo.getHeight() + ssTotalParent.getHeight() * 2 + 40, bold);

        canvas.drawBitmap(ssTotalParent, 0, logo.getHeight() + ssTotalParent.getHeight() * 2 + 90, paint);
        canvas.drawBitmap(ssEmotoParent, ssTotalParent.getWidth(), logo.getHeight() + ssTotalParent.getHeight() * 2 + 90, paint);
        canvas.drawBitmap(ssFantiParent, ssEmotoParent.getWidth() * 2, logo.getHeight() + ssTotalParent.getHeight() * 2 + 90, paint);
        canvas.drawBitmap(ssIntellectoParent, 0, ssTotalParent.getHeight() * 3 + 90 + logo.getHeight(), paint);
        canvas.drawBitmap(ssPsymoParent, ssIntellectoParent.getWidth(), ssTotalParent.getHeight() * 3 + logo.getHeight() + 90, paint);
        canvas.drawBitmap(ssSenzoParent, ssIntellectoParent.getWidth() * 2, ssTotalParent.getHeight() * 3 + logo.getHeight() + 90, paint);
        return canvas;
    }

    public Canvas drawStats(Canvas canvas, int height, Paint paint) {
        int statsDescriptionX = 20;
        int statsValueX = 600;

        Paint bold = new Paint();
        bold.setColor(getResources().getColor(R.color.prhPurpleDark));
        bold.setTextSize(20);
        bold.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Paint purple = new Paint();
        purple.setColor(getResources().getColor(R.color.phrOrange));
        purple.setTextSize(20);
        purple.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        String statsText = MainActivity.childrenmode ? "ouder" : "volwassene";

        canvas.drawText("Weekstatistieken " + statsText + ":", statsDescriptionX, height + 150, bold);
        canvas.drawText("Gemiddelde emotionele intensiteit afgelopen week:", statsDescriptionX, height + 180, paint);
        canvas.drawText("Gemiddelde beeldende intensiteit afgelopen week:", statsDescriptionX, height + 210, paint);
        canvas.drawText("Gemiddelde intellectuele- intensiteit afgelopen week:", statsDescriptionX, height + 240, paint);
        canvas.drawText("Gemiddelde psychomotorische intensiteit afgelopen week:", statsDescriptionX, height + 270, paint);
        canvas.drawText("Gemiddelde sensorische intensiteit afgelopen week:", statsDescriptionX, height + 300, paint);

        canvas.drawText(round(weekStats[0], 2) + "", statsValueX, height + 180, purple);
        canvas.drawText(round(weekStats[1], 2) + "", statsValueX, height + 210, purple);
        canvas.drawText(round(weekStats[2], 2) + "", statsValueX, height + 240, purple);
        canvas.drawText(round(weekStats[3], 2) + "", statsValueX, height + 270, purple);
        canvas.drawText(round(weekStats[4], 2) + "", statsValueX, height + 300, purple);


        canvas.drawText("Dagstatistieken " + statsText + " van " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ":", statsDescriptionX, height + 340, bold);
        canvas.drawText("Gemiddelde emotionele intensiteit afgelopen dag:", statsDescriptionX, height + 370, paint);
        canvas.drawText("Gemiddelde beeldende intensiteit afgelopen dag:", statsDescriptionX, height + 400, paint);
        canvas.drawText("Gemiddelde intellectuele- intensiteit afgelopen dag:", statsDescriptionX, height + 430, paint);
        canvas.drawText("Gemiddelde psychomotorische intensiteit afgelopen dag:", statsDescriptionX, height + 460, paint);
        canvas.drawText("Gemiddelde sensorische intensiteit afgelopen dag:", statsDescriptionX, height + 490, paint);

        canvas.drawText(round(dayStats[0], 2) + "", statsValueX, height + 370, purple);
        canvas.drawText(round(dayStats[1], 2) + "", statsValueX, height + 400, purple);
        canvas.drawText(round(dayStats[2], 2) + "", statsValueX, height + 430, purple);
        canvas.drawText(round(dayStats[3], 2) + "", statsValueX, height + 460, purple);
        canvas.drawText(round(dayStats[4], 2) + "", statsValueX, height + 490, purple);

        return canvas;
    }

    public Canvas drawStatsForChild(Canvas canvas, int height, Paint paint) {
        int statsDescriptionX = 800;
        int statsValueX = 1380;

        Paint bold = new Paint();
        bold.setColor(getResources().getColor(R.color.prhPurpleDark));
        bold.setTextSize(20);
        bold.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Paint purple = new Paint();
        purple.setColor(getResources().getColor(R.color.phrOrange));
        purple.setTextSize(20);
        purple.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText("Weekstatistieken kind:", statsDescriptionX, height + 150, bold);
        canvas.drawText("Gemiddelde emotionele intensiteit afgelopen week:", statsDescriptionX, height + 180, paint);
        canvas.drawText("Gemiddelde beeldende intensiteit afgelopen week:", statsDescriptionX, height + 210, paint);
        canvas.drawText("Gemiddelde intellectuele- intensiteit afgelopen week:", statsDescriptionX, height + 240, paint);
        canvas.drawText("Gemiddelde psychomotorische intensiteit afgelopen week:", statsDescriptionX, height + 270, paint);
        canvas.drawText("Gemiddelde sensorische intensiteit afgelopen week:", statsDescriptionX, height + 300, paint);

        canvas.drawText(round(weekStatsChild[0], 2) + "", statsValueX, height + 180, purple);
        canvas.drawText(round(weekStatsChild[1], 2) + "", statsValueX, height + 210, purple);
        canvas.drawText(round(weekStatsChild[2], 2) + "", statsValueX, height + 240, purple);
        canvas.drawText(round(weekStatsChild[3], 2) + "", statsValueX, height + 270, purple);
        canvas.drawText(round(weekStatsChild[4], 2) + "", statsValueX, height + 300, purple);


        canvas.drawText("Dagstatistieken kind van " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ":", statsDescriptionX, height + 340, bold);
        canvas.drawText("Gemiddelde emotionele intensiteit afgelopen dag:", statsDescriptionX, height + 370, paint);
        canvas.drawText("Gemiddelde beeldende intensiteit afgelopen dag:", statsDescriptionX, height + 400, paint);
        canvas.drawText("Gemiddelde intellectuele- intensiteit afgelopen dag:", statsDescriptionX, height + 430, paint);
        canvas.drawText("Gemiddelde psychomotorische intensiteit afgelopen dag:", statsDescriptionX, height + 460, paint);
        canvas.drawText("Gemiddelde sensorische intensiteit afgelopen dag:", statsDescriptionX, height + 490, paint);

        canvas.drawText(round(dayStatsChild[0], 2) + "", statsValueX, height + 370, purple);
        canvas.drawText(round(dayStatsChild[1], 2) + "", statsValueX, height + 400, purple);
        canvas.drawText(round(dayStatsChild[2], 2) + "", statsValueX, height + 430, purple);
        canvas.drawText(round(dayStatsChild[3], 2) + "", statsValueX, height + 460, purple);
        canvas.drawText(round(dayStatsChild[4], 2) + "", statsValueX, height + 490, purple);

        return canvas;
    }

    public void startEmail() {
        File file = createPDF(type);
        Intent i = new Intent(this, ShareActivity.class);
        i.putExtra("pdf", file);
        startActivity(i);
    }
}