package com.app.ivans.ghimli.net;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.app.ivans.ghimli.R;
import com.app.ivans.ghimli.model.APIResponse;
import com.app.ivans.ghimli.model.CuttingOrderRecord;
import com.app.ivans.ghimli.utils.Extension;
import com.app.ivans.ghimli.utils.InternetUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CuttingOrderRecordDataSource extends PageKeyedDataSource<Integer, CuttingOrderRecord> {
    private static final int FIRST_PAGE = 1;
    public static final int PAGE_SIZE = 24;
    private Context context;
    private String authorization;
    private String search;
    private String mStatusLayer;
    private String mStatusCut;

    public CuttingOrderRecordDataSource(Context context, String authorization, String search, String statusLayer, String statusCut) {
        this.context = context;
        this.authorization = authorization;
        this.search = search;
        this.mStatusLayer = statusLayer;
        this.mStatusCut = statusCut;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, CuttingOrderRecord> callback) {
        FAPI.service().getCuttingOrder(authorization, params.requestedLoadSize, FIRST_PAGE, search, mStatusLayer, mStatusCut).enqueue(new APICallback<APIResponse>(context) {
            @Override
            protected void onSuccess(APIResponse apiResponse) {
                if (InternetUtil.isInternetOn()){
                    callback.onResult(apiResponse.getData().getCuttingOrderRecords(), null, FIRST_PAGE + 1);
                    Extension.dismissLoading();
                } else {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        public void run() {
                            Extension.dismissLoading();
                        }
                    });
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle(context.getString(R.string.sorry));
                    alertDialog.setMessage("Periksa Jaringan Internet.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.dialog_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }

            @Override
            protected void onError(BadRequest error) {
                Log.d("DataSource", "onError: " + error.errors);
            }
        });
    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, CuttingOrderRecord> callback) {
        FAPI.service().getCuttingOrder(authorization, PAGE_SIZE, params.key, search, mStatusLayer, mStatusCut).enqueue(new APICallback<APIResponse>(context) {
            @Override
            protected void onSuccess(APIResponse apiResponse) {
                Integer key = (params.key > 1) ? params.key - 1 : null;
                callback.onResult(apiResponse.getData().getCuttingOrderRecords(), key);
            }

            @Override
            protected void onError(BadRequest error) {
                Log.d("DataSource", "onError: " + error.errors);
            }
        });
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, CuttingOrderRecord> callback) {
        FAPI.service().getCuttingOrder(authorization, params.requestedLoadSize, params.key, search, mStatusLayer, mStatusCut).enqueue(new APICallback<APIResponse>(context) {
            @Override
            protected void onSuccess(APIResponse apiResponse) {
                Integer key = (params.key < apiResponse.getData().getTotalPage()) ? params.key + 1 : null;
                callback.onResult(apiResponse.getData().getCuttingOrderRecords(), key);
            }

            @Override
            protected void onError(BadRequest error) {
                Log.d("DataSource", "onError: " + error.errors);
            }
        });
    }
}