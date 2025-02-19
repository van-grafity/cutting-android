package com.app.ivans.ghimli;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.app.ivans.ghimli.databinding.ActivityMainBinding;
import com.app.ivans.ghimli.model.User;
import com.app.ivans.ghimli.ui.activity.LoginActivity;
import com.app.ivans.ghimli.ui.activity.MenuActivity;
import com.app.ivans.ghimli.utils.Extension;

import br.com.kots.mob.complex.preferences.ComplexPreferences;

public class MainActivity extends AppCompatActivity {
    private boolean mIsPortrait;
    private ActivityMainBinding binding;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(MainActivity.this));
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        mIsPortrait = getResources().getBoolean(R.bool.portrait_only);
//        if(mIsPortrait){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//        else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//        }

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, Extension.PREF_USER, MODE_PRIVATE);
        mUser = complexPreferences.getObject(Extension.PREF_USER_KEY, User.class);
        if (mUser == null || mUser.getId() == 0) {
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
            finish();
            return;
        } else {
            startActivity(new Intent(getBaseContext(), MenuActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setTitle(getString(R.string.app_name));
        alertDialogBuilder
                .setMessage("Apakah Anda yakin ingin keluar dari Aplikasi?")
                .setCancelable(true)
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}