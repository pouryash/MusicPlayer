package com.example.ps.music.commen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.example.ps.music.R;
import com.example.ps.music.model.Song;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by poorya on 9/22/2018.
 */

public class Image {

    public static boolean isValidImage(String url) {

        String[] imageExtension = url.split("\\.", 0);
        String[] allExtension = {"png", "PNG", "jpg", "JPG", "JPEG", "jpeg", "gif", "GIF"};

        if ( url.startsWith("https://") || url.startsWith("http://")) {
            for (String s : imageExtension) {
                if (s.length() == 3 || s.length() == 4) {
                    for (String e : allExtension) {
                        if (e.equals(s)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //    public static int getDominantColor(Bitmap bitmap) {
//        List<Palette.Swatch> swatchesTemp = Palette.from(bitmap).generate().getSwatches();
//        List<Palette.Swatch> swatches = new ArrayList<Palette.Swatch>(swatchesTemp);
//        Collections.sort(swatches, new Comparator<Palette.Swatch>() {
//            @Override
//            public int compare(Palette.Swatch swatch1, Palette.Swatch swatch2) {
//                return swatch2.getPopulation() - swatch1.getPopulation();
//            }
//        });
//        return swatches.size() > 0 ? swatches.get(0).getRgb() : Color.argb(50, 50, 50, 50);
//    }


    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Drawable uriToDrawable(String path , Context context){
        Drawable yourDrawable;
        Uri uri =Uri.parse(path);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            yourDrawable = Drawable.createFromStream(inputStream, path );
        } catch (FileNotFoundException e) {
            yourDrawable = context.getResources().getDrawable(R.drawable.no_image);
        }
        return yourDrawable;
    }
}
