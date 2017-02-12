package com.acenosekai.ant3x.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acenosekai.ant3x.R;
import com.acenosekai.ant3x.factory.BaseFragment;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class OpeningFragment extends BaseFragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_opening,container,false);
    }

    @Override
    public BaseFragment backFragment() {
        return null;
    }
}
