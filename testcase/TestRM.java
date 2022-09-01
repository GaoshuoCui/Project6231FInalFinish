package testcase;

import PortInformation.FEPort;
import PortInformation.SequencerPort;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TestRM {

    public static void sendRequest(String seqId, String address, String city, String msg) throws IOException {
        InetAddress add = InetAddress.getByName("localhost");
        String ms = seqId + ":" + address + ":" + city + ":" + msg;
        byte[] data = ms.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, add, 6004);

        DatagramSocket socket = new DatagramSocket(SequencerPort.SEQUENCER_PORT.sequencerPort);
        DatagramSocket recvSocket = new DatagramSocket(FEPort.FE_PORT.FEPort);

        socket.send(sendPacket);

        byte[] recvData = new byte[1024];
        DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
        recvSocket.receive(recvPacket);

        String receiveMessage = new String(recvPacket.getData(), 0, recvPacket.getLength());

        System.out.println(receiveMessage);

        socket.close();
        recvSocket.close();

//        packageMsgAndSendToFE(socket, msg.getFEHostAddress(), receiveMessage);
    }

    public static void main(String[] args) throws IOException {

        sendRequest("3","localhost","MTL","listEventAvailability-CONFERENCES");
    }
}
