package com.app.ivans.ghimli.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.ivans.ghimli.R;
import com.app.ivans.ghimli.adapter.CuttingAdapter;
import com.app.ivans.ghimli.adapter.DepartmentAdapter;
import com.app.ivans.ghimli.databinding.ActivityHomeBinding;
import com.app.ivans.ghimli.databinding.ToolbarBinding;
import com.app.ivans.ghimli.model.APIResponse;
import com.app.ivans.ghimli.model.CuttingOrderRecord;
import com.app.ivans.ghimli.model.Department;
import com.app.ivans.ghimli.model.FGL;
import com.app.ivans.ghimli.net.API;
import com.app.ivans.ghimli.utils.BannerImageLoader;
import com.app.ivans.ghimli.utils.Extension;
import com.app.ivans.ghimli.ui.viewmodel.CuttingOrderViewModel;
import com.app.ivans.ghimli.ui.viewmodel.CuttingViewModel;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private ActivityHomeBinding binding;
    private ToolbarBinding toolbarBinding;
    private ArrayList<Department> mItemSearchEngine;
    private ArrayList<CuttingOrderRecord> mItemCuttingOrderRecord;
    private CuttingViewModel cuttingViewModel;
    private CuttingOrderViewModel cuttingOrderViewModel;
    ArrayList<Integer> mItems = new ArrayList<>();

    private LineDataSet totalsDataSet;
    private ArrayList totals;
    private ArrayList<String> dates;
    private boolean mIsPortrait;
    CuttingAdapter cuttingAdapter;

    private Handler handler;

    private long lastTotalRxBytes = 0;
    private long lastTotalTxBytes = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(LayoutInflater.from(HomeActivity.this));
        setContentView(binding.getRoot());
        toolbarBinding = binding.toolbar;
        setSupportActionBar(toolbarBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mIsPortrait = getResources().getBoolean(R.bool.portrait_only);
//        if(mIsPortrait){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            layoutManager = new GridLayoutManager(HomeActivity.this, 2, LinearLayoutManager.VERTICAL, false);
//        }
//        else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//            layoutManager = new GridLayoutManager(HomeActivity.this, 3, LinearLayoutManager.VERTICAL, false);
//        }
//        binding.rvCuttingOrderRecord.setHasFixedSize(true);

        toolbarBinding.tvTitleLarge.setVisibility(View.GONE);
        toolbarBinding.tvTitleLarge.setText("Ghim Li Indonesia");
        toolbarBinding.ivLogoStore.setVisibility(View.VISIBLE);
        toolbarBinding.txtSearch.setVisibility(View.VISIBLE);

        handler = new Handler(Looper.getMainLooper());

        // Start the speed monitoring task
        startSpeedMonitoring();

        toolbarBinding.txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                 startActivity(new Intent(getActivity(), SearchActivity.class));
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));

            }
        });

//        binding.swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.BLUE);
        binding.tvName.setText(API.currentUser(HomeActivity.this).getName());

        cuttingViewModel = new ViewModelProvider(HomeActivity.this).get(CuttingViewModel.class);
        cuttingOrderViewModel = new ViewModelProvider(HomeActivity.this).get(CuttingOrderViewModel.class);

        binding.rvCuttingOrderRecord.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2, LinearLayoutManager.VERTICAL, false));
//        layoutManager = new GridLayoutManager(HomeActivity.this, 2, LinearLayoutManager.VERTICAL, false);

        loadDataDepartment();
        loadDataCuttingOrderRecord();
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefreshLayout.setRefreshing(false);
                refreshItem();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Extension.showLoading(HomeActivity.this);
                    }
                });

            }

            private void refreshItem() {
                loadDataCuttingOrderRecord();
            }

            void onItemLoad() {

            }
        });


        banner();

        totals = new ArrayList();
        dates = new ArrayList();

        Gson gson = new Gson();
        APIResponse datas = gson.fromJson(loadJSONFromRes(), APIResponse.class);
        Log.i(TAG, "onCreate: json " + datas.getData().getGl().get(0).getBuyer());

        for (int i = 0; i < datas.getData().getGl().size(); i++) {
            FGL FGL = datas.getData().getGl().get(i);
            Log.i(TAG, "onCreate: " + FGL.getBuyer());
            totals.add(new Entry(i, (float) FGL.getTotal()));
            dates.add(FGL.getDate());

        }

        setDataTotal();

        binding.fabScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ScanQrActivity.class));
            }
        });

        binding.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                API.logOut(HomeActivity.this);
            }
        });
    }

    private void startSpeedMonitoring() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateSpeed();
                handler.postDelayed(this, 1000); // Update speed every 1 second
            }
        }, 1000); // Start the task after 1 second
    }

    private void updateSpeed() {
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();

        long downSpeed = totalRxBytes - lastTotalRxBytes;
        long upSpeed = totalTxBytes - lastTotalTxBytes;

        lastTotalRxBytes = totalRxBytes;
        lastTotalTxBytes = totalTxBytes;

        String downSpeedStr = formatSpeed(downSpeed);
        String upSpeedStr = formatSpeed(upSpeed);
        toolbarBinding.txtSearch.setText("Up :"+ upSpeedStr +" "+"Down :"+downSpeedStr);
        Log.i(TAG, "Down Speed: "+downSpeedStr);
        Log.i(TAG, "Up Speed: "+upSpeedStr);
    }

    private String formatSpeed(long speedBytes) {
        if (speedBytes < 1024) {
            return speedBytes + " B/s";
        } else if (speedBytes < 1024 * 1024) {
            return String.format("%.2f", speedBytes / 1024.0) + " KB/s";
        } else {
            return String.format("%.2f", speedBytes / (1024.0 * 1024.0)) + " MB/s";
        }
    }

    void banner() {

        mItems.add(R.drawable.ic_launcher_background);
        mItems.add(R.drawable.ic_launcher_background);
        mItems.add(R.drawable.ic_launcher_background);
        mItems.add(R.drawable.ic_launcher_background);

        binding.banner.setImages(mItems);

        binding.banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        binding.banner.setIndicatorGravity(BannerConfig.CENTER);
        binding.banner.setImageLoader(new BannerImageLoader());
        binding.banner.setBannerAnimation(Transformer.Default);
        binding.banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Toast.makeText(HomeActivity.this, "Comming soon", Toast.LENGTH_SHORT).show();
            }
        });
        binding.banner.isAutoPlay(false);

        binding.banner.start();
    }

    public void setDataTotal() {
        XAxis xAxis = binding.chart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.mAxisMaximum = (float) dates.size() - 1;
        xAxis.mAxisMinimum = 0f;
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = binding.chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        //leftAxis.axisMaximum = (y.size - 1).toFloat()
        leftAxis.mAxisMinimum = 0f;
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);
        //leftAxis.setValueFormatter { value, _ ->
        //moneys[value.toInt()]
        //}

        totalsDataSet = new LineDataSet(totals, "Totals");
        totalsDataSet.setDrawIcons(false);
        totalsDataSet.enableDashedLine(10f, 5f, 0f);
        totalsDataSet.enableDashedHighlightLine(10f, 5f, 0f);
        totalsDataSet.setColors(Color.parseColor("#00B082"));
        totalsDataSet.setCircleColor(Color.parseColor("#00B082"));
        totalsDataSet.setLineWidth(1f);
        totalsDataSet.setCircleRadius(3f);
        totalsDataSet.setDrawCircleHole(false);
        totalsDataSet.setValueTextSize(9f);
        totalsDataSet.setDrawFilled(true);
        totalsDataSet.setDrawValues(false);
        totalsDataSet.setFormLineWidth(1f);
        float[] floatArray = new float[]{10f, 5f};
        totalsDataSet.setFormLineDashEffect(new DashPathEffect(floatArray, 0f));
        totalsDataSet.setFormSize(15f);

        if (Utils.getSDKInt() >= 18) {
            //val drawable = ContextCompat.getDrawable(context!!, R.drawable.fade_blue)
//            ContextCompat drawable2 = ContextCompat.getDrawable(DashboardAnalyticsActivity.this, getResources().getDrawable(0));
            totalsDataSet.setFillDrawable(null);
        } else {
            totalsDataSet.setFillColor(Color.parseColor("#00B082"));
        }

        ArrayList dataSets = new ArrayList<>();
        dataSets.add(totalsDataSet);
        LineData data = new LineData(dataSets);

        binding.chart.setData(data);
        binding.chart.invalidate();
    }

    public String loadJSONFromRes() {
        String data = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.data);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            data = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return data;
    }

    public void loadDataCuttingOrderRecord() {
        cuttingAdapter = new CuttingAdapter(HomeActivity.this, new CuttingAdapter.ItemAdapterOnClickHandler() {
            @Override
            public void onClick(CuttingOrderRecord cuttingOrder, View view, int position) {
                Intent intent = new Intent(HomeActivity.this, CuttingOrderRecordDetailActivity.class);
                intent.putExtra(Extension.CUTTING_ORDER_RECORD, cuttingOrder);
                startActivity(intent);
            }
        });

        cuttingOrderViewModel.init(HomeActivity.this, API.getToken(HomeActivity.this), "", "", "");
        cuttingOrderViewModel.getCuttingOrderPagedList().observe(HomeActivity.this, new Observer<PagedList<CuttingOrderRecord>>() {
            @Override
            public void onChanged(PagedList<CuttingOrderRecord> cuttingOrderRecords) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Extension.dismissLoading();
                    }
                });
                cuttingAdapter.submitList(cuttingOrderRecords);
                binding.rvCuttingOrderRecord.setAdapter(cuttingAdapter);

            }
        });

    }

    public void loadDataDepartment() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(HomeActivity.this, 2, LinearLayoutManager.VERTICAL, false);

        mItemSearchEngine = new ArrayList<>();
        mItemSearchEngine.add(new Department("IT", "https://www.google.com/search?q=", R.drawable.ic_launcher_background));
        mItemSearchEngine.add(new Department("Compliance", "https://m.facebook.com/", R.drawable.ic_launcher_background));
        mItemSearchEngine.add(new Department("Human Resource", "https://m.youtube.com/", R.drawable.ic_launcher_background));
        mItemSearchEngine.add(new Department("Maintenance", "https://detik.com/", R.drawable.ic_launcher_background));
        mItemSearchEngine.add(new Department("Payroll", "https://mobile.twitter.com/", R.drawable.ic_launcher_background));
        mItemSearchEngine.add(new Department("Store", "https://search.yahoo.com/?q=", R.drawable.ic_launcher_background));

        DepartmentAdapter mAdapter = new DepartmentAdapter(mItemSearchEngine, HomeActivity.this, new DepartmentAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View view, int position, Department searchEngine) {
                Toast.makeText(HomeActivity.this, "Comming soon", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvDept.setHasFixedSize(true);
        binding.rvDept.setLayoutManager(layoutManager);
        binding.rvDept.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.loadDataCuttingOrderRecord();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toolbarBinding = null;
        binding = null;
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacksAndMessages(null);
        finish();
    }

    //    ROP UserBankAccountActivity.class
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
                case BottomSheetBehavior.STATE_COLLAPSED:
                    break;
                case BottomSheetBehavior.STATE_DRAGGING:
                    break;
                case BottomSheetBehavior.STATE_EXPANDED:
                    break;
                case BottomSheetBehavior.STATE_HIDDEN:
                    break;
                case BottomSheetBehavior.STATE_SETTLING:

                    break;
                default:
                    break;
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };
}