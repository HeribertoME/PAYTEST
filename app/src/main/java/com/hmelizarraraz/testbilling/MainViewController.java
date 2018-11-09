package com.hmelizarraraz.testbilling;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.DrawableRes;
import android.util.Log;

class MainViewController {

    private static final String TAG = "MainViewController";

    // Graphics for the gas gauge
    private static int[] TANK_RES_ID = { R.drawable.gas0, R.drawable.gas1,
            R.drawable.gas2, R.drawable.gas3, R.drawable.gas4 };

    // How many units (1/4 tank is our unit) fill in the tank.
    private static final int TANK_MAX = 4;

    private MainActivity activity;

    // Current amount of gas in tank, in units
    private int tank;

    public MainViewController(MainActivity activity) {
        this.activity = activity;
        loadData();
    }

    public void useGas() {
        tank--;
        saveData();
        Log.d(TAG, ">>>Tank is now: " + tank);
    }

    public boolean isTankEmpty() {
        return tank <= 0;
    }

    public boolean isTankFull() {
        return tank >= TANK_MAX;
    }

    public @DrawableRes int getTankResId() {
        int index = (tank >= TANK_RES_ID.length) ? (TANK_RES_ID.length - 1) : tank;
        return TANK_RES_ID[index];
    }

    /**
     * Save current tank level to disc
     *
     * Note: In a real application, we recommend you save data in a secure way to
     * prevent tampering.
     * For simplicity in this sample, we simply store the data using a
     * SharedPreferences.
     */
    private void saveData() {
        SharedPreferences.Editor spe = activity.getPreferences(Context.MODE_PRIVATE).edit();
        spe.putInt("tank", tank);
        spe.apply();
        Log.d(TAG, ">>>Saved data: tank = " + String.valueOf(tank));
    }

    private void loadData() {
        SharedPreferences sp = activity.getPreferences(Context.MODE_PRIVATE);
        tank = sp.getInt("tank", 2);
        Log.d(TAG, ">>>Loaded data: tank = " + String.valueOf(tank));
    }
}
