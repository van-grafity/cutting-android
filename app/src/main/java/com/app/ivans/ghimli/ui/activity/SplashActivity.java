package com.app.ivans.ghimli.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.app.ivans.ghimli.MainActivity;
import com.app.ivans.ghimli.R;
import com.app.ivans.ghimli.databinding.ActivitySplashBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(LayoutInflater.from(SplashActivity.this));
        setContentView(binding.getRoot());

        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.anim_in);
        binding.ivSplash.startAnimation(animation);

        new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(1000);
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage());
                } finally {
                    if (Build.VERSION.SDK_INT > 22) {
                        Dexter.withActivity(SplashActivity.this)
                                .withPermissions(
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.READ_SMS,
                                        Manifest.permission.RECEIVE_SMS,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION

                                ).withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                next();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

                    } else {
                        next();
                    }
                }
            }
        }.start();
    }

    private void next() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }

    //    private void next() {
//        if (API.currentUser(SplashActivity.this).getUserToken() != null) {
//            if (API.currentUser(SplashActivity.this) != null) {
//                startActivity(new Intent(this, MainActivity.class));
//                finish();
//            } else {
//                startActivity(new Intent(this, LoginActivity.class));
//                finish();
//            }
//        } else {
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//        }
//    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}