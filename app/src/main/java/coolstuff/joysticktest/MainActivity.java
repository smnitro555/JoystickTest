package coolstuff.joysticktest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {

    JoystickView stickLeft , stickRight;
    TextView x1, x2, y1, y2;
    BluetoothAdapter myBluetoothAdapter;
    BluetoothSocket myBluetoothSocket;
    BluetoothDevice myBluetoothDevice;
    OutputStream myOutputStream;
    InputStream myInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    DroneVal drone;
    Button connectDevice;

    int REQUEST_ENABLE_BT = 1;

    public View findViewById(int id) {
        return super.findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stickLeft = (JoystickView) findViewById(R.id.stick1);
        stickRight = (JoystickView) findViewById(R.id.stick2);
        connectDevice = (Button) findViewById(R.id.connectDevice);
        x1 = (TextView) findViewById(R.id.x1);
        y1 = (TextView) findViewById(R.id.y1);
        x2 = (TextView) findViewById(R.id.x2);
        y2 = (TextView) findViewById(R.id.y2);


        stickLeft.setOnJoystickMoveListener(
                new JoystickView.OnJoystickMoveListener() {
                    public void onValueChanged(int angleVal, int powerVal, int zVal) {
                        double xVal = (powerVal * Math.sin(Math.toRadians(angleVal)))/100;
                        double yVal = (powerVal * Math.cos(Math.toRadians(angleVal)))/100;
                        x1.setText(String.valueOf(xVal));
                        y1.setText(String.valueOf(yVal));
                        int xValueInt = (int) (100 * xVal);
                        int yValueInt = (int) (100 * yVal);
                        drone.updateManuever(xValueInt, yValueInt);
                        sendData();
                    }
                }, JoystickView.DEFAULT_LOOP_INTERVAL);

        stickRight.setOnJoystickMoveListener(
                new JoystickView.OnJoystickMoveListener() {
                    public void onValueChanged(int angleVal, int powerVal, int zVal) {
                        double xVal = (powerVal * Math.sin(Math.toRadians(angleVal))) / 100;
                        double yVal = (powerVal * Math.cos(Math.toRadians(angleVal))) / 100;
                        x2.setText(String.valueOf(xVal));
                        y2.setText(String.valueOf(yVal));
                        int xValueInt = (int) (100 * xVal);
                        int yValueInt = (int) (100 * yVal);
                        drone.updatePowerVals(xValueInt, yValueInt);
                        sendData();
                    }
                }, JoystickView.DEFAULT_LOOP_INTERVAL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ConnectDevice(View view) {
            try
            {
                findBT();
                openBT();
                String temp = "Device connected";
                Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
            }
            catch (IOException ex) {
                String temp = "Device Unable to Connect";
                Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
            }
    }

    //Close button
    public void DisconnectDevice(View view) {
            try
            {
                closeBT();
            }
            catch (IOException ex) { }
    }

    void findBT()
    {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null)
        {
            Toast.makeText(this, "No bluetooth adapter available", Toast.LENGTH_SHORT).show();
        }

        if(!myBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("HC-06"))
                {
                    myBluetoothDevice = device;
                    break;
                }
            }
        }
        Toast.makeText(this, "Bluetooth Device Found", Toast.LENGTH_SHORT).show();
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        myBluetoothSocket = myBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
        myBluetoothSocket.connect();
        myOutputStream = myBluetoothSocket.getOutputStream();
        myInputStream = myBluetoothSocket.getInputStream();

        beginListenForData();

        Toast.makeText(this, "Bluetooth Opened", Toast.LENGTH_SHORT).show();
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = myInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            myInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    public void sendData() {
        try {
            String msg = DroneVal.getData();
            msg += "\n";
            myOutputStream.write(msg.getBytes());
        } catch (IOException ex) {
            String temp = "Unable to Send Data";
            Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
        }
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        myOutputStream.close();
        myInputStream.close();
        myBluetoothSocket.close();
        Toast.makeText(getApplicationContext(), "Bluetooth Closed", Toast.LENGTH_SHORT).show();
    }

}
