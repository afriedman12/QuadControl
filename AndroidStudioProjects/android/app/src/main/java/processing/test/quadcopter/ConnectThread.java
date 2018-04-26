package processing.test.quadcopter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

class ConnectThread extends Thread {
    private final BluetoothSocket socket;
    private final BluetoothDevice device;

    private final String LOG_TAG = "ConnectThread";


    private final UUID MY_UUID = UUID.fromString("633e589e-2036-4fac-8371-d9a9544e9355");

    public ConnectThread(BluetoothDevice connectToDevice) {
        // Use a temp object that is later assigned to the class member
        // because the class member is final
        BluetoothSocket tmpSocket = null;
        device = connectToDevice;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also in the server code
            tmpSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e (LOG_TAG, "Socket's create method failed", e);
        }

        socket = tmpSocket;
    }

    public void run() {
        try {
            // Connect to the remote device through the socket. This call blocks until
            // it succeeds or throws an exception
            socket.connect();
        } catch (IOException connectException) {
            // unable to connect...close the socket and return
            try {
                socket.close();
            } catch (IOException closeException) {
                Log.e(LOG_TAG, "Could not close the client socket", closeException);
            }

            return;
        }

        manageMyConnectedSocket(socket);
    }

    public void cancel() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not close the client socket", e);
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket localSocket)
    {
        BTManager.SetBluetoothSocket(localSocket);
    }





}