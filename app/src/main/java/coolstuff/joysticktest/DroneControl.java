package coolstuff.joysticktest;

import android.bluetooth.BluetoothSocket;

/**
 * Created by smnitro on 7/17/2015.
 */
public class DroneControl {
    private int motor1, motor2, motor3, motor4;
    private int axisX = 0;
    private int axisY = 0;
    private int axisZ = 0;

    public DroneControl() {
        motor1 = 0;
        motor2 = 0;
        motor3 = 0;
        motor4 = 0;
    }

    public void interpretString(String incoming) {
        // String transmitted as 3 digits X value, 3 digits y value, 3 digits z value
        axisX = Integer.parseInt(incoming.substring(0,2));
        axisY = Integer.parseInt(incoming.substring(3,5));
        axisZ = Integer.parseInt(incoming.substring(6,8));
    }

    @Override
    public String toString() {
        // Outputs the motors values as 3 digit values, that are representative of the
        // percentage values of the motor control.
        String moto1 = String.format("%03d", motor1);
        String moto2 = String.format("%03d", motor2);
        String moto3 = String.format("%03d", motor3);
        String moto4 = String.format("%03d", motor4);
        return ((moto1.concat(moto2)).concat(moto3)).concat(moto4);
    }

    public void autoPilotTime(Boolean autopilotCondition, ConnectedThread connection) {

    }
}
