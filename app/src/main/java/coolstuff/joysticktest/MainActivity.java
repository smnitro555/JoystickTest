package coolstuff.joysticktest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    JoystickView stickLeft , stickRight;
    TextView x1, x2, y1, y2;

    //stickLeft.();
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

        stickLeft.setOnJoystickMoveListener(
                new JoystickView.OnJoystickMoveListener() {
                    public void onValueChanged(int xVal, int yVal, int zVal) {
                        x1.setText(String.valueOf(xVal));
                        y1.setText(String.valueOf(yVal));
                    }
                }, JoystickView.DEFAULT_LOOP_INTERVAL);

        stickRight.setOnJoystickMoveListener(
                new JoystickView.OnJoystickMoveListener() {
                    public void onValueChanged(int xVal, int yVal, int zVal) {
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


}
