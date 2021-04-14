package nl.avans.praktijkhoogbegaafd;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import nl.avans.praktijkhoogbegaafd.dal.FeelingsDB;
import nl.avans.praktijkhoogbegaafd.logic.FeelingsEntityManager;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    public static String name = "";
    public static String begeleidster = "";
    public static String birthDay = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FeelingsEntityManager fem = new FeelingsEntityManager(getApplication());

        Room.databaseBuilder(this, FeelingsDB.class, "feelingsDB");


        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if(sharedPref.contains("Name")){
            name = sharedPref.getString("Name", "?");
        }
        if(sharedPref.contains("Birthday")){
            birthDay = sharedPref.getString("Birthday", "?");
        }
        if(sharedPref.contains("Begeleidster")){
            begeleidster = sharedPref.getString("Begeleidster", "?");
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        View navHeader = navigationView.getHeaderView(0);
        TextView tvName = navHeader.findViewById(R.id.tv_header_name);
        TextView tvBegeleidster = navHeader.findViewById(R.id.tv_header_begeleidster);

        tvBegeleidster.setText(begeleidster);
        tvName.setText(name);




        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}