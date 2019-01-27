package com.example.ps.music;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.ps.music.commen.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by poorya on 9/22/2018.
 */

public class Constants {





    public interface ACTION {
            public static String MAIN_ACTION = "com.marothiatechs.customnotification.action.main";
            public static String INIT_ACTION = "com.marothiatechs.customnotification.action.init";
            public static String PREV_ACTION = "com.marothiatechs.customnotification.action.prev";
            public static String PLAY_ACTION = "com.marothiatechs.customnotification.action.play";
            public static String NEXT_ACTION = "com.marothiatechs.customnotification.action.next";
            public static String STARTFOREGROUND_ACTION = "com.marothiatechs.customnotification.action.startforeground";
            public static String STOPFOREGROUND_ACTION = "com.marothiatechs.customnotification.action.stopforeground";

        }

        public interface NOTIFICATION_ID {
            public static int FOREGROUND_SERVICE = 101;
        }

        public static Bitmap getDefaultAlbumArt(Context context , String url) {
            Bitmap bm = null;
               BitmapFactory.Options options = new BitmapFactory.Options();
               try {
                   bm = BitmapFactory.decodeResource(context.getResources(),
                           R.drawable.no_image, options);
               } catch (Error | Exception ignored) {

           }
            return bm;
        }
        public static Bitmap getDefaultAlbumAt(Context context , final String url) {
            Bitmap bm = null;

            return bm;

        }

}
