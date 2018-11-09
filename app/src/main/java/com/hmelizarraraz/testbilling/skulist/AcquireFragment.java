package com.hmelizarraraz.testbilling.skulist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.hmelizarraraz.testbilling.R;
import com.hmelizarraraz.testbilling.billing.BillingProvider;
import com.hmelizarraraz.testbilling.skulist.row.SkuRowData;

import java.util.ArrayList;
import java.util.List;

public class AcquireFragment extends DialogFragment {
    private static final String TAG = "AcquireFragment";

    private RecyclerView recyclerView;
    private SkusAdapter adapter;
    private View loadingView;
    private TextView errorTextView;
    private BillingProvider billingProvider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.acquire_fragment, container, false);

        errorTextView   = root.findViewById(R.id.error_textview);
        recyclerView    = root.findViewById(R.id.list);
        loadingView     = root.findViewById(R.id.screen_wait);
        // Setup a toolbar for this fragment
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.setTitle(R.string.button_purchase);
        setWaitScreen(true);
        onManagerReady((BillingProvider) getActivity());
        return root;
    }

    /**
     * Refresca la UI de este fragmento
     */
    public void refreshUI() {
        Log.d(TAG, ">>>Parece que la lista de compras podría haberse actualizado, actualizando la interfaz de usuario");
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * NOtifica al fragmento que el administrador de pagos esta listo y provee
     * una instancia de BillingProvider para acceder a el
     * @param billingProvider Instancia de BillingProvider
     */
    public void onManagerReady(BillingProvider billingProvider) {
        this.billingProvider = billingProvider;
        if (recyclerView != null) {
            adapter = new SkusAdapter(this.billingProvider);
            if (recyclerView.getAdapter() == null) {
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
            handleManagerAndUiReady();
        }
    }

    /**
     * Habilita o inhabilita la pantalla de espera
     * @param set Valor para habilitar
     */
    private void setWaitScreen(boolean set) {
        recyclerView.setVisibility(set ? View.GONE : View.VISIBLE);
        loadingView.setVisibility(set ? View.VISIBLE : View.GONE);
    }

    /**
     * Ejecuta la consulta para detalles de SKU en el hilo de fondo
     */
    private void handleManagerAndUiReady() {
        final List<SkuRowData> inList = new ArrayList<>();
        SkuDetailsResponseListener responseListener = new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                    // Reempaqueta el resultado para un adaptador
                    for (SkuDetails details : skuDetailsList) {
                        Log.i(TAG, ">>>Found sku: " + details);
                        inList.add(new SkuRowData(
                                details.getSku(),
                                details.getTitle(),
                                details.getPrice(),
                                details.getDescription(),
                                details.getType()
                        ));
                    }

                    if (inList.size() == 0) {
                        displayAnErrorIfNeeded();
                    } else {
                        adapter.updateData(inList);
                        setWaitScreen(false);
                    }
                }
            }
        };

        // Iniciar consulta para in-app SKUs
         List<String> skus = billingProvider.getBillingManager().getSkus(BillingClient.SkuType.INAPP);
         billingProvider.getBillingManager().querySkuDetailsAsync(BillingClient.SkuType.INAPP, skus, responseListener);


        // Iniciar consulta para SKUs de suscripcion
        skus = billingProvider.getBillingManager().getSkus(BillingClient.SkuType.SUBS);
        billingProvider.getBillingManager().querySkuDetailsAsync(BillingClient.SkuType.SUBS, skus, responseListener);

    }

    private void displayAnErrorIfNeeded() {
        if (getActivity() == null || getActivity().isFinishing()) {
            Log.i(TAG, ">>>No se necesita mostar error - la actividad ya esta finalizando");
            return;
        }

        loadingView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
        // TODO: Manejar varios códigos de respuesta de Billing Manager
        //errorTextView.setText(getText(R.string.error_not_finished));

    }


}
