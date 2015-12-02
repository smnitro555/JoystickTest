package coolstuff.joysticktest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.SeekBar;
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
    SeekBar motor1trimBar, motor2trimBar, motor3trimBar, motor4trimBar;
    TextView x1, x2, y1, y2;
    BluetoothAdapter myBluetoothAdapter;
    BluetoothSocket myBluetoothSocket;
    BluetoothDevice myBluetoothDevice;
    OutputStream myOutputStream;
    InputStream myInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    DroneVal drone;
    Button connectDevice;
    boolean calibrate = false;

    public View findViewById(int id) {
        return super.findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stickLeft = (JoystickView) findViewById(R.id.stick1);
        stickRight = (JoystickView) findViewById(R.id.stick2);
        motor1trimBar = (SeekBar) findViewById(R.id.motor1trimBar);
        motor2trimBar = (SeekBar) findViewById(R.id.motor2trimBar);
        motor3trimBar = (SeekBar) findViewById(R.id.motor3trimBar);
        motor4trimBar = (SeekBar) findViewById(R.id.motor4trimBar);
        connectDevice = (Button) findViewById(R.id.connectDevice);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.highValue);
        ToggleButton toggle2 = (ToggleButton) findViewById(R.id.startup);
        x1 = (TextView) findViewById(R.id.x1);
        y1 = (TextView) findViewById(R.id.y1);
        x2 = (TextView) findViewById(R.id.x2);
        y2 = (TextView) findViewById(R.id.y2);
        drone = new DroneVal();

        motor1trimBar.setProgress((motor1trimBar.getMax() / 2));
        motor2trimBar.setProgress((motor2trimBar.getMax() / 2));
        motor3trimBar.setProgress((motor3trimBar.getMax() / 2));
        motor4trimBar.setProgress((motor4trimBar.getMax() / 2));

        stickLeft.setOnJoystickMoveListener(
                new JoystickView.OnJoystickMoveListener() {
                    public void onValueChanged(int angleVal, int powerVal, int zVal) {
                        double xVal = (powerVal * Math.sin(Math.toRadians(angleVal))) / 100;
                        double yVal = (powerVal * Math.cos(Math.toRadians(angleVal))) / 100;
                        x1.setText(String.valueOf(xVal));
                        y1.setText(String.valueOf(yVal));
                        int xValueInt = (int) (100 * xVal);
                        int yValueInt = (int) (100 * yVal);
                        if (!calibrate) {
                            drone.updateManuever(xValueInt, yValueInt);
                            try {
                                // sendData();
                            } catch (Exception ex) {
                                //String temp = "Could not Update Right Stick";
                                //Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, JoystickView.DEFAULT_LOOP_INTERVAL);

        stickRight.setOnJoystickMoveListener(
                new JoystickView.OnJoystickMoveListener() {
                    public void onValueChanged(int angleVal, int powerVal, int zVal) {
                        double xVal = (powerVal * Math.sin(Math.toRadians(angleVal))) / 100;
                        double yVal = (powerVal * Math.cos(Math.toRadians(angleVal))) / 100;
                        x2.setText(String.valueOf(xVal));
                        y2.setText(String.valueOf(yVal));
                        //int xValueInt = (int) (100 * xVal);
                        //int yValueInt = (int) (100 * yVal);
                        if (!calibrate) {
                            drone.updatePowerVals(xVal, yVal);
                            try {
                                sendData();
                            }
                            catch (Exception ex) {
                                //String temp = "Could not Update Left Stick";
                                //Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, JoystickView.DEFAULT_LOOP_INTERVAL);

        motor1trimBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        drone.setTrim(progress, 1, motor1trimBar.getMax());
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        motor2trimBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        drone.setTrim(progress, 2, motor1trimBar.getMax());
                    }
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        motor3trimBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        drone.setTrim(progress, 3, motor1trimBar.getMax());
                    }
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        motor4trimBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        drone.setTrim(progress, 4, motor1trimBar.getMax());
                    }
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    calibrate = true;
                    drone.updatePowerVals(1.0, 1.0);
                    try {
                        sendData();
                    } catch (Exception ex) {
                        //String temp = "Could not Update Left Stick";
                        //Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // The toggle is disabled
                    calibrate = false;
                    drone.setCalibrateLow();
                    try {
                        sendData();
                    } catch (Exception ex) {
                        //String temp = "Could not Update Left Stick";
                        //Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        toggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    // Startup Mode
                    calibrate = true;
                    drone.setStartup();
                    try {
                        sendData();
                    }
                    catch (Exception ex) {
                        //String temp = "Could not Update Left Stick";
                        //Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // The toggle is disabled
                    calibrate = false;
                    drone.setCalibrateLow();
                    try {
                        sendData();
                    }
                    catch (Exception ex) {
                        //String temp = "Could not Update Left Stick";
                        //Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
                    boolean found = findBT();
                    if (found) {
                        openBT();
                        String temp = "Device connected";
                        Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();

                    } else {
                        String temp = "Device Not Connected";
                        Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (IOException ex) {
                    String temp = "Device Unable to Connect";
                    Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                }

    }

    //Close button
    public void DisconnectDevice(View view) {
            try
            {
                closeBT();
            }
            catch (IOException ex) {
                String temp = "Unable to Disconnect";
                Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
            }
    }

    boolean findBT()
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
                    return true;
                }
            }
        }
        return false;
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        myBluetoothSocket = myBluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
        myBluetoothSocket.connect();
        myOutputStream = myBluetoothSocket.getOutputStream();
        myInputStream = myBluetoothSocket.getInputStream();

        //beginListenForData();

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
        if (!((drone.previousSent).equals(drone.getData()))) {
            try {
                String msg = drone.getData();
                msg += "\n";
                drone.setPreviousSentData(drone.getData());
                myOutputStream.write(msg.getBytes());
            } catch (IOException ex) {
                String temp = "Unable to Send Data";
                Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendData2(String message) {
        Log.e("SendData2", "it has run");
        try {
            String msg = message;
            msg += "\n";
            Log.e("OUR BYTES", new String(msg.getBytes()));
            myOutputStream.write(msg.getBytes());
        } catch (IOException ex) {
            String temp = "Unable to Send Data";
            Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
        }
    }

    void closeBT() throws IOException
    {
        try {
            stopWorker = true;
            myOutputStream.close();
            myInputStream.close();
            myBluetoothSocket.close();
            Toast.makeText(getApplicationContext(), "Bluetooth Closed", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex) {
            String temp = "Unable to Close Connection";
            Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
        }

    }

}
