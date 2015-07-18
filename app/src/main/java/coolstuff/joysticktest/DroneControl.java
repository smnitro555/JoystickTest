package coolstuff.joysticktest;

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


}
