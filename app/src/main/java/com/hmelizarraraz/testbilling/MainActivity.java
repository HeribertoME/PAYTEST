package com.hmelizarraraz.testbilling;

import android.app.AlertDialog;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hmelizarraraz.testbilling.billing.BillingManager;
import com.hmelizarraraz.testbilling.billing.BillingProvider;
import com.hmelizarraraz.testbilling.skulist.AcquireFragment;

public class MainActivity extends FragmentActivity implements BillingProvider {

    // Debug tag, for logging
    private static final String TAG = "MainActivity";

    // Tag for a dialog that allows us to find it when screen was rotated
    private static final String DIALOG_TAG = "dialog";

    private BillingManager billingManager;
    private AcquireFragment acquireFragment;
    private MainViewController viewController;

    private View screenWait, screenMain;
    private ImageView gasImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Iniar el controlador y cargar la data
        viewController = new MainViewController(this);

        // Try to restore dialog fragment if we were showing it prior to screen rotation
        if (savedInstanceState != null) {
            acquireFragment = (AcquireFragment) getSupportFragmentManager()
                    .findFragmentByTag(DIALOG_TAG);
        }

        // Crear e inicializar BillingManager el cual establece comunicacion con BillingLibrary
        billingManager  = new BillingManager(this);

        screenWait      = findViewById(R.id.screen_wait);
        screenMain      = findViewById(R.id.screen_main);
        gasImageView    = findViewById(R.id.gas_gauge);

        // Especificacion de los clicks listener
        // para los botones de comprar y manejar
        findViewById(R.id.button_purchase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPurchaseButtonClicked(v);
            }
        });

        findViewById(R.id.button_drive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDriveButtonClicked(v);
            }
        });

        showRefreshedUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        billingManager.destroy();
    }

    @Override
    public BillingManager getBillingManager() {
        return this.billingManager;
    }

    /**
     * El usuario hizo click en el boton "Comprar" - Muestra un dialogo de compra con todos los SKUs disponibles
     * @param view Boton
     */
    public void onPurchaseButtonClicked(View view) {
        Log.d(TAG, ">>>Boton Comprar pulsado");

        if (acquireFragment == null) {
            acquireFragment = new AcquireFragment();
        }

        if (!isAcquireFragmentShown()) {
            acquireFragment.show(getSupportFragmentManager(), DIALOG_TAG);
        }
    }

    /**
     * Boton manejar pulsado. Quema gasolina!
     * @param view Boton
     */
    public void onDriveButtonClicked(View view) {
        Log.d(TAG, "Boton manejar pulsado.");

        if (viewController.isTankEmpty()) {
            alert(R.string.alert_no_gas);
        } else {
            viewController.useGas();
            alert(R.string.alert_drove);
            updateUI();
        }
    }

    /**
     * Remueve el spiner de carga y refresca la UI
     */
    public void showRefreshedUI() {
        setWaitScreen(false);
        updateUI();

        if (isAcquireFragmentShown()) {
            acquireFragment.refreshUI();
        }
    }

    /**
     * Muestra una alerta de dialogo al usuario
     * @param messageId String id a mostrar dentro del alert dialog
     */
    @UiThread
    void alert(@StringRes int messageId) {
        alert(messageId, null);
    }

    /**
     * Muestra una alerta de dialogo al usuario
     * @param messageId String id a mostrar dentro del alert dialog
     * @param optionalParam Atributo adicional para el string
     */
    @UiThread
    void alert(@StringRes int messageId, @Nullable Object optionalParam) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("Dialog could be shown only from the main thread");
        }

        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setNeutralButton("OK", null);

        if (optionalParam == null) {
            bld.setMessage(messageId);
        } else {
            bld.setMessage(getResources().getString(messageId, optionalParam));
        }

        bld.create().show();
    }

    /**
     * Habilita o deshabilita la pantalla de espera
     * @param set
     */
    private void setWaitScreen(boolean set) {
        screenMain.setVisibility(set ? View.GONE : View.VISIBLE);
        screenWait.setVisibility(set ? View.VISIBLE : View.GONE);
    }

    /**
     * Actualiza la UI para reflejar el modelo
     */
    @UiThread
    private void updateUI() {
        Log.d(TAG, ">>>Actualizando la UI. Thread: " + Thread.currentThread().getName());

        // Actualizar el indicador de gas para reflejar el estado del tanque
        gasImageView.setImageResource(viewController.getTankResId());
    }

    public boolean isAcquireFragmentShown() {
        return acquireFragment != null && acquireFragment.isVisible();
    }
}
