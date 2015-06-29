package coolstuff.joysticktest;

/**
 * Created by smnitro on 6/15/2015.
 * Originally created by zerokol
 * Using with github policy
 */

public interface JoystickMovedListener {
    public void OnMoved(int pan, int tilt);
    public void OnReleased();
    public void OnReturnedToCenter();
}
