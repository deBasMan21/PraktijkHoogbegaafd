package nl.avans.praktijkhoogbegaafd.logic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class ScreenshotLogic {

    public static Bitmap takeScreenshot(View v){
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap bitmap = v.getDrawingCache();
        Bitmap b = Bitmap.createBitmap(bitmap);
        v.setDrawingCacheEnabled(true);
        b.setHasAlpha(true);

        Bitmap newBitmap = Bitmap.createBitmap(b.getWidth(),
                b.getHeight(), b.getConfig());
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(b, 0F, 0F, null);
        return newBitmap;
    }
}
