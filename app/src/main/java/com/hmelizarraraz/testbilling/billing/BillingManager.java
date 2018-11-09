package com.hmelizarraraz.testbilling.billing;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * BillingManager que manejara todas las interacciones con la Play Store
 * (via libreria de Billing), mantiene la conexion a ella a traves de BillingClient
 * y mantiene los estados o datos de cache temporal si es necesario
 */
public class BillingManager implements PurchasesUpdatedListener {
    private static final String TAG = "BillingManager";

    private final BillingClient billingClient;
    private final Activity activity;

    // Definicion de constantes de SKU de Google Play Developer Console
    private static final HashMap<String, List<String>> SKUS;
    static {
        SKUS = new HashMap<>();
        SKUS.put(BillingClient.SkuType.INAPP, Arrays.asList("gas", "premium"));
        SKUS.put(BillingClient.SkuType.SUBS, Arrays.asList("gold_monthly", "gold_yearly"));
    }

    private static final String SUBS_SKUS[] = {"gold_monthly", "gold_yearly"};

    public BillingManager(Activity activity) {
        this.activity       = activity;
        this.billingClient  = BillingClient.newBuilder(activity).setListener(this).build();
        startServiceConnectionIfNeeded(null);
    }

    @Override
    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {
        Log.d(TAG, ">>>onPurchasesUpdated() response: " + responseCode);
    }

    private void startServiceConnectionIfNeeded(final Runnable executeOnSuccess) {
        if (billingClient.isReady()) {
            Log.i(TAG, ">>>billingClient is ready");
            if (executeOnSuccess != null) {
                executeOnSuccess.run();
            }
        } else {
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(int responseCode) {
                    if (responseCode == BillingClient.BillingResponse.OK) {
                        Log.i(TAG, ">>>onBillingSetupFinished() response: " + responseCode);
                        if (executeOnSuccess != null) {
                            executeOnSuccess.run();
                        } else {
                            Log.w(TAG, ">>>onBillingSetupFinished() error code: " + responseCode);
                        }
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    Log.w(TAG, ">>>onBillingServiceDisconnected()");
                }
            });
        }
    }

    public void querySkuDetailsAsync(@BillingClient.SkuType final String itemType,
                                     final List<String> skuList, final SkuDetailsResponseListener listener) {
        // Especificar un ruunable para iniciar cuando se establezca la conexión con el cliente
        Runnable executeOnConnectedService = new Runnable() {
            @Override
            public void run() {
                SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                        .setSkusList(skuList)
                        .setType(itemType)
                        .build();

                billingClient.querySkuDetailsAsync(
                        skuDetailsParams,
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                                listener.onSkuDetailsResponse(responseCode, skuDetailsList);
                            }
                        });
            }
        };

        // Si el cliente se disconecto, reintentamos 1 vez
        // y si da exito, ejecutamos la consulta
        startServiceConnectionIfNeeded(executeOnConnectedService);
    }

    public List<String> getSkus(@BillingClient.SkuType String type) {
        return SKUS.get(type);
    }

    public void startPurchaseFlow(final String skuId, final String billingType) {
        // Especifica un runnable para empezar cuando la conexion al cliente Billing esta establecida
        Runnable executeOnConnectedService = new Runnable() {
            @Override
            public void run() {
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setType(billingType)
                        .setSku(skuId)
                        .build();
                billingClient.launchBillingFlow(activity, billingFlowParams);
            }
        };

        // Si el cliente se disconecto, reintentamos 1 vez
        // y si da exito, ejecutamos la consulta
        startServiceConnectionIfNeeded(executeOnConnectedService);
    }

    public void destroy() {
        this.billingClient.endConnection();
    }

}
