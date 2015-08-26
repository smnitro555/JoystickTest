package coolstuff.joysticktest;

/**
 * Created by Siva on 8/21/2015.
 * Used to Calculate Values from Joystick to Motor Values
 */
public class DroneVal {
    int motor1 = 0;
    int motor2 = 0;
    int motor3 = 0;
    int motor4 = 0;

    public void updatePowerVals(int x1, int y1) {
        if (y1 <= 0) {
            updateAll(0);
        } else {
            updateAll(y1);
        }
    }

    public void updateManuever(int x1, int y1) {


    }

    public void updateAll(int val) {
        motor1 = val;
        motor2 = val;
        motor3 = val;
        motor4 = val;
    }

    public String getData() {
        String motorValues = motor1 + "," + motor2 + "," + motor3 + "," + motor4;
        return motorValues;
    }
}
