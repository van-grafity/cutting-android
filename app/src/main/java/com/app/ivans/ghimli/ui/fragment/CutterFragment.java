package com.app.ivans.ghimli.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ivans.ghimli.R;
import com.app.ivans.ghimli.adapter.CuttingAdapter;
import com.app.ivans.ghimli.model.CuttingOrderRecord;
import com.app.ivans.ghimli.net.API;
import com.app.ivans.ghimli.ui.activity.CuttingOrderRecordDetailActivity;
import com.app.ivans.ghimli.ui.activity.ScanQrActivity;
import com.app.ivans.ghimli.ui.viewmodel.CutterViewModel;
import com.app.ivans.ghimli.ui.viewmodel.CuttingOrderViewModel;
import com.app.ivans.ghimli.utils.Extension;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CutterFragment extends Fragment {

    private CuttingOrderViewModel cuttingOrderViewModel;
    private CuttingAdapter cuttingAdapter;
    RecyclerView rvCuttingOrderRecord;
    private boolean isDataLoaded = false;
    private FloatingActionButton fabScan;

    public static CutterFragment newInstance() {
        return new CutterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cutter, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Cutter");
        rvCuttingOrderRecord = view.findViewById(R.id.rvCuttingOrderRecord);
        fabScan = view.findViewById(R.id.fabScan);
        Extension.showLoading(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cuttingOrderViewModel = new ViewModelProvider(this).get(CuttingOrderViewModel.class);

//        rvCuttingOrderRecord.hasFixedSize();
        rvCuttingOrderRecord.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        cuttingAdapter = new CuttingAdapter(getActivity(), new CuttingAdapter.ItemAdapterOnClickHandler() {
            @Override
            public void onClick(CuttingOrderRecord cuttingOrder, View view, int position) {
                Intent intent = new Intent(getActivity(), CuttingOrderRecordDetailActivity.class);
                intent.putExtra(Extension.CUTTING_ORDER_RECORD, cuttingOrder);
                startActivity(intent);
            }
        });
        if (cuttingOrderViewModel.getCuttingOrderPagedList() != null) {

            cuttingAdapter.submitList(cuttingOrderViewModel.getCuttingOrderPagedList().getValue());

            rvCuttingOrderRecord.setAdapter(cuttingAdapter);
        } else {
            if (!isDataLoaded) {
                // Make the API call
                loadCuttingOrderData();
                isDataLoaded = true;
            }
        }

        fabScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanQrActivity.class);
                intent.putExtra(Extension.CUTTING_CODE, "CUT_CODE");
                intent.putExtra(Extension.CUTTING_QR, "CO");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void loadCuttingOrderData() {
        cuttingOrderViewModel.init(getActivity(), API.getToken(getActivity()), "", "2", "1");
        cuttingOrderViewModel.getCuttingOrderPagedList().observe(getViewLifecycleOwner(), new Observer<PagedList<CuttingOrderRecord>>() {
            @Override
            public void onChanged(PagedList<CuttingOrderRecord> cuttingOrderRecords) {
                cuttingAdapter.submitList(cuttingOrderRecords);
                rvCuttingOrderRecord.setAdapter(cuttingAdapter);
            }
        });
    }

}