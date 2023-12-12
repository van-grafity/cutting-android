package com.app.ivans.ghimli.ui.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ivans.ghimli.R;
import com.app.ivans.ghimli.ui.activity.CuttingOrderRecordDetailActivity;
import com.app.ivans.ghimli.ui.activity.HomeActivity;
import com.app.ivans.ghimli.ui.activity.ScanQrActivity;
import com.app.ivans.ghimli.ui.viewmodel.CutPieceStockViewModel;
import com.app.ivans.ghimli.utils.Extension;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CutPieceStockFragment extends Fragment {

    private CutPieceStockViewModel mViewModel;
    private FloatingActionButton fabScanBundle;

    public static CutPieceStockFragment newInstance() {
        return new CutPieceStockFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cut_piece_stock, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Cut Piece Stock");
        fabScanBundle = view.findViewById(R.id.fabScanBundle);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CutPieceStockViewModel.class);
        // TODO: Use the ViewModel

        fabScanBundle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanQrActivity.class);
                intent.putExtra(Extension.CUTTING_QR, "CT");
                startActivity(intent);
            }
        });
    }

}