package com.conecuh.bluetoothjoystick;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import com.conecuh.bluetoothjoystick.common.logger.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by dwilkins on 12/21/13.
 */
public class ConnectedThread extends Thread {
    private InputStream mmInStream = null;
    private OutputStream mmOutStream = null;
    android.os.Handler h;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    // SPP UUID service
    private static String TAG = "ConnectedThread";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address = "20:13:10:25:40:43";


    public final int MOVE_MESSAGE = 100;
    public final int TURN_MESSAGE = 101;
    public final int STOP_MESSAGE = 102;
    public final int PAUSE_MESSAGE = 103;
    public final int RESUME_MESSAGE = 104;

    public android.os.Handler joystickHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            byte[] readBuf = (byte[]) msg.obj;
            switch (msg.what) {
                case MOVE_MESSAGE:													// if receive massage
                    String strSpeed = new String(readBuf, 0, msg.arg1);					// create string from bytes array
                    Float fSpeed = Float.parseFloat(strSpeed);
                    break;
                case TURN_MESSAGE:													// if receive massage
                    String strDirection = new String(readBuf, 0, msg.arg1);					// create string from bytes array
                    Float fDirection = Float.parseFloat(strDirection);
                    break;
                case STOP_MESSAGE:													// if receive massage
                    break;
                case PAUSE_MESSAGE:
                    onPause();
                    break;
                case RESUME_MESSAGE:
                    onResume();
                    break;
            }
        };
    };


    public ConnectedThread() {
        // Get the input and output streams, using temp objects because
        // member streams are final
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }


    public void run() {
        byte[] buffer = new byte[256];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                if(mmInStream != null) {
                    bytes = mmInStream.read(buffer);		// Get number of bytes and message in "buffer"
                } else {
                    sleep(100);
                }
//                h.obtainMessage(RECEIVE_MESSAGE, bytes, -1, buffer).sendToTarget();		// Send to message queue Handler
            } catch (IOException e) {
                break;
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String message) {
        byte[] msgBuffer = message.getBytes();
        try {
            mmOutStream.write(msgBuffer);
        } catch (IOException e) {
        }
    }

    public void resumeMessage() {
        joystickHandler.obtainMessage(RESUME_MESSAGE, 0,0).sendToTarget();		// Send to message queue Handler
    }
    public void pauseMessage() {
        joystickHandler.obtainMessage(PAUSE_MESSAGE, 0,0).sendToTarget();		// Send to message queue Handler
    }


    public void onResume() {
        Log.d(TAG, "...onResume - try connect...");
        checkBTState();
        if(btAdapter == null) {
            return;
        }
        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            btSocket = createBluetoothSocket(device);
            try {
                tmpIn = btSocket.getInputStream();
                tmpOut = btSocket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

    /*try {
      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
    } catch (IOException e) {
      errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
    }*/

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        if(btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.


    }

    public void onPause() {
//        Log.d(TAG, "...In onPause()...");

        try     {
            if(btSocket != null) {
                btSocket.close();
                btSocket = null;
            }
            btAdapter = null;
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }




    private void errorExit(String title, String message){
//        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
//        finish();
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            btAdapter = BluetoothAdapter.getDefaultAdapter();		// get Bluetooth adapter
            if(btAdapter == null) {
//                warning("Fatal Error", "Bluetooth not supported on this device");
                return;
            }
            if (!btAdapter.isEnabled()) {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, 1);
            }
        }
    }



}
