package com.example.android.networkconnect;

import android.hardware.input.InputManager;
import android.view.View;
import android.widget.Toast;

/**
 * Created by adonniou on 21/04/15.
 */
public class InputListener_usb implements InputManager.InputDeviceListener {
    @Override
    public void onInputDeviceAdded(int deviceId) {
        //Toast.makeText( "Oninputadd: " + deviceId, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this., "Oninputadd: "+ deviceId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {

    }

    @Override
    public void onInputDeviceChanged(int deviceId) {

    }
}
