package com.app.ivans.ghimli;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.app.ivans.ghimli.databinding.ActivityScanQrBinding;
import com.app.ivans.ghimli.model.APIResponse;
import com.app.ivans.ghimli.net.API;
import com.app.ivans.ghimli.utils.Extension;
import com.app.ivans.ghimli.utils.NetworkChangeReceiver;
import com.app.ivans.ghimli.utils.OnNetworkListener;
import com.app.ivans.ghimli.viewmodel.CuttingViewModel;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;

public class ScanQrActivity extends AppCompatActivity implements OnNetworkListener {
    private static final String TAG = "CuttingLayingSheetScanQ";
    private ActivityScanQrBinding binding;
    private CuttingViewModel cuttingViewModel;

    private CodeScannerView mScannerView;
    private CodeScanner mCodeScanner;
    private final int CAMERA_REQUEST_CODE = 101;
    private Snackbar snack;
    private NetworkChangeReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanQrBinding.inflate(LayoutInflater.from(ScanQrActivity.this));
        setContentView(binding.getRoot());
        mNetworkReceiver = new NetworkChangeReceiver();
        mNetworkReceiver.setOnNetworkListener(this);
        snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE);
        setupPermissions();
        cuttingViewModel = new ViewModelProvider(ScanQrActivity.this).get(CuttingViewModel.class);

        mScannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, mScannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message = result.getText();
                        String partStr = message.substring(0, 2);

                        if (partStr.equals("CO")) {
//                            Toast.makeText(CuttingLayingSheetScanQrActivity.this, message, Toast.LENGTH_SHORT).show();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Extension.showLoading(ScanQrActivity.this);
                                }
                            });
                            cuttingViewModel.getLayingPlanningBySerialNumberLiveData(API.getToken(ScanQrActivity.this), message).observe(ScanQrActivity.this, new Observer<APIResponse>() {
                                @Override
                                public void onChanged(APIResponse apiResponse) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Extension.dismissLoading();
                                        }
                                    });
                                    if (apiResponse.getStatus() == 404) {

                                        AlertDialog alertDialog = new AlertDialog.Builder(ScanQrActivity.this).create();
                                        alertDialog.setTitle(getString(R.string.sorry));
                                        alertDialog.setMessage(apiResponse.getMessage());
                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alertDialog.show();
                                        finish();

                                    }
                                    Intent intent = new Intent(ScanQrActivity.this, CuttingOrderRecordFormActivity.class);
                                    intent.putExtra("serialNumber", message);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else if (partStr.equals("CT")) {
//                            Toast.makeText(CuttingLayingSheetScanQrActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ScanQrActivity.this, CuttingTicketDetailActivity.class);
                            intent.putExtra("serialNumber", message);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ScanQrActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                        }

//                        if (partStr.equals("CO")) {
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Extension.showLoading(CuttingLayingSheetScanQrActivity.this);
//                                }
//                            });
//                            cuttingViewModel.getLayingPlanningBySerialNumberLiveData(API.getToken(CuttingLayingSheetScanQrActivity.this), message).observe(CuttingLayingSheetScanQrActivity.this, new Observer<APIResponse>() {
//                                @Override
//                                public void onChanged(APIResponse apiResponse) {
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            Extension.dismissLoading();
//                                        }
//                                    });
//                                    if (apiResponse.getStatus() == 404) {
//
//                                        AlertDialog alertDialog = new AlertDialog.Builder(CuttingLayingSheetScanQrActivity.this).create();
//                                        alertDialog.setTitle(getString(R.string.sorry));
//                                        alertDialog.setMessage(apiResponse.getMessage());
//                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_ok),
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int which) {
//                                                        dialog.dismiss();
//                                                    }
//                                                });
//                                        alertDialog.show();
//                                        finish();
//
//                                    }
//                                    Intent intent = new Intent(CuttingLayingSheetScanQrActivity.this, CuttingOrderRecordFormActivity.class);
//                                    intent.putExtra("serialNumber", message);
//                                    startActivity(intent);
//                                }
//                            });
//                        } else if (partStr.equals("CT")) {
//                            startActivity(new Intent(CuttingLayingSheetScanQrActivity.this, CuttingTicketDetailActivity.class));
//                        } else {
//                            Toast.makeText(CuttingLayingSheetScanQrActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(CuttingLayingSheetScanQrActivity.this, HomeActivity.class));
//                        }
                    }
                });
            }
        });
    }

    private void setupPermissions() {
        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    /**
     * Requests permission to access the camera
     * @param requestCode is the request code
     * @param permissions is a string that seeks to access the camera
     * @param grantResults is a request code
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showSnackBar() {
        snack.setAction("CLOSE", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snack.dismiss();
            }
        });
        snack.setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright));
        snack.show();
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            registerNetworkBroadcastForNougat();
        } catch (Exception e) {
            Log.d(TAG, "onStart: " + "already registered");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
            Log.d(TAG, "onStop: " + "already unregistered");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
        try {
            registerNetworkBroadcastForNougat();
        } catch (Exception e) {
            Log.d(TAG, "onStart: " + "already registered");
        }
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
        Log.i(TAG, "onPause: unregisterReceiver");
        unregisterReceiver(mNetworkReceiver);
    }

    @Override
    public void onNetworkConnected() {
        snack.dismiss();
    }

    @Override
    public void onNetworkDisconnected() {
        showSnackBar();
    }
}