package com.app.ivans.ghimli.ui.viewmodel;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.app.ivans.ghimli.model.APIResponse;
import com.app.ivans.ghimli.model.CuttingOrderRecordDetail;
import com.app.ivans.ghimli.repository.CuttingRepository;

public class CuttingViewModel extends ViewModel {

    private CuttingRepository favoriteRepository;
    private LiveData<APIResponse> productResponseData;

    public CuttingViewModel() {
        Context context = new Activity();
        favoriteRepository = new CuttingRepository(context);
    }

    public LiveData<APIResponse> createOptCuttingOrderLiveData(String auth, String serialNumber, String fabricRoll, String fabricBatch, String color, double yardage, double weight, int layer, String joint, String balanceEnd, String remarks, String operator, int user_id) {
        productResponseData = favoriteRepository.createOptCuttingOrderResponse(auth, serialNumber, fabricRoll, fabricBatch, color, yardage, weight, layer, joint, balanceEnd, remarks, operator, user_id);
        return productResponseData;
    }

    public LiveData<APIResponse> updateOptCuttingOrderLiveData(String auth, int id, String serialNumber, String fabricRoll, String fabricBatch, String color, double yardage, double weight, int layer, String joint, String balanceEnd, String remarks, String operator, int user_id) {
        productResponseData = favoriteRepository.updateOptCuttingOrderResponse(auth, id, serialNumber, fabricRoll, fabricBatch, color, yardage, weight, layer, joint, balanceEnd, remarks, operator, user_id);
        return productResponseData;
    }

    public LiveData<APIResponse> destroyOptCuttingOrderLiveData(String auth, int id) {
        productResponseData = favoriteRepository.destroyOptCuttingOrderResponse(auth, id);
        return productResponseData;
    }

    public LiveData<APIResponse> createOptCuttingOrderObj(String auth, CuttingOrderRecordDetail cuttingOrderRecordDetail) {
        productResponseData = favoriteRepository.createOptCuttingOrderResponseObj(auth, cuttingOrderRecordDetail);
        return productResponseData;
    }

    public LiveData<APIResponse> getCuttingOrderLiveData(String auth) {
//        productResponseData = favoriteRepository.getCuttingOrderResponse(auth);
        return productResponseData;
    }

    public LiveData<APIResponse> getColorLiveData(String auth) {
        productResponseData = favoriteRepository.getColorResponse(auth);
        return productResponseData;
    }

    public LiveData<APIResponse> getCuttingOrderBySerialNumberLiveData(String auth, String serialNumber) {
        productResponseData = favoriteRepository.getCuttingOrderBySerialNumberResponse(auth, serialNumber);
        return productResponseData;
    }

    public LiveData<APIResponse> getLayingPlanningBySerialNumberLiveData(String auth, String serialNumber) {
        productResponseData = favoriteRepository.getLayingPlanningBySerialNumberResponse(auth, serialNumber);
        return productResponseData;
    }

    public LiveData<APIResponse> postStatusCutBySerialNumberLiveData(String auth, String serialNumber, String status) {
        productResponseData = favoriteRepository.postStatusCutResponse(auth, serialNumber, status);
        return productResponseData;
    }
    
    public LiveData<APIResponse> searchCuttingOrderLiveData(String auth, String serialNumber) {
        productResponseData = favoriteRepository.searchCuttingOrderResponse(auth, serialNumber);
        return productResponseData;
    }

    public LiveData<APIResponse> getRemarksLiveData(String auth) {
        productResponseData = favoriteRepository.getRemarksResponse(auth);
        return productResponseData;
    }

    public LiveData<APIResponse> getBundleStatusLiveData(String auth) {
        productResponseData = favoriteRepository.getBundleStatusResponse(auth);
        return productResponseData;
    }

    public LiveData<APIResponse> getCuttingTicketDetailLiveData(String auth, String serialNumber) {
        productResponseData = favoriteRepository.getCuttingTicketDetailResponse(auth, serialNumber);
        return productResponseData;
    }

    public LiveData<APIResponse> bundleTransferLiveData(String auth, String serialNumber, String transactionType, int location) {
        productResponseData = favoriteRepository.bundleTransferResponse(auth, serialNumber, transactionType, location);
        return productResponseData;
    }

    public LiveData<APIResponse> bundleTransferMultipleLiveData(String auth, String[] serialNumber, String transactionType, int location) {
        productResponseData = favoriteRepository.bundleTransferMultipleResponse(auth, serialNumber, transactionType, location);
        return productResponseData;
    }
}