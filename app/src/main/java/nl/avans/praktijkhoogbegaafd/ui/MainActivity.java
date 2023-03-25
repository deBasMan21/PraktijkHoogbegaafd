package nl.avans.praktijkhoogbegaafd.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.dal.InfoEntity;
import nl.avans.praktijkhoogbegaafd.domain.BegeleidstersHolder;
import nl.avans.praktijkhoogbegaafd.logic.FeelingsEntityManager;
import nl.avans.praktijkhoogbegaafd.logic.InfoEntityManager;
import nl.avans.praktijkhoogbegaafd.logic.NotificationUtils;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    public static FeelingsEntityManager fem;

    public static boolean childrenmode = false;
    public static boolean withPhr = false;
    public static String name = "";
    public static String begeleidster = "";

    public static int id = 1;

    public static File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        String path = Environment.getExternalStorageDirectory().toString();
        file = new File(path);

        fem = new FeelingsEntityManager(getApplication());

        id = fem.getHighestId();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        SharedPreferences prefs = getSharedPreferences("info", Context.MODE_PRIVATE);

        if(!prefs.getString(getResources().getString(R.string.PREFS_NAME), "empty").equals("empty")){
            MainActivity.name = prefs.getString(getResources().getString(R.string.PREFS_NAME), "");
            MainActivity.begeleidster = prefs.getString(getResources().getString(R.string.PREFS_BEGELEIDSTER), "");
            MainActivity.childrenmode = prefs.getBoolean(getResources().getString(R.string.PREFS_CHILDRENMODE), false);
            MainActivity.withPhr = prefs.getBoolean(getResources().getString(R.string.PREFS_WITHPHR), false);
        } else {
            Intent intent = new Intent(this, StartupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        TextView tvName = navHeader.findViewById(R.id.tv_header_name);
        TextView tvBegeleidster = navHeader.findViewById(R.id.tv_header_begeleidster);

        tvBegeleidster.setText(withPhr ? begeleidster : "");
        tvName.setText(name);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        reminderNotification();

        new Thread(new Runnable() {
            @Override
            public void run() {
                BegeleidstersHolder.standard.begeleidsters = getBegeleidsters();
            }
        }).start();

    }

    public void reminderNotification()
    {
        NotificationUtils _notificationUtils = new NotificationUtils(this);
        _notificationUtils.setReminders();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public Map<String, String> getBegeleidsters() {
        try {
            URL url = new URL("https://www.praktijkhoogbegaafd.nl/begeleidsters.json");
            String jsonString = getJson(url);
            JSONObject obj = new JSONObject(jsonString);

            Map<String, String> map = new HashMap<String, String>();
            Iterator<String> keys = obj.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                String value = obj.getString(key);
                map.put(key, value);
            }

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public String getJson(URL url) {
        try (InputStream input = url.openStream()) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder json = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }
            return json.toString();
        } catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            return "error ocurred " + e;
        }
    }
}