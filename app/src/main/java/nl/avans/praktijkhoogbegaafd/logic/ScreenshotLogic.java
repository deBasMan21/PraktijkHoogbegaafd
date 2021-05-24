package nl.avans.praktijkhoogbegaafd.logic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import java.io.File;

public class ScreenshotLogic {

    public static Bitmap lastScreenshot;

    public static Bitmap takeScreenshot(View v){
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(true);
        lastScreenshot = b;
        return b;
    }

    public static Bitmap takescreenshotOfRootView(View v){
        return takeScreenshot(v.getRootView());
    }

    public static void startIntent(){

    }


}
