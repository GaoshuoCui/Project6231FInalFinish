package ReplicaHost1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.omg.CORBA.Any;

import ReplicaHost1RemoteObject.EventSystemImplementation;

import java.util.AbstractMap.SimpleEntry;



public class BackupThread implements Runnable {
    DatagramPacket packet;
    DatagramSocket socket;
    EventSystemImplementation city;

    public BackupThread(DatagramSocket socket, DatagramPacket packet , EventSystemImplementation city) {
        this.city = city;
        this.packet = packet;
        this.socket = socket;
    }

    @Override
    public void run() {
        String message = new String(packet.getData(), 0 , packet.getLength());
        String[] ms = message.split(":");
        String info = ms[1];

        try {

            String[] command = info.split("-");
            //String[] command = {"addEvent","MTLM0001","MTLA070819","Conferences","3","MTLC0001"};
            /**
            sendRequest(sb.append(City)
                    .append(":")
                command[0]
                    .append("addEvent")
                command[1]
                	ManagerID
                command[2]
 					EventId
                command[3] 
                	event type;
               	command[4]
               		event compacity;
               	command[5]
               		customerID;
               	command[6]
               		new eventID;
               	command[7]
               		new eventtype;
               	command[8]
               		old eventID;
               	command[9]
               		old eventtype;               		
             */
            String result = "";
        	 switch(command[0]) {
             case "addEvent" :
                 result = city.addEvent(command[1],command[2], command[3], Integer.parseInt(command[4]));
                 if (result.contains("true")) {
                 	 result = "true: Add Event Complete!";
					}else {
                 result = "false: Add Event fail!";}
                 break;
             case "removeEvent" :
                 result = city.removeEvent(command[1],command[2],command[3]);
                 if (result.contains("true")) {
                	 result = "true: Remove Event Complete!";
					}else {
                result = "false: Remove Event fail!";}
                 break;
             case "listEventAvailability" :
             	String[] res = city.listEventAvailability(command[1],command[3]);
                 result = res.toString();
                 break;
             case "bookevent" :
             	result = city.bookevent(command[5], command[2], command[3]);
                 break;
             case "dropevent" :
                 result = city.dropevent(command[5], command[2]);
                 break;
             case "getbookingSchedule" :
            	 String[] result3 = city.getbookingSchedule(command[5]);
             	StringBuilder r1 = new StringBuilder();
                 Arrays.stream(result3).forEach(record -> r1.append(record).append(" "));
                 result = r1.toString().trim();
                 break;
             case "swapEvent" :
                 result = city.swapEvent(command[5],command[6],command[7],command[8],command[9]);
                 break;
             default :
                 System.out.println("Invalid Command!");
         }

            byte[] ack = result.getBytes();
            DatagramPacket packet = new DatagramPacket(ack, 0, ack.length, this.packet.getAddress(), 5001);
            socket.send(packet);

        }  catch (Exception e) {
            e.printStackTrace();
        }


        //socket.close(); Not close here.
    }

}
