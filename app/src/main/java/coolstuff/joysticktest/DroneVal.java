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
    int trimSensitivity = 50;
    double trim1 = 1.0;
    double trim2 = 1.0;
    double trim3 = 1.0;
    double trim4 = 1.0;

    public DroneVal() {
        //Nothing needs to happen
    }

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
        return (((motor1*800)+1200) + "," + ((motor2*800)+1200) + "," + ((motor3*800)+1200) + "," + ((motor4*800)+1200) + "n");
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
