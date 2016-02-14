package com.bitdubai.fermat_cbp_api.all_definition.wallet;

import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.world.interfaces.Currency;
import com.bitdubai.fermat_cbp_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.MoneyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetCryptoBrokerMarketRateException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetCryptoBrokerQuoteException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetCryptoBrokerStockTransactionException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetCryptoBrokerWalletSettingException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetStockCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetTransactionCryptoBrokerWalletMatchingException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerStockTransaction;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.FiatIndex;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.Quote;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.setting.CryptoBrokerWalletSetting;

import java.util.List;
import java.util.UUID;

/**
 * Created by jorge on 30-09-2015.
 * Modified by Franklin Marcano 01.12.2015
 */
public interface Wallet {
    /**
     * This method load the instance the StockBalance
     * @return StockBalance
     * @exception CantGetStockCryptoBrokerWalletException
     */
    StockBalance getStockBalance()  throws CantGetStockCryptoBrokerWalletException;
    /**
     * This method load the instance the CryptoBrokerWalletSetting
     * @return StockBalance
     * @exception CantGetCryptoBrokerWalletSettingException
     */
    CryptoBrokerWalletSetting getCryptoWalletSetting() throws CantGetCryptoBrokerWalletSettingException;
    /**
     * This method load the list CryptoBrokerStockTransaction
     * @param merchandise
     * @param moneyType
     * @param transactionType
     * @param balanceType
     * @return List<CryptoBrokerStockTransaction>
     * @exception CantGetCryptoBrokerStockTransactionException
     */
    List<CryptoBrokerStockTransaction> getCryptoBrokerStockTransactionsByMerchandise(Currency merchandise, MoneyType moneyType, TransactionType transactionType, BalanceType balanceType) throws CantGetCryptoBrokerStockTransactionException;

    /**
     * This method load the list CryptoBrokerStockTransaction
     * @param merchandise
     * @param fiatCurrency
     * @param moneyType
     * @return FiatIndex
     * @exception CantGetCryptoBrokerMarketRateException
     */
    FiatIndex getMarketRate(Currency merchandise, FiatCurrency fiatCurrency, MoneyType moneyType) throws CantGetCryptoBrokerMarketRateException;

    /**
     * This method load the list CryptoBrokerStockTransaction
     * @param merchandise
     * @param quantity
     * @param payment
     * @return Quote
     * @exception CantGetCryptoBrokerQuoteException
     */
    Quote getQuote(Currency merchandise, float quantity, Currency payment) throws CantGetCryptoBrokerQuoteException;

    /**
     * This method load the list CryptoBrokerStockTransaction
     * @return void
     * @exception CantGetTransactionCryptoBrokerWalletMatchingException
     */
    void markAsSeen(UUID transactionId) throws CantGetTransactionCryptoBrokerWalletMatchingException;
}