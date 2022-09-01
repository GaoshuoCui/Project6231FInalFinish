package ReplicaHost2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class TimerForSleep implements Runnable {

    DatagramSocket socket;

    public TimerForSleep(DatagramSocket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
            this.socket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
