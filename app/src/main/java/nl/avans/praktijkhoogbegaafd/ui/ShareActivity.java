package nl.avans.praktijkhoogbegaafd.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import nl.avans.praktijkhoogbegaafd.BuildConfig;
import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.logic.CachedFileProvider;
import nl.avans.praktijkhoogbegaafd.logic.ScreenshotLogic;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File file = storeScreenshot(ScreenshotLogic.lastScreenshot, "screenshot1");

        if(file.canRead() && file.exists()){
            System.out.println("GELUKT");
        }

        Uri uri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider", file);

        Uri uri2 = Uri.parse(file.getPath());

        File file1 = new File(uri2.getPath());
        if(file1.exists() && file1.canRead()){
            System.out.println("GELUKT");
        }

        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:"));
//        i.setType("message/rfc822");
//        i.putExtra(Intent.EXTRA_STREAM, uri);
        i.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + CachedFileProvider.AUTHORITY + "/" + file.getName()));
        startActivity(Intent.createChooser(i, "Send email..."));

    }


    public File storeScreenshot(Bitmap bitmap, String filename) {
        FileOutputStream fout = null;
        File tempFile = null;
        try{
            File tempDir = this.getCacheDir();
            tempFile = File.createTempFile("your_file", ".txt", tempDir);
            fout = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.flush();
            fout.close();

        }catch (Exception e){
            e.printStackTrace();
        }
//
//
//
//        ContextWrapper cw = new ContextWrapper(this);
//        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        File file = new File(directory, filename + ".jpg");
//        file.setReadable(true);
//        if (file.exists()) {
//            file.delete();
//        }
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (java.io.IOException e) {
//            e.printStackTrace();
//        }
        return tempFile;
    }
}