package com.bitdubai.fermat_cbp_plugin.layer.wallet_module.crypto_customer.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_cbp_api.all_definition.enums.ClauseStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ClauseType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationStatus;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.CantCreateMessageSignatureException;
import com.bitdubai.fermat_cbp_api.all_definition.identity.ActorIdentity;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation.Clause;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.interfaces.CustomerBrokerPurchaseNegotiation;
import com.bitdubai.fermat_cbp_api.layer.negotiation.exceptions.CantGetListClauseException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantGetNegotiationsWaitingForCustomerException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.ClauseInformation;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.CustomerBrokerNegotiationInformation;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Customer and Broker Negotiation Information
 *
 * @author Nelson Ramirez
 * @version 1.0
 * @since 05/11/15.
 */
public class CryptoCustomerWalletModuleCustomerBrokerNegotiationInformation implements CustomerBrokerNegotiationInformation {

    // -- for test purposes
    private static final Random random = new Random(321515131);
    private static final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();
    private static final Calendar calendar = Calendar.getInstance();
    // for test purposes --

    private ActorIdentity customerIdentity;
    private ActorIdentity brokerIdentity;
    private Map<ClauseType, String> summary;
    private Map<ClauseType, ClauseInformation> clauses;
    private Collection<Clause> negotiationClause;
    private NegotiationStatus status;
    private long date;


    public CryptoCustomerWalletModuleCustomerBrokerNegotiationInformation(CustomerBrokerPurchaseNegotiation negotiation) {
        //TEST
        this.customerIdentity = new CryptoCustomerWalletModuleActorIdentityImpl("CustomerAlias", new byte[0]);
        this.brokerIdentity = new CryptoCustomerWalletModuleActorIdentityImpl(negotiation.getBrokerPublicKey(), new byte[0]);

        String currencyQty = decimalFormat.format(random.nextFloat() * 100);
        String exchangeRate = decimalFormat.format(random.nextFloat());

        summary = new HashMap<>();
        summary.put(ClauseType.CUSTOMER_CURRENCY_QUANTITY, currencyQty);
        summary.put(ClauseType.CUSTOMER_CURRENCY, "merchandise");
        summary.put(ClauseType.EXCHANGE_RATE, exchangeRate);
        summary.put(ClauseType.BROKER_CURRENCY, "paymentCurrency");
        summary.put(ClauseType.BROKER_PAYMENT_METHOD, "paymentMethod");

        this.status = status;
        date = calendar.getTimeInMillis();

        clauses = new HashMap<>();
        clauses.put(ClauseType.CUSTOMER_CURRENCY_QUANTITY, new CryptoCustomerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY_QUANTITY, currencyQty, ClauseStatus.DRAFT));
        clauses.put(ClauseType.CUSTOMER_CURRENCY, new CryptoCustomerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY, "merchandise", ClauseStatus.DRAFT));
        clauses.put(ClauseType.BROKER_BANK_ACCOUNT, new CryptoCustomerWalletModuleClauseInformation(ClauseType.BROKER_BANK_ACCOUNT, "Banesco\n2165645454654", ClauseStatus.DRAFT));
        clauses.put(ClauseType.BROKER_CURRENCY, new CryptoCustomerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY, "paymentCurrency", ClauseStatus.DRAFT));
        clauses.put(ClauseType.BROKER_PAYMENT_METHOD, new CryptoCustomerWalletModuleClauseInformation(ClauseType.BROKER_PAYMENT_METHOD, "paymentMethod", ClauseStatus.DRAFT));
        clauses.put(ClauseType.EXCHANGE_RATE, new CryptoCustomerWalletModuleClauseInformation(ClauseType.EXCHANGE_RATE, exchangeRate, ClauseStatus.DRAFT));
        clauses.put(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER, new CryptoCustomerWalletModuleClauseInformation(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER, "18-11-2015", ClauseStatus.DRAFT));
        clauses.put(ClauseType.BROKER_DATE_TIME_TO_DELIVER, new CryptoCustomerWalletModuleClauseInformation(ClauseType.BROKER_DATE_TIME_TO_DELIVER, "20-11-2015", ClauseStatus.DRAFT));
    }

    public CryptoCustomerWalletModuleCustomerBrokerNegotiationInformation(String customerAlias, String brokerAlias, NegotiationStatus status, Map<ClauseType, ClauseInformation> clauses) {

        this.customerIdentity = new CryptoCustomerWalletModuleActorIdentityImpl(customerAlias, new byte[0]);
        this.brokerIdentity = new CryptoCustomerWalletModuleActorIdentityImpl(brokerAlias, new byte[0]);

        String currencyQty = decimalFormat.format(clauses.get(ClauseType.CUSTOMER_CURRENCY_QUANTITY).getValue());
        String exchangeRate = decimalFormat.format(clauses.get(ClauseType.EXCHANGE_RATE).getValue());
        String merchandise = "";
        String paymentMethod = "";
        String paymentCurrency = "";

        if(clauses.get(ClauseType.CUSTOMER_CURRENCY) != null)
            merchandise = clauses.get(ClauseType.CUSTOMER_CURRENCY).getValue();

        if(clauses.get(ClauseType.BROKER_PAYMENT_METHOD) != null)
            paymentMethod = clauses.get(ClauseType.BROKER_PAYMENT_METHOD).getValue();

        if(clauses.get(ClauseType.BROKER_CURRENCY) != null)
            paymentCurrency = clauses.get(ClauseType.BROKER_CURRENCY).getValue();

        summary = new HashMap<>();
        summary.put(ClauseType.CUSTOMER_CURRENCY_QUANTITY, currencyQty);
        summary.put(ClauseType.CUSTOMER_CURRENCY, merchandise);
        summary.put(ClauseType.EXCHANGE_RATE, exchangeRate);
        summary.put(ClauseType.BROKER_CURRENCY, paymentCurrency);
        summary.put(ClauseType.BROKER_PAYMENT_METHOD, paymentMethod);

        this.status = status;
        this.clauses = clauses;

        /*this.customerIdentity = new CryptoCustomerWalletModuleActorIdentityImpl("CustomerAlias", new byte[0]);
        this.brokerIdentity = new CryptoCustomerWalletModuleActorIdentityImpl(brokerAlias, new byte[0]);

        String currencyQty = decimalFormat.format(random.nextFloat() * 100);
        String exchangeRate = decimalFormat.format(random.nextFloat());

        summary = new HashMap<>();
        summary.put(ClauseType.CUSTOMER_CURRENCY_QUANTITY, currencyQty);
        summary.put(ClauseType.CUSTOMER_CURRENCY, merchandise);
        summary.put(ClauseType.EXCHANGE_RATE, exchangeRate);
        summary.put(ClauseType.BROKER_CURRENCY, paymentCurrency);
        summary.put(ClauseType.BROKER_PAYMENT_METHOD, paymentMethod);

        this.status = status;
        date = calendar.getTimeInMillis();

        clauses = new HashMap<>();
        clauses.put(ClauseType.CUSTOMER_CURRENCY_QUANTITY, new CryptoCustomerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY_QUANTITY, currencyQty, ClauseStatus.DRAFT));
        clauses.put(ClauseType.CUSTOMER_CURRENCY, new CryptoCustomerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY, merchandise, ClauseStatus.DRAFT));
        clauses.put(ClauseType.BROKER_BANK_ACCOUNT, new CryptoCustomerWalletModuleClauseInformation(ClauseType.BROKER_BANK_ACCOUNT, "Banesco\n2165645454654", ClauseStatus.DRAFT));
        clauses.put(ClauseType.BROKER_CURRENCY, new CryptoCustomerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY, paymentCurrency, ClauseStatus.DRAFT));
        clauses.put(ClauseType.BROKER_PAYMENT_METHOD, new CryptoCustomerWalletModuleClauseInformation(ClauseType.BROKER_PAYMENT_METHOD, paymentMethod, ClauseStatus.DRAFT));
        clauses.put(ClauseType.EXCHANGE_RATE, new CryptoCustomerWalletModuleClauseInformation(ClauseType.EXCHANGE_RATE, exchangeRate, ClauseStatus.DRAFT));
        clauses.put(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER, new CryptoCustomerWalletModuleClauseInformation(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER, "18-11-2015", ClauseStatus.DRAFT));
        clauses.put(ClauseType.BROKER_DATE_TIME_TO_DELIVER, new CryptoCustomerWalletModuleClauseInformation(ClauseType.BROKER_DATE_TIME_TO_DELIVER, "20-11-2015", ClauseStatus.DRAFT));
        */
    }

    @Override
    public ActorIdentity getCustomer() {
        return customerIdentity;
    }

    @Override
    public ActorIdentity getBroker() {
        return brokerIdentity;
    }

    @Override
    public Map<ClauseType, String> getNegotiationSummary() {
        return summary;
    }

    @Override
    public Map<ClauseType, ClauseInformation> getClauses() {
        return clauses;
    }

    @Override
    public NegotiationStatus getStatus() {
        return status;
    }

    @Override
    public String getMemo() {
        return null;
    }

    @Override
    public void setMemo(String memo) {

    }

    @Override
    public long getLastNegotiationUpdateDate() {
        return 0;
    }

    @Override
    public void setLastNegotiationUpdateDate(Long lastNegotiationUpdateDate) {

    }

    @Override
    public long getNegotiationExpirationDate() {
        return 0;
    }

    @Override
    public UUID getNegotiationId() {
        //TODO
        return null;
    }

    @Override
    public void setCancelReason(String cancelReason) {

    }

    @Override
    public String getCancelReason() {
        //TODO
        return null;
    }
}
