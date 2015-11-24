package com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.enums.ConnectionRequestAction;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.enums.ProtocolState;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.enums.RequestType;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantAcceptConnectionRequestException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantCancelConnectionRequestException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantDenyConnectionRequestException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantDisconnectException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantExposeIdentitiesException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantExposeIdentityException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantListPendingConnectionNewsException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantRequestConnectionException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.ConnectionRequestNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.UnexpectedProtocolStateException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.interfaces.CryptoBrokerManager;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.interfaces.CryptoBrokerSearch;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.utils.CryptoBrokerConnectionInformation;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.utils.CryptoBrokerConnectionNew;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.utils.CryptoBrokerExposingData;
import com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.database.ConnectionNewsDao;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.UnexpectedPluginExceptionSeverity;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class <code>com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.structure.CryptoBrokerActorNetworkServiceManager</code>
 * bla bla bla.
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 20/11/2015.
 */
public final class CryptoBrokerActorNetworkServiceManager implements CryptoBrokerManager {

    private final ConnectionNewsDao      connectionNewsDao     ;
    private final ErrorManager           errorManager          ;
    private final PluginVersionReference pluginVersionReference;

    public CryptoBrokerActorNetworkServiceManager(final ConnectionNewsDao      connectionNewsDao     ,
                                                  final ErrorManager           errorManager          ,
                                                  final PluginVersionReference pluginVersionReference) {

        this.connectionNewsDao      = connectionNewsDao     ;
        this.errorManager           = errorManager          ;
        this.pluginVersionReference = pluginVersionReference;
    }

    private ConcurrentHashMap<String, CryptoBrokerExposingData> cryptoBrokersToRegister;

    @Override
    public final void exposeIdentity(final CryptoBrokerExposingData cryptoBroker) throws CantExposeIdentityException {

        if (cryptoBrokersToRegister == null)
            cryptoBrokersToRegister = new ConcurrentHashMap<>();

        cryptoBrokersToRegister.putIfAbsent(cryptoBroker.getPublicKey(), cryptoBroker);
    }

    @Override
    public final void exposeIdentities(final List<CryptoBrokerExposingData> cryptoBrokerExposingDataList) throws CantExposeIdentitiesException {

        if (cryptoBrokersToRegister == null)
            cryptoBrokersToRegister = new ConcurrentHashMap<>();

        for(final CryptoBrokerExposingData cryptoBroker : cryptoBrokerExposingDataList)
            cryptoBrokersToRegister.putIfAbsent(cryptoBroker.getPublicKey(), cryptoBroker);
    }

    public final ConcurrentHashMap<String, CryptoBrokerExposingData> getCryptoBrokersToRegister() {
        return cryptoBrokersToRegister;
    }

    public final void removeCryptoBrokerToRegister(final String publicKey) {

        cryptoBrokersToRegister.remove(publicKey);
    }

    @Override
    public final CryptoBrokerSearch getSearch() {
        return null;
    }

    /**
     * I indicate to the Agent the action that it must take:
     * - Protocol State: PROCESSING_SEND.
     * - Action        : REQUEST.
     * - Type          : SENT.
     */
    @Override
    public final void requestConnection(final CryptoBrokerConnectionInformation brokerInformation) throws CantRequestConnectionException {

        try {

            final UUID newId = UUID.randomUUID();

            final ProtocolState           state  = ProtocolState          .PROCESSING_SEND;
            final RequestType             type   = RequestType            .SENT           ;
            final ConnectionRequestAction action = ConnectionRequestAction.REQUEST        ;

            connectionNewsDao.createConnectionRequest(
                    newId            ,
                    brokerInformation,
                    state            ,
                    type             ,
                    action
            );

        } catch (final CantRequestConnectionException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantRequestConnectionException(e, null, "Unhandled Exception.");
        }
    }

    /**
     * I indicate to the Agent the action that it must take:
     * - Protocol State: PROCESSING_SEND.
     * - Action        : DISCONNECT.
     * - Type          : SENT.
     */
    @Override
    public final void disconnect(final String identityPublicKey,
                                 final Actors identityActorType,
                                 final String brokerPublicKey  ) throws CantDisconnectException {

        try {

            final UUID newId    = UUID.randomUUID();
            final long sentTime = System.currentTimeMillis();

            final ProtocolState           state  = ProtocolState          .PROCESSING_SEND;
            final RequestType             type   = RequestType            .SENT           ;
            final ConnectionRequestAction action = ConnectionRequestAction.DISCONNECT     ;

            connectionNewsDao.createDisconnectionRequest(
                    newId            ,
                    identityPublicKey,
                    identityActorType,
                    brokerPublicKey  ,
                    sentTime         ,
                    state            ,
                    type             ,
                    action
            );

        } catch (final CantDisconnectException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantDisconnectException(e, null, "Unhandled Exception.");
        }
    }

    /**
     * I update the record with the new address an then, i indicate the ns agent the action that it must take:
     * - Action        : DENY.
     * - Protocol State: PROCESSING_SEND.
     */
    @Override
    public final void denyConnection(final UUID requestId) throws CantDenyConnectionRequestException ,
                                                                  ConnectionRequestNotFoundException {

        try {

            final ProtocolState protocolState = ProtocolState.PROCESSING_SEND;

            connectionNewsDao.denyConnection(
                    requestId,
                    protocolState
            );

        } catch (CantDenyConnectionRequestException | ConnectionRequestNotFoundException e){
            // ConnectionRequestNotFoundException - THIS SHOULD NOT HAPPEN.
            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantDenyConnectionRequestException(e, null, "Unhandled Exception.");
        }
    }

    /**
     * I update the record with the new address an then, i indicate the ns agent the action that it must take:
     * - Action        : CANCEL.
     * - Protocol State: PROCESSING_SEND.
     *
     * We must to validate if the record is in PENDING_REMOTE_ACTION.
     */
    @Override
    public final void cancelConnection(final UUID requestId) throws CantCancelConnectionRequestException,
                                                                    ConnectionRequestNotFoundException  ,
                                                                    UnexpectedProtocolStateException    {

    }

    /**
     * I update the record with the new address an then, i indicate the ns agent the action that it must take:
     * - Action        : ACCEPT.
     * - Protocol State: PROCESSING_SEND.
     */
    @Override
    public final void acceptConnection(final UUID requestId) throws CantAcceptConnectionRequestException,
                                                                    ConnectionRequestNotFoundException  {

        try {

            final ProtocolState protocolState = ProtocolState.PROCESSING_SEND;

            connectionNewsDao.acceptConnection(
                    requestId,
                    protocolState
            );

        } catch (CantAcceptConnectionRequestException | ConnectionRequestNotFoundException e){
            // ConnectionRequestNotFoundException - THIS SHOULD NOT HAPPEN.
            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantAcceptConnectionRequestException(e, null, "Unhandled Exception.");
        }
    }

    /**
     * we'll return all the requests pending a local action.
     * State : PENDING_LOCAL_ACTION.
     *
     * @throws CantListPendingConnectionNewsException  if something goes wrong.
     */
    @Override
    public final List<CryptoBrokerConnectionNew> listPendingConnectionNews() throws CantListPendingConnectionNewsException {

        try {

            return connectionNewsDao.listAllPendingRequests();

        } catch (final CantListPendingConnectionNewsException e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e){

            errorManager.reportUnexpectedPluginException(this.pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListPendingConnectionNewsException(e, null, "Unhandled Exception.");
        }
    }
}
