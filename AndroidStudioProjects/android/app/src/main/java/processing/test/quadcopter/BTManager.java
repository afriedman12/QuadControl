package processing.test.quadcopter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public class BTManager {
    private static BluetoothAdapter btAdapter;
    private static BluetoothSocket btSocket;
    private static OutputStream btOutStream;

    private static String LOG_TAG = "BTManager";

    private static String TARGET_DEVICE = "C4:9D:ED:06:39:9D";

    // Request range : 5000 - 5999
    private static int REQUEST_ENABLE_BT = 5001;

    private static String lastXVelocity;
    private static String lastYVelocity;
    private static String lastZVelocity;

    public static BluetoothDevice init(Activity activity) {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            // TBD : need to handle bluetooth being disabled or unavailable
        }

        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        BluetoothDevice btServer = null;

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();

                if (deviceHardwareAddress.equals(TARGET_DEVICE))
                    btServer = device;
            }
        }

        return btServer;
    }

    public static synchronized void SetBluetoothSocket(BluetoothSocket newBtSocket) {

        // null out the existing socket and stream if they exist
        btSocket = null;
        btOutStream = null;

        if (newBtSocket != null) {
            try {
                OutputStream btStream = newBtSocket.getOutputStream();

                btSocket = newBtSocket;
                btOutStream = btStream;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error creating output stream", e);
            }
        }
    }

    public static synchronized void WriteToSocket(String s) {
        if (btSocket == null) {
            Log.e(LOG_TAG, "Attempted to write without an open stream");
        } else {
            try {
                btOutStream.write(s.getBytes());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to write to output stream");
            }
        }
    }

    public static void handleVelocities(float x, float y, float z){
        String xAsString = String.format("%.4f", x);
        String yAsString = String.format("%.4f", y);
        String zAsString = String.format("%.4f", z);

        if(!xAsString.equals(lastXVelocity )|| !yAsString.equals(lastYVelocity)|| !zAsString.equals(lastZVelocity)){
            lastXVelocity = xAsString;
            lastYVelocity = yAsString;
            lastZVelocity = zAsString;
            WriteToSocket("V " + xAsString + "; " + yAsString + "; " + zAsString + "\r\n");
        }
    }
}