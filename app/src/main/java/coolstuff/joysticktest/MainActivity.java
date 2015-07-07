package coolstuff.joysticktest;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends ActionBarActivity {

    private JoystickView stickLeft , stickRight;
    private TextView x1, x2, y1, y2;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;

    public View findViewById(int id) {
        return super.findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stickLeft = (JoystickView) findViewById(R.id.stick1);
        stickRight = (JoystickView) findViewById(R.id.stick2);
        x1 = (TextView) findViewById(R.id.x1);
        y1 = (TextView) findViewById(R.id.y1);
        x2 = (TextView) findViewById(R.id.x2);
        y2 = (TextView) findViewById(R.id.y2);

        BA = BluetoothAdapter.getDefaultAdapter();

        stickLeft.setOnJoystickMoveListener(
                new JoystickView.OnJoystickMoveListener() {
                    public void onValueChanged(int angleVal, int powerVal, int zVal) {
                        double xVal = (powerVal * Math.sin(Math.toRadians(angleVal)))/100;
                        double yVal = (powerVal * Math.cos(Math.toRadians(angleVal)))/100;
                        x1.setText(String.valueOf(xVal));
                        y1.setText(String.valueOf(yVal));
                    }
                }, JoystickView.DEFAULT_LOOP_INTERVAL);

        stickRight.setOnJoystickMoveListener(
                new JoystickView.OnJoystickMoveListener() {
                    public void onValueChanged(int angleVal, int powerVal, int zVal) {
                        double xVal = (powerVal * Math.sin(Math.toRadians(angleVal)))/100;
                        double yVal = (powerVal * Math.cos(Math.toRadians(angleVal)))/100;
                        x2.setText(String.valueOf(xVal));
                        y2.setText(String.valueOf(yVal));
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
        String temp = "Device connected";
        Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
    }


    public void blueOn(View view) {
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(),"Turned on",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Already on", Toast.LENGTH_LONG).show();
        }
    }
}
