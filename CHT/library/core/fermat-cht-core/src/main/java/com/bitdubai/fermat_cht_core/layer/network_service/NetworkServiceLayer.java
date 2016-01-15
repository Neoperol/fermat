package com.bitdubai.fermat_cht_core.layer.network_service;

import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_cht_core.layer.network_service.chat.ChatNetworkServicePluginSubsystem;
import com.bitdubai.fermat_core_api.layer.all_definition.system.abstract_classes.AbstractLayer;
import com.bitdubai.fermat_core_api.layer.all_definition.system.exceptions.CantRegisterPluginException;
import com.bitdubai.fermat_core_api.layer.all_definition.system.exceptions.CantStartLayerException;

/**
 * Created by Gabriel Araujo (gabe_512@hotmail.com) on 05/01/2016.
 */
public class NetworkServiceLayer extends AbstractLayer {

    public NetworkServiceLayer() {
        super(Layers.NETWORK_SERVICE);
    }

    public void start() throws CantStartLayerException {

        try {

            registerPlugin(new ChatNetworkServicePluginSubsystem());

        } catch (CantRegisterPluginException e) {

            throw new CantStartLayerException(
                    e,
                    "",
                    "Problem trying to register a plugin."
            );
        }
    }

}
