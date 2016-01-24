package com.bitdubai.fermat_dap_android_wallet_asset_user_bitdubai.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatEditText;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatWorkerCallBack;
import com.bitdubai.fermat_android_api.ui.util.BitmapWorkerTask;
import com.bitdubai.fermat_android_api.ui.util.FermatWorker;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_dap_android_wallet_asset_user_bitdubai.R;
import com.bitdubai.fermat_dap_android_wallet_asset_user_bitdubai.dialogs.RedeemAcceptDialog;
import com.bitdubai.fermat_dap_android_wallet_asset_user_bitdubai.models.Data;
import com.bitdubai.fermat_dap_android_wallet_asset_user_bitdubai.models.DigitalAsset;
import com.bitdubai.fermat_dap_android_wallet_asset_user_bitdubai.models.RedeemPoint;
import com.bitdubai.fermat_dap_android_wallet_asset_user_bitdubai.sessions.AssetUserSession;
import com.bitdubai.fermat_dap_api.layer.dap_module.wallet_asset_user.interfaces.AssetUserWalletSubAppModuleManager;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantLoadWalletException;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by frank on 12/15/15.
 */
public class AssetRedeemFragment extends AbstractFermatFragment {

    private Activity activity;

    private AssetUserSession assetUserSession;
    private AssetUserWalletSubAppModuleManager moduleManager;

    private View rootView;
    private Toolbar toolbar;
    private Resources res;
    private ImageView assetRedeemImage;
    private FermatTextView assetRedeemNameText;
    private FermatTextView assetRedeemRemainingText;
    private FermatTextView selectedRPText;
    private FermatEditText assetsToRedeemEditText;
    private View selectRPButton;
    private View redeemAssetsButton;

    private DigitalAsset digitalAsset;

    int selectedRPCount;

    public AssetRedeemFragment() {

    }

    public static AssetRedeemFragment newInstance() {
        return new AssetRedeemFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assetUserSession = (AssetUserSession) appSession;
        moduleManager = assetUserSession.getModuleManager();
        activity = getActivity();

        configureToolbar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dap_wallet_asset_user_asset_redeem, container, false);
        res = rootView.getResources();

        setupUI();
        setupUIData();

        return rootView;
    }

    private void setupUI() {
        setupBackgroundBitmap();

        assetRedeemImage = (ImageView) rootView.findViewById(R.id.assetRedeemImage);
        assetRedeemNameText = (FermatTextView) rootView.findViewById(R.id.assetRedeemNameText);
        assetRedeemRemainingText = (FermatTextView) rootView.findViewById(R.id.assetRedeemRemainingText);
        assetsToRedeemEditText = (FermatEditText) rootView.findViewById(R.id.assetsToRedeemEditText);
        selectedRPText = (FermatTextView) rootView.findViewById(R.id.selectedRedeemPointsText);
        selectRPButton = rootView.findViewById(R.id.selectRedeemPointsButton);
        redeemAssetsButton = rootView.findViewById(R.id.redeemAssetsButton);

//        layout = rootView.findViewById(R.id.assetDetailRemainingLayout);
        redeemAssetsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (selectedRPCount > 0) {
                    Object x = appSession.getData("redeem_points");
                    if (x != null) {
                        final List<RedeemPoint> redeemPoints = (List<RedeemPoint>) x;
                        if (redeemPoints.size() > 0) {
                            RedeemAcceptDialog dialog = new RedeemAcceptDialog(getActivity(), (AssetUserSession) appSession, appResourcesProviderManager);
                            dialog.setYesBtnListener(new RedeemAcceptDialog.OnClickAcceptListener() {
                                @Override
                                public void onClick() {
                                    doRedeem(digitalAsset.getAssetPublicKey(), redeemPoints);
                                }
                            });
                            dialog.show();
                        }
                    }
                } else {
                    Toast.makeText(activity, "No redeem points selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        selectRPButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                appSession.setData("asset_data", data);
                changeActivity(Activities.DAP_WALLET_ASSET_USER_ASSET_REDEEM_SELECT_REDEEMPOINTS, appSession.getAppPublicKey());
            }
        });

        selectedRPCount = getRedeemPointsSelectedCount();
        String message = (selectedRPCount == 0) ? "Select redeem points" : selectedRPCount + " redeem points selected";
        selectedRPText.setText(message);
    }

    private void setupBackgroundBitmap() {
        AsyncTask<Void, Void, Bitmap> asyncTask = new AsyncTask<Void, Void, Bitmap>() {

            WeakReference<ViewGroup> view;

            @Override
            protected void onPreExecute() {
                view = new WeakReference(rootView) ;
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap drawable = null;
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = true;
                    options.inSampleSize = 5;
                    drawable = BitmapFactory.decodeResource(
                            getResources(), R.drawable.bg_app_image_user,options);
                }catch (OutOfMemoryError error){
                    error.printStackTrace();
                }
                return drawable;
            }

            @Override
            protected void onPostExecute(Bitmap drawable) {
                if (drawable!= null) {
                    view.get().setBackground(new BitmapDrawable(getResources(),drawable));
                }
            }
        } ;
        asyncTask.execute();
    }

    private int getRedeemPointsSelectedCount() {
        Object x = appSession.getData("redeem_points");
        int count = 0;
        if (x != null) {
            List<RedeemPoint> redeemPoints = (List<RedeemPoint>) x;
            if (redeemPoints.size() > 0) {
                for (RedeemPoint redeemPoint :
                        redeemPoints) {
                    if (redeemPoint.isSelected()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void doRedeem(final String assetPublicKey, final List<RedeemPoint> redeemPoints) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        FermatWorker task = new FermatWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                moduleManager.redeemAssetToRedeemPoint(assetPublicKey, null, Data.getRedeemPoints(redeemPoints));
                return true;
            }
        };

        task.setContext(activity);
        task.setCallBack(new FermatWorkerCallBack() {
            @Override
            public void onPostExecute(Object... result) {
                dialog.dismiss();
                if (activity != null) {
                    Toast.makeText(activity, "Everything ok...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorOccurred(Exception ex) {
                dialog.dismiss();
                if (activity != null)
                    Toast.makeText(activity, "Fermat Has detected an exception",
                            Toast.LENGTH_SHORT).show();
            }
        });
        task.execute();
    }

    private void setupUIData() {
//        digitalAsset = (DigitalAsset) appSession.getData("asset_data");
        String digitalAssetPublicKey = ((DigitalAsset) appSession.getData("asset_data")).getAssetPublicKey();
        try {
            digitalAsset = Data.getDigitalAsset(moduleManager, digitalAssetPublicKey);
        } catch (CantLoadWalletException e) {
            e.printStackTrace();
        }

        toolbar.setTitle(digitalAsset.getName());

//        if (digitalAsset.getImage() != null) {
//            assetRedeemImage.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(digitalAsset.getImage())));
//        } else {
//            assetRedeemImage.setImageDrawable(rootView.getResources().getDrawable(R.drawable.img_asset_without_image));
//        }
        byte[] img = (digitalAsset.getImage() == null) ? new byte[0] : digitalAsset.getImage();
        BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(assetRedeemImage, res, R.drawable.img_asset_without_image, false);
        bitmapWorkerTask.execute(img);

        assetRedeemNameText.setText(digitalAsset.getName());
        assetsToRedeemEditText.setText(digitalAsset.getAvailableBalanceQuantity() + "");
        assetRedeemRemainingText.setText(digitalAsset.getAvailableBalanceQuantity() + " Assets Remaining");
    }

    private void configureToolbar() {
        toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setBackgroundColor(Color.parseColor("#381a5e"));
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setBottom(Color.WHITE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.setStatusBarColor(Color.parseColor("#381a5e"));
            }
        }
    }
}
