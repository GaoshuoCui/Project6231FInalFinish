package ReplicaHost4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ReplicaHost4RemoteObject.RMImpl;
import ReplicaHost4RemoteObject.EventSystemSTHWrong;

import java.util.AbstractMap.SimpleEntry;


public class MainThread implements Runnable {

    DatagramPacket packet;
    DatagramSocket socket;
    RMImpl city;
    EventSystemSTHWrong cityWrong;
    boolean bug;
    int CrashTime;

    public MainThread(DatagramSocket socket, DatagramPacket packet , RMImpl city, 
    		EventSystemSTHWrong cityWrong, boolean bug, int CrashTime) {
        this.city = city;
        this.packet = packet;
        this.socket = socket;
        this.cityWrong = cityWrong;
        this.bug = bug;
        this.CrashTime=CrashTime;
    }

    @Override
    public void run() {
        InetAddress address = null;
        String message = new String(packet.getData(), 0 , packet.getLength());
        String info = null;
        if (!message.equals("Hi")){
            String[] ms = message.split(":");
            info = ms[1];
        } else {
            info = "Hi";
        }

        int port = 8800;
        byte[] data2 = null;
        DatagramPacket packet2 = null;

        try {

            String[] command = info.split("-");
            //test command---------------------------------------------------------------
           //String[] command = {"addEvent","MTLM0001","MTLA070819","Conferences","3","MTLC0001"
            		//,"MTLA070819","Conferences","MTLA010819","Conferences"};
            String result = "";
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
            
            if(CrashTime>0) {
            	String result1;
            	String result2;
                String resultb;
				switch(command[0]) {
                    case "addEvent" :
                        result1 = city.addEvent(command[1],command[2], command[3], Integer.parseInt(command[4]));
                        if (result1.contains("true")) {
                        	 result = "true: Add Event Complete!";
       					}else {
                        result = "false: Add Event fail!";}
                        Thread.sleep(CrashTime);
                        break;
                    case "removeEvent" :
                         result1 = city.removeEvent(command[1],command[2],command[3]);
                         Thread.sleep(CrashTime);
                        if (result1.contains("true")) {
                       	 result = "true: Remove Event Complete!";
						}else {
                       result = "false: Remove Event fail!";}
                        break;
                    case "listEventAvailability" :
                    	String[] res = city.listEventAvailability(command[1],command[3]);
                    	 StringBuilder r = new StringBuilder();
                         Arrays.stream(res).forEach(record -> r.append(record).append(" "));
                         result = r.toString().trim();
                         Thread.sleep(CrashTime);

                        break;
                    case "bookevent" :
                    	result = city.bookevent(command[5], command[2], command[3]);
                        Thread.sleep(CrashTime);
                        break;
                    case "dropevent" :
                        result = city.dropevent(command[5], command[2]);
                        Thread.sleep(CrashTime);
                        break;
                    case "getbookingSchedule" :
                    	String[] result3 = city.getbookingSchedule(command[5]);
                    	StringBuilder r1 = new StringBuilder();
                        Arrays.stream(result3).forEach(record -> r1.append(record).append(" "));
                        result = r1.toString().trim();
                        Thread.sleep(CrashTime);
                        
                        break;
                    case "swapEvent" :
                        result2 = city.swapEvent(command[5],command[6],command[7],command[8],command[9]);
                        Thread.sleep(CrashTime);
                        result = result2.toString();
                        break;
                    case "Hi" :
                        ReplyEcho(this.packet);
                        break;
                    default :
                        System.out.println("Invalid Command!");
                }
            }
            else if (!bug){
                switch(command[0]) {
	                case "addEvent" :
	                    result = cityWrong.addEvent(command[1],command[2]);
	                    String ans=city.addEvent(command[1],command[2], command[3], Integer.parseInt(command[4]));
	                    
	                    //city.udpToFE(ans, "addEvent");
	                    break;

                    case "removeEvent" :
                        result = cityWrong.removeEvent(command[1],command[2]);
                        city.removeEvent(command[1],command[2],command[3]);
                        break;
                    case "listEventAvailability" :
                        city.listEventAvailability(command[1],command[3]);
                        String[] res = cityWrong.listEventAvailability(command[1]);
                        StringBuilder r = new StringBuilder();
                        Arrays.stream(res).forEach(record -> r.append(record).append(" "));
                        result = r.toString().trim();
                        //---------------------------------------------------------------------------
                        //city.udpToFE(result, "listEventAvailability");
                        break;

                    case "bookevent" :
                        city.bookevent(command[5], command[2], command[3]);
                        result = cityWrong.bookevent(command[1], command[2], command[3]);
                        break;
                    case "dropevent" :
                        city.dropevent(command[5], command[2]);
                        result = cityWrong.dropevent(command[1], command[2]);
                        break;
                    case "getbookingSchedule" :
                        StringBuilder sb = new StringBuilder();
                        city.getbookingSchedule(command[5]);
                        Arrays.stream(cityWrong.getbookingSchedule(command[1])).forEach(record -> sb.append(record).append(" "));
                        result = sb.toString().trim();
                        break;
                    case "swapEvent" :
                        city.swapEvent(command[5],command[6],command[7],command[8],command[9]);
                        result = cityWrong.swapEvent(command[1],command[2],command[3]);
                        break;
                    case "Hi" :
                        ReplyEcho(this.packet);
                        break;
                    default :
                        System.out.println("Invalid Command!");
                }
            }else {
            	String result1;
            	String result2;
                String resultb;
				switch(command[0]) {
                    case "addEvent" :
                        result1 = city.addEvent(command[1],command[2], command[3], Integer.parseInt(command[4]));
                        if (result1.contains("true")) {
                        	 result = "true: Add Event Complete!";
       					}else {
                        result = "false: Add Event fail!";}
                        Thread.sleep(CrashTime);
                        break;
                    case "removeEvent" :
                         result1 = city.removeEvent(command[1],command[2],command[3]);
                        if (result1.contains("true")) {
                       	 result = "true: Remove Event Complete!";
						}else {
                       result = "false: Remove Event fail!";}
                        break;
                    case "listEventAvailability" :
                    	String[] res = city.listEventAvailability(command[1],command[3]);
                    	 StringBuilder r = new StringBuilder();
                         Arrays.stream(res).forEach(record -> r.append(record).append(" "));
                         result = r.toString().trim();

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
                        result2 = city.swapEvent(command[5],command[6],command[7],command[8],command[9]);
                        result = result2.toString();
                        break;
                    case "Hi" :
                        ReplyEcho(this.packet);
                        break;
                    default :
                        System.out.println("Invalid Command!");
                }
            }


            address = packet.getAddress();
            port = packet.getPort();

            data2 = result.getBytes();
            packet2 = new DatagramPacket(data2, data2.length, address, port);
            socket.send(packet2);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //socket.close();----
    }

    private void ReplyEcho(DatagramPacket packet) {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] reply = "Reply Hi".getBytes();
            DatagramPacket packet1 = new DatagramPacket(reply, 0, reply.length, packet.getAddress(), packet.getPort());
            socket.send(packet1);
            socket.close();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
