package edu.citadel.tyler.arduinobot;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import java.util.UUID;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import java.util.Set;
import android.widget.ListView;
import android.os.Handler;
import android.os.Message;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    // Declarations
    String status;
    Button leftButton;
    Button rightButton;
    Button forwardButton;
    Button reverseButton;
    Button stopButton;
    Button action1Button;
    Button action2Button;
    Button action3Button;
    Button action4Button;
    Button connectButton;
    Button disconnectButton;
    TextView statusText;
    TextView textLight;
    BluetoothAdapter androidBluetoothAdapter;
    String opcode;
    static final int REQUEST_ENABLE_BT = 1;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    BluetoothAdapter mmAdapter;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    ArrayAdapter<String> mmPairedDevicesArrayAdapter;
    ListView pairedListView;
    // handler constants
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_READ = 2;
    String readMessage="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // IDs
        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);
        forwardButton = (Button) findViewById(R.id.forwardButton);
        reverseButton = (Button) findViewById(R.id.reverseButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        action1Button = (Button) findViewById(R.id.action1Button);
        action2Button = (Button) findViewById(R.id.action2Button);
        action3Button = (Button) findViewById(R.id.action3Button);
        action4Button = (Button) findViewById(R.id.action4Button);
        connectButton = (Button) findViewById(R.id.connectButton);
        disconnectButton = (Button) findViewById(R.id.disconnectButton);
        statusText = (TextView) findViewById(R.id.statusText);
        textLight = (TextView) findViewById(R.id.textLight);
        // ClickListeners
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        forwardButton.setOnClickListener(this);
        reverseButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        action1Button.setOnClickListener(this);
        action2Button.setOnClickListener(this);
        action3Button.setOnClickListener(this);
        action4Button.setOnClickListener(this);

        connectButton.setOnClickListener(this);
        disconnectButton.setOnClickListener(this);
        // Special
        mmPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name); // ArrayAdapter for paired devices
        pairedListView = (ListView) findViewById(R.id.title_paired_devices); // Listview for paired devices
        pairedListView.setAdapter(mmPairedDevicesArrayAdapter);
        androidBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        statusText.setText(status);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        statusBT();
        mmPairedDevicesArrayAdapter.clear();// clears the array so items aren't duplicated when resuming from onPause
        mmAdapter = BluetoothAdapter.getDefaultAdapter(); // Get Adapter
        Set<BluetoothDevice> pairedDevices = mmAdapter.getBondedDevices(); // Get Paired and add to list
        // Add previously paired devices to the array
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE); //make textbox visible
            for (BluetoothDevice device : pairedDevices) {
                mmPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            mmPairedDevicesArrayAdapter.add("No paired devices!");
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (v == leftButton) {
                opcode = "3";
                status = "Sent Opcode: 2 (Left)";
                write(opcode);
                statusText.setText(status);
            }
            if (v == rightButton) {
                opcode = "2";
                status = "Sent Opcode: 3 (Right)";
                write(opcode);
                statusText.setText(status);
            }
            if (v == forwardButton) {
                opcode = "1";
                status = "Sent Opcode: 1 (Forward)";
                write(opcode);
                statusText.setText(status);
            }
            if (v == reverseButton) {
                opcode = "9";
                status = "Sent Opcode: 9 (Reverse)";
                write(opcode);
                statusText.setText(status);
            }
            if (v == stopButton) {
                opcode = "8";
                status = "Sent Opcode: 6 (Stop)";
                write(opcode);
                statusText.setText(status);
            }
            if (v == action1Button) {
                opcode = "4";
                status = "Sent Opcode: 4 (Action 1)";
                write(opcode);
                statusText.setText(status);
            }
            if (v == action2Button) {
                opcode = "5";
                status = "Sent Opcode: 5 (Action 2)";
                write(opcode);
                statusText.setText(status);
            }
            if (v == action3Button) {
                opcode = "6";
                status = "Sent Opcode: 6 (Action 1 Off)";
                write(opcode);
                statusText.setText(status);
            }
            if (v == action4Button) {
                opcode = "7";
                status = "Sent Opcode: 7 (Action 2 Off)";
                write(opcode);
                statusText.setText(status);
            }

            if (v == connectButton) {
                status = "Attempting to Connect...";
                statusText.setText(status);
                connect();
            }
            if (v == disconnectButton) {
                disconnect();
            }
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "IOException in onClick", Toast.LENGTH_LONG).show();
        }
    }

    public void connect() throws IOException {
        statusText.setText("Turning on Bluetooth...");
        mmAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mmAdapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
            statusText.setText("Bluetooth Activated");
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            mmDevice = mmAdapter.getRemoteDevice("30:14:12:17:18:91"); // Change to device.getAddress in future -- ARDUINO MAC
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            statusText.setText("Socket Established");
            mmSocket.connect();
            statusText.setText("Establishing streams");
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            ReceiveBytes ReceiveBT;
            ReceiveBT = new ReceiveBytes(mmSocket);
            new Thread(ReceiveBT).start();
            statusText.setText("Connection Complete.");
            Toast.makeText(getBaseContext(), "Ready", Toast.LENGTH_SHORT).show();
        }
    }

    public void statusBT() {
        mmAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mmAdapter == null) {
            Toast.makeText(getBaseContext(), "Bluetooth Not Currently Enabled", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!mmAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1); // Turn on BT
            }
        }
    }


    public void disconnect() {
        try{
            status = "Disconnecting...";
            statusText.setText(status);
            mmInputStream.close();
            mmOutputStream.close();
            mmSocket.close();
            androidBluetoothAdapter.disable();
            statusText.setText("Bluetooth Disconnected");
    } catch(RuntimeException e){} catch(IOException e){}
    }

    public void write(String sendString) {
        if (mmAdapter == null) {
            Toast.makeText(getBaseContext(), "Turn On Bluetooth First!", Toast.LENGTH_SHORT).show();
        }
        else {
            byte[] Buffer = sendString.getBytes();           //converts entered String into bytes
            try {
                mmOutputStream.write(Buffer);                //write bytes over BT connection via outstream
            }
            catch (IOException e) {
                // if this fails, alert user then close
                Toast.makeText(getBaseContext(), "Send Failure", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    // Handler that gets information back from the Socket
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Get the bytes from the msg.obj
            byte[] readBuf = (byte[]) msg.obj;
            // construct a string from the valid bytes in the buffer
            readMessage = new String(readBuf, 0, msg.arg1);
            if (readMessage.startsWith(".")){
                textLight.setText("0" + readMessage + "%");
            }
        }
    };


    private class ReceiveBytes implements Runnable {
        private BluetoothSocket btSocket;
        private InputStream btInputStream = null;
        public ReceiveBytes(BluetoothSocket socket) {
            btSocket = socket;
            try {
                btInputStream = btSocket.getInputStream();
            } catch (IOException streamError) {
                Toast.makeText(getBaseContext(), "Send/Receive error", Toast.LENGTH_LONG).show();
            }
        }

        public void run() {
            byte[] buffer = new byte[1024]; // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = btInputStream.read(buffer);
                    // Send the obtained bytes to the UI
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}








