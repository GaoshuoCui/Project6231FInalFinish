package Sequencer;

import java.io.IOException;
import java.net.*;

import PortInformation.Replica;
import PortInformation.SequencerPort;

public class Sequencer {

    private Integer sequenceNumber;

    public Sequencer() {
        this.sequenceNumber = 0;
    }

    public void listen(int udpPort) throws IOException {
        DatagramSocket socket = new DatagramSocket(udpPort);
        DatagramPacket packet = null;
        byte[] data = null;
        int count = 0;
        while(true)
        {
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            System.out.println("Message " + count +" received:   "+new String(packet.getData(), 0, packet.getLength()));
    
            InetAddress addressFE = InetAddress.getByName("localhost");
            String packageMessage = "Sequencer: request "+"("+count+")"+" received!";
            byte[] datafe = packageMessage.getBytes();
            DatagramPacket sendPacketFE = new DatagramPacket(datafe, datafe.length, addressFE, 1349);
            socket.send(sendPacketFE);  
            String FEIpAddress = packet.getAddress().getHostAddress();
            String receiveMessage = new String(packet.getData(), 0, packet.getLength());
            packetAndSendMessage(FEIpAddress, receiveMessage, socket);
            count++;
            System.out.println("Replica Connected: " + count);
            InetAddress address = packet.getAddress();
            System.out.println("Replica IP:"+address.getHostAddress());

        }
    }

    private void multicastMessage(String packageMessage, DatagramSocket socket) throws IOException {

        //TODO：Modify IP
        InetAddress address = InetAddress.getByName("localhost");

        byte[] data = packageMessage.getBytes();
        DatagramPacket sendPacket1 = new DatagramPacket(data, data.length, address, Replica.REPLICA.replica1);
        DatagramPacket sendPacket2 = new DatagramPacket(data, data.length, address, Replica.REPLICA.replica2);
        DatagramPacket sendPacket3 = new DatagramPacket(data, data.length, address, Replica.REPLICA.replica3);
        DatagramPacket sendPacket4 = new DatagramPacket(data, data.length, address, Replica.REPLICA.replica4);

        socket.send(sendPacket1);
        socket.send(sendPacket2);
        socket.send(sendPacket3);
        socket.send(sendPacket4);
        System.out.println("Multicast finished!");
    }

    private void packetAndSendMessage(String FEIpAddress, String receiveMessage, DatagramSocket socket) throws IOException {
        synchronized (this.sequenceNumber){
            //1.packet the message, give a sequence number, sequence number ++
            String sendMessage = this.sequenceNumber.toString() + ":" + FEIpAddress + ":" + receiveMessage;
            this.sequenceNumber ++;

            //2.multicast the message to replica
            multicastMessage(sendMessage, socket);
        }
    }

    public static void main(String[] args) throws IOException {
        Sequencer sequencer = new Sequencer();
        System.out.println("Sequencer Start!");
        sequencer.listen(SequencerPort.SEQUENCER_PORT.sequencerPort);
    }
}
