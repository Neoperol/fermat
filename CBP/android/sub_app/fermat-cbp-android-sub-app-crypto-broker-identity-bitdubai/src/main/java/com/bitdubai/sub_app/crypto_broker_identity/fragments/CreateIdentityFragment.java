package com.bitdubai.sub_app.crypto_broker_identity.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitdubai.fermat_android_api.layer.definition.wallet.FermatFragment;
import com.bitdubai.sub_app.crypto_broker_identity.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateIdentityFragment extends FermatFragment {


    public CreateIdentityFragment() {
        // Required empty public constructor
    }

    public static CreateIdentityFragment newInstance() {
        return new CreateIdentityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


}
