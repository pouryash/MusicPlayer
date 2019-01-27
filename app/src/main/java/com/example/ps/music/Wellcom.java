package com.example.ps.music;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

/**
 * Created by poorya on 9/14/2018.
 */

public class Wellcom extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences.Editor showOnce = getSharedPreferences("showOnce",MODE_PRIVATE).edit();
        showOnce.putBoolean("showOnce",true);
        showOnce.apply();


        SliderPage page1 = new SliderPage();
        page1.setTitle("Exciting Music");
        page1.setDescription("organize your time with our music list");
        page1.setImageDrawable(R.drawable.ic_headset);
        page1.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        addSlide(AppIntroFragment.newInstance(page1));

        SliderPage page2 = new SliderPage();
        page2.setTitle("Free App");
        page2.setDescription("this is free application for everyone");
        page2.setImageDrawable(R.drawable.ic_karaoke_microphone_icon);
        page2.setBgColor(getResources().getColor(R.color.colorWellcomeBlue));
        addSlide(AppIntroFragment.newInstance(page2));

        SliderPage page3 = new SliderPage();
        page3.setTitle("Enjoy Our App");
        page3.setDescription("get started");
        page3.setImageDrawable(R.drawable.ic_power);
        page3.setBgColor(getResources().getColor(R.color.colorWellcomeGreen));
        addSlide(AppIntroFragment.newInstance(page3));

        // OPTIONAL METHODS
        // Override bar/separator color.

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need isSeekbarTouch ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        startActivity(new Intent(this , MainActivity.class));
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(this,MainActivity.class));
    }
}
