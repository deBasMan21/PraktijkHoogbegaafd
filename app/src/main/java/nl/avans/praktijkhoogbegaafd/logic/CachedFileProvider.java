package nl.avans.praktijkhoogbegaafd.logic;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;

public class CachedFileProvider extends ContentProvider {
    public static final String AUTHORITY = "nl.avans.logic.provider";
    private UriMatcher uriMatcher;
    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "*", 1);
        return true;
    }
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        switch (uriMatcher.match(uri)) {
            case 1:// If it returns 1 - then it matches the Uri defined in onCreate
                String fileLocation = getContext().getCacheDir() + File.separator +     uri.getLastPathSegment();
                ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(fileLocation),     ParcelFileDescriptor.MODE_READ_ONLY);
                return pfd;
            default:// Otherwise unrecognised Uri
                throw new FileNotFoundException("Unsupported uri: " + uri.toString());
        }
    }
    @Override public int update(Uri uri, ContentValues contentvalues, String s, String[] as) { return     0; }
    @Override public int delete(Uri uri, String s, String[] as) { return 0; }
    @Override public Uri insert(Uri uri, ContentValues contentvalues) { return null; }
    @Override public String getType(Uri uri) { return null; }
    @Override public Cursor query(Uri uri, String[] projection, String s, String[] as1, String s1) {     return null; }
}
