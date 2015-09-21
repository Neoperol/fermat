package com.bitdubai.fermat_cry_plugin.layer.asset_vault.developer.bitdubai.version_1.exceptions;

import com.bitdubai.fermat_api.FermatException;

/**
 * Created by rodrigo on 9/19/15.
 */
public class CantCreateAssetVaultSeed extends FermatException {
    public final static String DEFAULT_MESSAGE = "There was an error trying to create the Asset Vault Seed.";

    public CantCreateAssetVaultSeed(String message, Exception cause, String context, String possibleReason) {
        super(message, cause, context, possibleReason);
    }
}
