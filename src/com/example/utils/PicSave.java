package com.example.utils;

/**
 * Created by sharma on 5/11/16.
 */

        import java.io.File;
        import java.io.FileOutputStream;

        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Bitmap.CompressFormat;
        import android.os.Environment;
        import android.widget.Toast;

public class PicSave  {
    public static File getSavePath() {
        File path;
        if (hasSDCard()) {
            path = new File(getSDCardPath() + "/shawon/");
            path.mkdir();
        } else {
            path = Environment.getDataDirectory();
        }
        return path;
    }


    public static String getCacheFilename() {
        File f = getSavePath();
        return f.getAbsolutePath() + "/cache.png";
    }

    public static Bitmap loadFromFile(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) {
                return null;
            }
            Bitmap tmp = BitmapFactory.decodeFile(filename);
            return tmp;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap loadFromCacheFile() {
        return loadFromFile(getCacheFilename());
    }

    public static void saveToCacheFile(Bitmap bmp) {
        saveToFile(getCacheFilename(), bmp);
    }

    public static void saveToFile(String filename, Bitmap bmp) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            bmp.compress(CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
        }
    }

    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static String getSDCardPath() {
        File path = Environment.getExternalStorageDirectory();
        return path.getAbsolutePath();
    }

}