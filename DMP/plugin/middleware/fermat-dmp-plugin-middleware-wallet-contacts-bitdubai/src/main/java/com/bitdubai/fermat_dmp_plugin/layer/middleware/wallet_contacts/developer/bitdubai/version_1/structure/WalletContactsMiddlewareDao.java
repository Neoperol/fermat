package com.bitdubai.fermat_dmp_plugin.layer.middleware.wallet_contacts.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.dmp_middleware.wallet_contacts.interfaces.WalletContactRecord;
import com.bitdubai.fermat_api.layer.dmp_middleware.wallet_contacts.exceptions.CantCreateWalletContactException;
import com.bitdubai.fermat_api.layer.dmp_middleware.wallet_contacts.exceptions.CantDeleteWalletContactException;
import com.bitdubai.fermat_api.layer.dmp_middleware.wallet_contacts.exceptions.CantGetAllWalletContactsException;
import com.bitdubai.fermat_api.layer.dmp_middleware.wallet_contacts.exceptions.CantGetWalletContactException;
import com.bitdubai.fermat_api.layer.dmp_middleware.wallet_contacts.exceptions.CantUpdateWalletContactException;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTransaction;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DealsWithPluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.pip_platform_service.error_manager.DealsWithErrors;
import com.bitdubai.fermat_api.layer.pip_platform_service.error_manager.ErrorManager;
import com.bitdubai.fermat_api.layer.pip_platform_service.error_manager.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_dmp_plugin.layer.middleware.wallet_contacts.developer.bitdubai.version_1.exceptions.CantInitializeCryptoWalletContactsDatabaseException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The Class <code>com.bitdubai.fermat_dmp_plugin.layer.middleware.wallet_contacts.developer.bitdubai.version_1.structure.WalletContactsMiddlewareDao</code> have
 * all methods implementation to access the data base (CRUD)
 * <p/>
 *
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 05/06/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class WalletContactsMiddlewareDao implements DealsWithErrors, DealsWithPluginDatabaseSystem {

    /**
     * Represent the Error manager.
     */
    private ErrorManager errorManager;

    /**
     *  Represent the Plugin Database.
     */
    private PluginDatabaseSystem pluginDatabaseSystem;

    /**
     * Represent de Database where i will be working with
     */
    Database database;


    /**
     * Constructor with parameters
     *
     * @param errorManager DealsWithErrors
     * @param pluginDatabaseSystem DealsWithPluginDatabaseSystem
     */
    public WalletContactsMiddlewareDao(ErrorManager errorManager, PluginDatabaseSystem pluginDatabaseSystem) {
        this.errorManager = errorManager;
        this.pluginDatabaseSystem = pluginDatabaseSystem;
    }

    /**
     * This method open or creates the database i'll be working with
     *
     * @param ownerId plugin id
     * @param databaseName database name
     * @throws CantInitializeCryptoWalletContactsDatabaseException
     */
    public void initializeDatabase(UUID ownerId, String databaseName) throws CantInitializeCryptoWalletContactsDatabaseException {
        try {

             /*
              * Open new database connection
              */
            database = this.pluginDatabaseSystem.openDatabase(ownerId, databaseName);

        } catch (CantOpenDatabaseException cantOpenDatabaseException) {

             /*
              * The database exists but cannot be open. I can not handle this situation.
              */
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_WALLET_CONTACTS_MIDDLEWARE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, cantOpenDatabaseException);
            throw new CantInitializeCryptoWalletContactsDatabaseException(cantOpenDatabaseException.getMessage());

        } catch (DatabaseNotFoundException e) {

             /*
              * The database no exist may be the first time the plugin is running on this device,
              * We need to create the new database
              */
            WalletContactsMiddlewareDatabaseFactory walletContactsDatabaseFactory = new WalletContactsMiddlewareDatabaseFactory(pluginDatabaseSystem);

            try {
                  /*
                   * We create the new database
                   */
                database = walletContactsDatabaseFactory.createDatabase(ownerId, databaseName);
            } catch (CantCreateDatabaseException cantCreateDatabaseException) {
                  /*
                   * The database cannot be created. I can not handle this situation.
                   */
                errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_WALLET_CONTACTS_MIDDLEWARE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, cantCreateDatabaseException);
                throw new CantInitializeCryptoWalletContactsDatabaseException(cantCreateDatabaseException.getMessage());
            }
        }
    }

    /**
     * Method that list the all entities on the database.
     *
     *  @return All Wallet Contacts.
     */
    public List<WalletContactRecord> findAll(UUID walletId) throws CantGetAllWalletContactsException {

        List<WalletContactRecord> walletContactsListRecord = new ArrayList<>();

        try {

            DatabaseTable walletContactAddressBookTable = database.getTable(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_NAME);
            walletContactAddressBookTable.setUUIDFilter(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_WALLET_ID_COLUMN_NAME, walletId, DatabaseFilterType.EQUAL);
            walletContactAddressBookTable.loadToMemory();

            List<DatabaseTableRecord> records = walletContactAddressBookTable.getRecords();

            for (DatabaseTableRecord record : records){

                UUID contactId = record.getUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_CONTACT_ID_COLUMN_NAME);
                UUID actorId = record.getUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_ID_COLUMN_NAME);
                String actorName = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_NAME_COLUMN_NAME);
                Actors actorType = Actors.getByCode(record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_TYPE_COLUMN_NAME));

                String receivedAddress = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_CRYPTO_ADDRESS_COLUMN_NAME);
                String receivedCurrency = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_ADDRESS_CRYPTO_CURRENCY_COLUMN_NAME);
                CryptoAddress receivedCryptoAddress = new CryptoAddress(receivedAddress, CryptoCurrency.getByCode(receivedCurrency));

                WalletContactsMiddlewareRecord walletContact = new WalletContactsMiddlewareRecord(actorId, actorName, actorType, contactId, receivedCryptoAddress, walletId);

                walletContactsListRecord.add(walletContact);
            }

        } catch (Exception e) {
            // Register the failure.
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_WALLET_CONTACTS_MIDDLEWARE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetAllWalletContactsException(e.getMessage());
        }

        return walletContactsListRecord;
    }

    /**
     * Method that list the all entities on the database.
     *
     *  @return All Wallet Contacts.
     */
    public List<WalletContactRecord> findAllScrolling(UUID walletId, Integer max, Integer offset) throws CantGetAllWalletContactsException {

        List<WalletContactRecord> walletContactsListRecord = new ArrayList<>();

        try {

            DatabaseTable walletContactAddressBookTable = database.getTable(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_NAME);
            walletContactAddressBookTable.setUUIDFilter(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_WALLET_ID_COLUMN_NAME, walletId, DatabaseFilterType.EQUAL);
            walletContactAddressBookTable.setFilterTop(max.toString());
            // TODO: WHEN OFFSET IS IMPLEMENTED UNCOMMENT NEXT LINE
            //walletContactAddressBookTable.setFilterOffset(offset.toString());
            walletContactAddressBookTable.loadToMemory();

            List<DatabaseTableRecord> records = walletContactAddressBookTable.getRecords();

            for (DatabaseTableRecord record : records){

                UUID contactId = record.getUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_CONTACT_ID_COLUMN_NAME);
                UUID actorId = record.getUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_ID_COLUMN_NAME);
                String actorName = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_NAME_COLUMN_NAME);
                Actors actorType = Actors.getByCode(record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_TYPE_COLUMN_NAME));

                String receivedAddress = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_CRYPTO_ADDRESS_COLUMN_NAME);
                String receivedCurrency = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_ADDRESS_CRYPTO_CURRENCY_COLUMN_NAME);
                CryptoAddress receivedCryptoAddress = new CryptoAddress();
                receivedCryptoAddress.setAddress(receivedAddress);
                receivedCryptoAddress.setCryptoCurrency(CryptoCurrency.getByCode(receivedCurrency));

                WalletContactsMiddlewareRecord walletContact = new WalletContactsMiddlewareRecord(actorId, actorName, actorType, contactId, receivedCryptoAddress, walletId);

                walletContactsListRecord.add(walletContact);
            }

        } catch (Exception e) {
            // Register the failure.
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_WALLET_CONTACTS_MIDDLEWARE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetAllWalletContactsException(e.getMessage());
        }

        return walletContactsListRecord;
    }

    /**
     * Method that create a new entity in the data base.
     *
     *  @param walletContactRecord WalletContactRecord to create.
     */
    public void create(WalletContactRecord walletContactRecord) throws CantCreateWalletContactException {

        if (walletContactRecord == null){
            throw new CantCreateWalletContactException("The entity is required, can not be null");
        }

        try {
            DatabaseTable walletContactAddressBookTable = database.getTable(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_NAME);
            DatabaseTableRecord entityRecord = walletContactAddressBookTable.getEmptyRecord();

            entityRecord.setUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_CONTACT_ID_COLUMN_NAME, walletContactRecord.getContactId());
            entityRecord.setUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_WALLET_ID_COLUMN_NAME, walletContactRecord.getWalletId());
            entityRecord.setUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_ID_COLUMN_NAME, walletContactRecord.getActorId());
            entityRecord.setStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_NAME_COLUMN_NAME, walletContactRecord.getActorName());
            entityRecord.setStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_TYPE_COLUMN_NAME, walletContactRecord.getActorType().getCode());

            if (walletContactRecord.getReceivedCryptoAddress() != null) {
                entityRecord.setStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_CRYPTO_ADDRESS_COLUMN_NAME, walletContactRecord.getReceivedCryptoAddress().getAddress());
                entityRecord.setStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_ADDRESS_CRYPTO_CURRENCY_COLUMN_NAME, walletContactRecord.getReceivedCryptoAddress().getCryptoCurrency().getCode());
            }

            DatabaseTransaction transaction = database.newTransaction();
            transaction.addRecordToInsert(walletContactAddressBookTable, entityRecord);
            database.executeTransaction(transaction);

        } catch (Exception e) {
            // Register the failure.
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_WALLET_CONTACTS_MIDDLEWARE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantCreateWalletContactException(e.getMessage());
        }
    }

    /**
     * Method that update a wallet contact in the database.
     *
     *  @param walletContactRecord WalletContactRecord to update.
     */
    public void update(WalletContactRecord walletContactRecord) throws CantUpdateWalletContactException {

        if (walletContactRecord == null){
            throw new CantUpdateWalletContactException("The entity is required, can not be null");
        }

        try {
            DatabaseTable walletContactAddressBookTable = database.getTable(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_NAME);
            walletContactAddressBookTable.setUUIDFilter(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_CONTACT_ID_COLUMN_NAME, walletContactRecord.getContactId(), DatabaseFilterType.EQUAL);
            walletContactAddressBookTable.loadToMemory();

            DatabaseTableRecord record = walletContactAddressBookTable.getRecords().get(0);

            if (walletContactRecord.getActorName() != null)
                record.setStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_NAME_COLUMN_NAME, walletContactRecord.getActorName());
            if (walletContactRecord.getReceivedCryptoAddress() != null) {
                record.setStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_CRYPTO_ADDRESS_COLUMN_NAME, walletContactRecord.getReceivedCryptoAddress().getAddress());
                record.setStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_ADDRESS_CRYPTO_CURRENCY_COLUMN_NAME, walletContactRecord.getReceivedCryptoAddress().getCryptoCurrency().getCode());
            }

            DatabaseTransaction transaction = database.newTransaction();
            transaction.addRecordToUpdate(walletContactAddressBookTable, record);
            database.executeTransaction(transaction);

        } catch (Exception e) {
            // Register the failure.
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_WALLET_CONTACTS_MIDDLEWARE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantUpdateWalletContactException(e.getMessage());
        }
    }

    /**
     * Method that delete a entity in the database.
     *
     *  @param contactId UUID contactId.
     * */
    public void delete(UUID contactId) throws CantDeleteWalletContactException {

        if (contactId == null){
            throw new CantDeleteWalletContactException("The id is required can not be null");
        }

        try {
            DatabaseTable walletContactAddressBookTable = database.getTable(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_NAME);
            walletContactAddressBookTable.setStringFilter(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_CONTACT_ID_COLUMN_NAME, contactId.toString(), DatabaseFilterType.EQUAL);
            walletContactAddressBookTable.loadToMemory();
            //DatabaseTableRecord record = walletContactAddressBookTable.getRecords().get(0);

            DatabaseTransaction transaction = database.newTransaction();

            //TODO configure delete method in transaction AND UNCOMMENT THIS
            //transaction.addRecordToDelete(walletContactAddressBookTable, record);

            database.executeTransaction(transaction);

        } catch (Exception e) {
            // Register the failure.
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_WALLET_CONTACTS_MIDDLEWARE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantDeleteWalletContactException(e.getMessage());
        }
    }

    /**
     * Method that find a WalletContactRecord in database by actorName and walletId.
     *
     *  @param actorName String actorName.
     *  @param walletId UUID wallet id
     * */
    public WalletContactRecord findByNameAndWalletId(String actorName, UUID walletId) throws CantGetWalletContactException {

        WalletContactRecord walletContactRecord;

        if (actorName == null || walletId == null) {
            throw new CantGetWalletContactException("The name and the actorName is required, can not be null");
        }

        try {
            DatabaseTable walletContactAddressBookTable = database.getTable(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_NAME);
            walletContactAddressBookTable.setStringFilter(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_NAME_COLUMN_NAME, actorName, DatabaseFilterType.EQUAL);
            walletContactAddressBookTable.setUUIDFilter(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_WALLET_ID_COLUMN_NAME, walletId, DatabaseFilterType.EQUAL);
            walletContactAddressBookTable.loadToMemory();
            DatabaseTableRecord record = walletContactAddressBookTable.getRecords().get(0);

            UUID contactId = record.getUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_CONTACT_ID_COLUMN_NAME);
            UUID actorId = record.getUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_ID_COLUMN_NAME);
            Actors actorType = Actors.getByCode(record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_TYPE_COLUMN_NAME));

            String receivedAddress = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_CRYPTO_ADDRESS_COLUMN_NAME);
            String receivedCurrency = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_ADDRESS_CRYPTO_CURRENCY_COLUMN_NAME);
            CryptoAddress receivedCryptoAddress = new CryptoAddress();
            receivedCryptoAddress.setAddress(receivedAddress);
            receivedCryptoAddress.setCryptoCurrency(CryptoCurrency.getByCode(receivedCurrency));

            walletContactRecord = new WalletContactsMiddlewareRecord(actorId, actorName, actorType, contactId, receivedCryptoAddress, walletId);
        } catch (Exception e) {
            // Register the failure.
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_WALLET_CONTACTS_MIDDLEWARE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetWalletContactException(e.getMessage());
        }
        return walletContactRecord;
    }

    /**
     * Method that find a WalletContactRecord in database by actorName contains (string) and walletId.
     *
     *  @param actorName String actorName.
     *  @param walletId UUID wallet id
     * */
    public WalletContactRecord findByNameContainsAndWalletId(String actorName, UUID walletId) throws CantGetWalletContactException {

        WalletContactRecord walletContactRecord;

        if (actorName == null || walletId == null) {
            throw new CantGetWalletContactException("The name and the actorName is required, can not be null");
        }

        try {
            DatabaseTable walletContactAddressBookTable = database.getTable(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_NAME);
            walletContactAddressBookTable.setStringFilter(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_NAME_COLUMN_NAME, actorName, DatabaseFilterType.LIKE);
            walletContactAddressBookTable.setUUIDFilter(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_WALLET_ID_COLUMN_NAME, walletId, DatabaseFilterType.EQUAL);
            walletContactAddressBookTable.loadToMemory();
            DatabaseTableRecord record = walletContactAddressBookTable.getRecords().get(0);

            UUID contactId = record.getUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_CONTACT_ID_COLUMN_NAME);
            UUID actorId = record.getUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_ID_COLUMN_NAME);
            Actors actorType = Actors.getByCode(record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_TYPE_COLUMN_NAME));

            String receivedAddress = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_CRYPTO_ADDRESS_COLUMN_NAME);
            String receivedCurrency = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_ADDRESS_CRYPTO_CURRENCY_COLUMN_NAME);
            CryptoAddress receivedCryptoAddress = new CryptoAddress(receivedAddress, CryptoCurrency.getByCode(receivedCurrency));

            walletContactRecord = new WalletContactsMiddlewareRecord(actorId, actorName, actorType, contactId, receivedCryptoAddress, walletId);
        } catch (Exception e) {
            // Register the failure.
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_WALLET_CONTACTS_MIDDLEWARE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetWalletContactException(e.getMessage());
        }
        return walletContactRecord;
    }

    /**
     * Method that find a WalletContactRecord in database by contact id.
     *
     *  @param contactId UUID contact id
     * */
    public WalletContactRecord findById(UUID contactId) throws CantGetWalletContactException {

        WalletContactRecord walletContactRecord;

        if (contactId == null) {
            throw new CantGetWalletContactException("The name and the contactId is required, can not be null");
        }

        try {
            DatabaseTable walletContactAddressBookTable = database.getTable(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_NAME);
            walletContactAddressBookTable.setUUIDFilter(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_CONTACT_ID_COLUMN_NAME, contactId, DatabaseFilterType.EQUAL);
            walletContactAddressBookTable.loadToMemory();
            DatabaseTableRecord record = walletContactAddressBookTable.getRecords().get(0);

            UUID walletId = record.getUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_WALLET_ID_COLUMN_NAME);
            UUID actorId = record.getUUIDValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_ID_COLUMN_NAME);
            String actorName = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_NAME_COLUMN_NAME);
            Actors actorType = Actors.getByCode(record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_ACTOR_TYPE_COLUMN_NAME));

            String receivedAddress = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_CRYPTO_ADDRESS_COLUMN_NAME);
            String receivedCurrency = record.getStringValue(WalletContactsMiddlewareDatabaseConstants.CRYPTO_WALLET_CONTACTS_ADDRESS_BOOK_TABLE_RECEIVED_ADDRESS_CRYPTO_CURRENCY_COLUMN_NAME);
            CryptoAddress receivedCryptoAddress = new CryptoAddress(receivedAddress, CryptoCurrency.getByCode(receivedCurrency));

            walletContactRecord = new WalletContactsMiddlewareRecord(actorId, actorName, actorType, contactId, receivedCryptoAddress, walletId);
        } catch (Exception e) {
            // Register the failure.
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_WALLET_CONTACTS_MIDDLEWARE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetWalletContactException(e.getMessage());
        }
        return walletContactRecord;
    }

    /**
     * DealsWithPluginDatabaseSystem Interface implementation.
     */
    @Override
    public void setErrorManager(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }

    /**
     * DealsWithPluginDatabaseSystem Interface implementation.
     */
    @Override
    public void setPluginDatabaseSystem(PluginDatabaseSystem pluginDatabaseSystem) {
        this.pluginDatabaseSystem = pluginDatabaseSystem;
    }
}