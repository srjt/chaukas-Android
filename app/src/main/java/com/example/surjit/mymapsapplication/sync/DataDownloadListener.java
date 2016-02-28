package com.example.surjit.mymapsapplication.sync;

import com.example.surjit.mymapsapplication.models.Incident;

import java.util.ArrayList;

/**
 * Created by surjit on 1/13/2016.
 */
public  interface DataDownloadListener {
    void dataDownloadedSuccessfully(ArrayList<Incident> data);
    void dataDownloadFailed(String errorMsg);
}
