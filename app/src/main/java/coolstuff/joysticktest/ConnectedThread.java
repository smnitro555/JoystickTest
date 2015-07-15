package coolstuff.joysticktest;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by smnitro on 7/14/2015.
 */
public class ConnectedThread extends Thread{
    private final BluetoothSocket joystickSocket;
    private final InputStream joystickInStream;
    private final OutputStream joystickOutStream;

    public ConnectedThread(BluetoothSocket socket) {
        joystickSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {

        }

        joystickInStream = tmpIn;
        joystickOutStream = tmpOut;

    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = joystickInStream.read(buffer);
                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                break;
            }
        }

    }


}
