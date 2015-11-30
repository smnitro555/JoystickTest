package coolstuff.joysticktest;

import android.util.Log;

/**
 * Created by Siva on 8/21/2015.
 * Used to Calculate Values from Joystick to Motor Values
 */
public class DroneVal {
    int motor1 = 0;
    int motor2 = 0;
    int motor3 = 0;
    int motor4 = 0;
    int trimSensitivity = 50;
    double trim1 = 1.0;
    double trim2 = 1.0;
    double trim3 = 1.0;
    double trim4 = 1.0;

    public DroneVal() {
        //Nothing needs to happen
    }

    public void updatePowerVals(double x1, double y1) {
        if (y1 <= 0) {
            updateAll(0);
        } else {
            updateAll(y1);
        }
    }

    public void updateManuever(double x1, double y1) {


    }

    public void updateAll(double val) {
        motor1 = (int) ((val*600) + 1200);
        motor2 = (int) ((val*600) + 1200);
        motor3 = (int) ((val*600) + 1200);
        motor4 = (int) ((val*600) + 1200);
        String temp = "" + val;
        Log.e("val", temp);
    }

    public String getData() {
        String temp = motor1 + "," + motor2 + "," + motor3 + "," + motor4 + "n";
        Log.e("Motor Values", temp);
        return temp;
    }

    public void setTrim(int progress, int trimNumber, int maxBounds) {
        if (trimNumber == 1) {
            trim1 = (progress-(maxBounds/2))/trimSensitivity;
        } else if (trimNumber == 2) {
            trim2 = (progress-(maxBounds/2))/trimSensitivity;
        } else if (trimNumber == 3) {
            trim3 = (progress-(maxBounds/2))/trimSensitivity;
        } else {
            trim4 = (progress-(maxBounds/2))/trimSensitivity;
        }
    }
}
