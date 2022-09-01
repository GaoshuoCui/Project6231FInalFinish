package testcase;


//import org.junit.Test;

import java.io.IOException;
import java.net.*;

public class TestReplica {


    public static void sendRequest(String city, String msg) throws IOException {
        InetAddress address = InetAddress.getByName("localhost");
        String ms = city + ":" + msg;
        byte[] data = ms.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, 3333);

        DatagramSocket socket = new DatagramSocket(2001);

        socket.send(sendPacket);

        byte[] recvData = new byte[1024];
        DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
        socket.receive(recvPacket);

        String receiveMessage = new String(recvPacket.getData(), 0, recvPacket.getLength());

        System.out.println(receiveMessage);

        socket.close();

//        packageMsgAndSendToFE(socket, msg.getFEHostAddress(), receiveMessage);
    }

    public static void main(String[] args) throws IOException {       
        TestReplica testReplica = new TestReplica();
        testReplica.senario5();
      
    }

    public void senario1() throws IOException {
        sendRequest("MTL","addEvent-MTLM1111-MTLA092520-CONFERENCES-3");
     
        sendRequest("MTL","listEventAvailability-CONFERENCES");
    }

    public void senario2() throws IOException {
       sendRequest("OTW","addEvent-OTWM1111-OTWA082020-TRADESHOWS-5");
        sendRequest("OTW","removeEevent-OTWA082020-TRADESHOWS");
        sendRequest("OTW","listEventAvailability-CONFERENCES");
    }

    public void senario3() throws IOException {
        sendRequest("TOR","addEvent-TORM1111-TORA111119-SEMINARS-2");
        sendRequest("TOR","addEvent-TORM1111-TORA111219-TRADESHOWS-2");
        sendRequest("TOR","addEvent-TORM1111-TORA111319-CONFERENCES-2");
        sendRequest("MTL","bookevent-MTLC1234-TORA111319-CONFERENCES");
        sendRequest("MTL","bookevent-MTLC1234-TORA111219-TRADESHOWS");
        sendRequest("MTL","bookevent-MTLC1234-TORA111119-SEMINARS");
        sendRequest("TOR","listEventAvailability-CONFERENCES");
    }
    public void senario4() throws IOException {
        sendRequest("MTL","addEvent-MTLM1111-MTLA110720-CONFERENCES-2");
        sendRequest("MTL","addEvent-MTLM1111-MTLA110820-CONFERENCES-2");
        sendRequest("MTL","addEvent-MTLM1111-MTLA110920-CONFERENCES-2");
        sendRequest("MTL","addEvent-MTLM1111-MTLA111020-CONFERENCES-2");
        sendRequest("MTL","bookevent-MTLC1234-MTLA110720-CONFERENCES");
        sendRequest("MTL","bookevent-MTLC1234-MTLA110820-CONFERENCES");
        sendRequest("MTL","bookevent-MTLC1234-MTLA110920-CONFERENCES");
        sendRequest("MTL","bookevent-MTLC1234-MTLA110720-CONFERENCES");
        sendRequest("OTW","listCourseAvailability-CONFERENCES");
    }
    public void senario5() throws IOException {
        sendRequest("MTL","addEvent-MTLM1111-MTLA110119-CONFERENCES-3");
        sendRequest("MTL","addEvent-MTLM1111-MTLA110219-CONFERENCES-3");
        sendRequest("MTL","addEvent-MTLM1111-MTLA110319-CONFERENCES-3");
        sendRequest("TOR","addEvent-TORM1111-TORA120420-CONFERENCES-2");
        sendRequest("TOR","addEvent-TORM1111-TORA120520-CONFERENCES-2");
        sendRequest("TOR","addEvent-TORM1111-TORA120620-CONFERENCES-2");
        sendRequest("MTL","bookevent-MTLC1234-MTLA110119-CONFERENCES");
        sendRequest("MTL","swapEvent-MTLC1234-TORA120420-CONFERENCES-MTLA110219-CONFERENCES");
        sendRequest("TOR","listeventAvailability-CONFERENCES");
    }
    public void senario6() throws IOException {
        sendRequest("TOR","addEvent-TORM1111-MTLA070119-CONFERENCES 3");
        sendRequest("TOR","addEvent-TORM1111-MTLA070219-CONFERENCES 3");
        sendRequest("TOR","addEvent-TORM1111-MTLA070319-CONFERENCES 3");
        sendRequest("OTW","addEvent-OTWM1111-OTWA080319-CONFERENCES 3");
        sendRequest("OTW","addEvent-OTWM1111-OTWA080319-CONFERENCES 3");
        sendRequest("OTW","addEvent-OTWM1111-OTWA080319-CONFERENCES 3");
        sendRequest("TOR","bookevent-TORC1234-MTLA070119-CONFERENCES");
        sendRequest("TOR","swapEvent-TORC1234-OTWA080319-CONFERENCES-MTLA070119-CONFERENCES");
        sendRequest("OTW","listeventAvailability-CONFERENCES");
    }
    public void senario7() throws IOException {
        sendRequest("MTL","addEvent-MTLM1111-MTLA090119-CONFERENCES-3");
        sendRequest("MTL","addEvent-MTLM1111-MTLA090219-CONFERENCES-3");
        sendRequest("MTL","addEvent-MTLM1111-MTLA090319-CONFERENCES-3");
        sendRequest("OTW","addEvent-OTWM1111-OTWA080319-CONFERENCES-3");
        sendRequest("OTW","addEvent-OTWM1111-OTWA080419-CONFERENCES-3");
        sendRequest("OTW","addEvent-OTWM1111-OTWA080519-CONFERENCES-3");
        sendRequest("TOR","bookevent-TORC1234-MTLA090119-CONFERENCES");
        sendRequest("TOR","swapEvent-TORC1234-OTWA080319-CONFERENCES-MTLA090119-CONFERENCES");
        sendRequest("OTW","listeventAvailability-CONFERENCES");
    }
    public void senario8() throws IOException {
        sendRequest("MTL","addEvent-MTLM1111-MTLA090119-CONFERENCES-3");
        sendRequest("MTL","addEvent-MTLM1111-MTLA090219-CONFERENCES-3");
        sendRequest("MTL","addEvent-MTLM1111-MTLA090319-CONFERENCES-3");
        sendRequest("OTW","addEvent-OTWM1111-OTWA080319-CONFERENCES-3");
        sendRequest("OTW","addEvent-OTWM1111-OTWA080419-CONFERENCES-3");
        sendRequest("OTW","addEvent-OTWM1111-OTWA080519-CONFERENCES-3");
        sendRequest("MTL","bookevent-MTLC1234-MTLA090119-CONFERENCES");
        sendRequest("MTL","swapEvent-MTLC1234-OTWA080319-CONFERENCES-MTLA090119-CONFERENCES");
        sendRequest("OTW","listeventAvailability-CONFERENCES");
    }
    public void senario9() throws IOException {
        sendRequest("MTL","addEvent-MTLM1111-MTLA090119-CONFERENCES-3");
        sendRequest("MTL","addEvent-MTLM1111-MTLA090219-CONFERENCES-3");
        sendRequest("OTW","addEvent-OTWM1111-OTWA100119-CONFERENCES-3");
        sendRequest("TOR","addEvent-TORM1111-TORA110119-CONFERENCES-3");
        sendRequest("TOR","bookevent-TORC4444-MTLA090119-CONFERENCES");
        sendRequest("TOR","bookevent-TORC4444-MTLA090219-CONFERENCES");
        sendRequest("TOR","bookevent-TORC5555-OTWA100119-CONFERENCES");
        sendRequest("TOR","bookevent-TORC5555-TORA110119-CONFERENCES");

        sendRequest("TOR","listeventAvailability-CONFERENCES");
        sendRequest("TOR","bookevent-TORC1111-TORA110119-CONFERENCES");
        sendRequest("TOR","bookevent-TORC1111-MTLA090119-CONFERENCES");
        sendRequest("TOR","bookevent-TORC1111-OTWA100119-CONFERENCES");
        sendRequest("TOR","bookevent-TORC1111-MTLA090219-CONFERENCES");
        sendRequest("OTW","bookevent-OTWC1111-MTLA090119-CONFERENCES");
        sendRequest("TOR","getEventSchedule-TORC1111");
        sendRequest("OTW","removeEvent-OTWM1111-OTWA100119-CONFERENCES");
        sendRequest("TOR","listeventAvailability-CONFERENCES");
        sendRequest("TOR","getEventSchedule-TORC1111");
    }
}
