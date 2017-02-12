package com.acenosekai.ant3x.factory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.acenosekai.ant3x.App;
import com.acenosekai.ant3x.ForegroundService;
import com.acenosekai.ant3x.MainActivity;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public abstract class BaseFragment extends Fragment {
    protected MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        backButton();

    }



    public void backButton(){
        mainActivity.backTo(backFragment());
    }

    abstract public BaseFragment backFragment();



}
