package FrontEnd;

import org.omg.CORBA.ORB;

import FECorba.FrontEndPOA;
import PortInformation.AddressInfo;
import PortInformation.FEPort;
import PortInformation.Replica;
import PortInformation.SequencerPort;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class FrontEndImpl extends FrontEndPOA {

    private ORB orb;
    private Map<String, Integer> softwareFailCounter;
	private String managerId="*";
	private String eventId="*";
	private String eventtype="*";
	private String capacity="*";
	private String customerID="*";
	private String neweventId="*";
	private String neweventtype="*";
	private String oldeventId="*";
	private String oldeventtype="*";
	public static String msgQueue;
	public static HashMap<Integer,String> msgSendQueue = new HashMap<Integer,String>();
	public static int count = 0;		
	public static boolean timeo = false; // For pack lose simulation.
	public static boolean resend = false; // For resend simulation.
    public FrontEndImpl(){
        softwareFailCounter = new HashMap<String,Integer>();
        softwareFailCounter.put("1",0);
        softwareFailCounter.put("2",0);
        softwareFailCounter.put("3",0);
        softwareFailCounter.put("4",0);
    }

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }
    
    public void SafeSend(DatagramSocket socket,String Req) throws Exception {
    	

    	Timer timer = new Timer(socket, timeo);
    	Thread thread = new Thread(timer);
        thread.start();
        if(timer.timeout ){
        	System.out.println("Times out, Resend Now.");
        	sendRequest(Req);//Simulation resend
        }else {
        	sendRequest(Req);
        }
        msgSendQueue.put(count,Req);
        int validate = listenSq(1349);
        if(resend) {
        	System.out.println("Resend Now.");
        	String req2 = msgSendQueue.get(validate);
        	sendRequest(req2);
        	}
        
    }
    

    @SuppressWarnings("static-access")
	public static int listenSq(int udpPort) throws IOException {
        DatagramSocket socket = new DatagramSocket(udpPort);
        DatagramPacket packet = null;
        byte[] data = null;
        while(true)
        {
        	
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            String re = new String(packet.getData(), 0, packet.getLength());
            System.out.println(re);
            String varifyInt = re.substring(re.indexOf('(')+1,re.indexOf('(')+2);
            int vint = Integer.parseInt(varifyInt);
            
            if(msgQueue.valueOf(vint).length()>0) {
            	
            }else {
            	System.out.println("   Message "+ count + " is lose!   ");
            	System.out.println("Resend"+" Message "+ vint);
            	socket.close();
            	return vint;
            }
            count++;
            socket.close();
            return -1;
        }
        
    }


	public String addEvent(String managerId, String eventId, String eventtype, int capacity) {
		 synchronized (this){
			 //System.out.println("addEvent in FE has been called");
	            String city = managerId.substring(0,3);
	            Map<String, String> resultSet = new HashMap<>();
	            DatagramSocket socket = null;
	            int count = 0;
	            StringBuilder sb = new StringBuilder();

	            try {
	                socket = new DatagramSocket(FEPort.FE_PORT.FEPort);
	               String Req = sb.append(city)
	                        .append(":")
	                        .append("addEvent")
	                        .append("-")
	                        .append(managerId)
	                        .append("-")
	                        .append(eventId)
	                        .append("-")
	                        .append(eventtype)
	                        .append("-")
	                        .append(Integer.toString(capacity))
	                        .append("-")
	                        .append(customerID)
	                        .append("-")
	                        .append(neweventId)
	                        .append("-")
	                        .append(neweventtype)
	                        .append("-")
	                        .append(oldeventId)
	                        .append("-")
	                        .append(oldeventtype).toString();
	                
	                SafeSend(socket,Req);

	                Timer timer = new Timer(socket, false);
	                Thread thread = new Thread(timer);
	                thread.start();
	                //TODO:Assume one failure happened
	                while(count < 4 && !timer.timeout) {
	                    count = registerListener(socket, resultSet);
	                }
	            } catch (Exception e) {
//	            e.printStackTrace();
	            } finally {
	                socket.close();
	            }


	            if (resultSet.size() < 4){
	                tellRMCrash(resultSet);
	            }
	           //System.out.println("FrontEndImpl: " + majority(resultSet));
	            return majority(resultSet);
	        }
	}


	public String removeEvent(String managerId, String eventId, String eventtype) {
		synchronized (this){
			String city = managerId.substring(0,3);
            Map<String, String> resultSet = new HashMap<>();
            DatagramSocket socket = null;
            int count = 0;
            StringBuilder sb = new StringBuilder();

            try {
                socket = new DatagramSocket(FEPort.FE_PORT.FEPort);

                String Req = (sb.append(city)
                        .append(":")
                        .append("removeEvent")
                        .append("-")
                        .append(managerId)
                        .append("-")
                        .append(eventId)
                        .append("-")
                        .append(eventtype)
                        .append("-")
                        .append(capacity)
                        .append("-")
                        .append(customerID)
                        .append("-")
                        .append(neweventId)
                        .append("-")
                        .append(neweventtype)
                        .append("-")
                        .append(oldeventId)
                        .append("-")
                        .append(oldeventtype).toString());
                SafeSend(socket,Req);
                Timer timer = new Timer(socket,false);
                Thread thread = new Thread(timer);
                thread.start();
                while(count < 4 && !timer.timeout) {
                    count = registerListener(socket, resultSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }


            if (resultSet.size() < 4){
                tellRMCrash(resultSet);
            }
            return majority(resultSet);
        }
	}

	@Override
	public String[] listEventAvailability(String managerId, String eventtype) {
		synchronized (this){

			String city = managerId.substring(0,3);
            Map<String, String> resultSet = new HashMap<>();
            DatagramSocket socket = null;
            int count = 0;
            StringBuilder sb = new StringBuilder();

            try {
                socket = new DatagramSocket(FEPort.FE_PORT.FEPort);
                String Req = (sb.append(city)
                        .append(":")
                        .append("listEventAvailability")
                        .append("-")
                        .append(managerId)
                        .append("-")
                        .append(eventId)
                        .append("-")
                        .append(eventtype)
                        .append("-")
                        .append(capacity)
                        .append("-")
                        .append(customerID)
                        .append("-")
                        .append(neweventId)
                        .append("-")
                        .append(neweventtype)
                        .append("-")
                        .append(oldeventId)
                        .append("-")
                        .append(oldeventtype).toString());
                SafeSend(socket,Req);
                Timer timer = new Timer(socket,false);
                Thread thread = new Thread(timer);
                thread.start();
                while(count < 4 && !timer.timeout) {
                    count = registerListener(socket, resultSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }

            if (resultSet.size() < 4){
                tellRMCrash(resultSet);
            }
            return majority(resultSet).split(" ");
        }
	}

	@Override
	public String bookevent(String customerId, String eventId, String eventtype) {
		  synchronized (this){
			  String city = customerId.substring(0,3);
	            Map<String, String> resultSet = new HashMap<>();
	            DatagramSocket socket = null;
	            int count = 0;
	            StringBuilder sb = new StringBuilder();

	            try{
	                socket = new DatagramSocket(FEPort.FE_PORT.FEPort);
	                String Req = (sb.append(city)
	                        .append(":")
	                        .append("bookevent")
	                        .append("-")
	                        .append(managerId)
	                        .append("-")
	                        .append(eventId)
	                        .append("-")
	                        .append(eventtype)
	                        .append("-")
	                        .append(capacity)
	                        .append("-")
	                        .append(customerId)
	                        .append("-")
	                        .append(neweventId)
	                        .append("-")
	                        .append(neweventtype)
	                        .append("-")
	                        .append(oldeventId)
	                        .append("-")
	                        .append(oldeventtype).toString());
	                SafeSend(socket,Req);
	                Timer timer = new Timer(socket,false);
	                Thread thread = new Thread(timer);
	                thread.start();
	                while(count < 4 && !timer.timeout) {
	                    count = registerListener(socket, resultSet);
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                socket.close();
	            }
	            if (resultSet.size() < 4){
	                tellRMCrash(resultSet);
	            }
	            return majority(resultSet);
	        }
	}

	public String dropevent(String customerId, String eventId) {
		synchronized (this){
			String city = customerId.substring(0,3);
            Map<String, String> resultSet = new HashMap<>();
            DatagramSocket socket = null;
            int count = 0;
            StringBuilder sb = new StringBuilder();

            try{
                socket = new DatagramSocket(FEPort.FE_PORT.FEPort);
                String Req = (sb.append(city)
                        .append(":")
                        .append("dropevent")
                        .append("-")
                        .append(managerId)
                        .append("-")
                        .append(eventId)
                        .append("-")
                        .append(eventtype)
                        .append("-")
                        .append(capacity)
                        .append("-")
                        .append(customerId)
                        .append("-")
                        .append(neweventId)
                        .append("-")
                        .append(neweventtype)
                        .append("-")
                        .append(oldeventId)
                        .append("-")
                        .append(oldeventtype).toString());
                SafeSend(socket,Req);
                Timer timer = new Timer(socket,false);
                Thread thread = new Thread(timer);
                thread.start();
                while(count < 4 && !timer.timeout) {
                    count = registerListener(socket, resultSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }

            if (resultSet.size() < 4){
                tellRMCrash(resultSet);
            }
            return majority(resultSet);
        }
	}

	public String[] getbookingSchedule(String customerId) {
		synchronized (this){
			


            int count = 0;
 
			String city = customerId.substring(0,3);
            DatagramSocket socket = null;
            StringBuilder sb = new StringBuilder();
            
            Map<String, String> resultSet = new HashMap<>();

            try{
                socket = new DatagramSocket(FEPort.FE_PORT.FEPort);
                String Req = (sb.append(city)
                        .append(":")
                        .append("getbookingSchedule")
                        .append("-")
                        .append(managerId)
                        .append("-")
                        .append(eventId)
                        .append("-")
                        .append(eventtype)
                        .append("-")
                        .append(capacity)
                        .append("-")
                        .append(customerId)
                        .append("-")
                        .append(neweventId)
                        .append("-")
                        .append(neweventtype)
                        .append("-")
                        .append(oldeventId)
                        .append("-")
                        .append(oldeventtype).toString());
                SafeSend(socket,Req);
                Timer timer = new Timer(socket,false);
                Thread thread = new Thread(timer);
                thread.start();
                
                
            while(count < 4 && !timer.timeout) {
                    count = registerListener(socket, resultSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }

            if (resultSet.size() < 4){
                tellRMCrash(resultSet);
            }
            return majority(resultSet).split(",");}
            
            /*
                
                resultString = registerListener2(socket);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }
            System.out.println("BS FrontEndImpl String: " + resultString);
            String[] arrayResult = {"Happy",resultString};
            System.out.println("BS FrontEndImpl: " + arrayResult[1]);
            return arrayResult;
        }*/
                
	}

	@Override
	public String swapEvent(String customerId, String neweventId, String neweventtype, String oldeventId,
			String oldeventtype) {
		 synchronized (this){
			 	String city = customerId.substring(0,3);
	            Map<String, String> resultSet = new HashMap<>();
	            DatagramSocket socket = null;
	            int count = 0;
	            StringBuilder sb = new StringBuilder();

	            try{
	                socket = new DatagramSocket(FEPort.FE_PORT.FEPort);
	                String Req = (sb.append(city)
	                        .append(":")
	                        .append("swapEvent")
	                        .append("-")
	                        .append(managerId)
	                        .append("-")
	                        .append(eventId)
	                        .append("-")
	                        .append(eventtype)
	                        .append("-")
	                        .append(capacity)
	                        .append("-")
	                        .append(customerId)
	                        .append("-")
	                        .append(neweventId)
	                        .append("-")
	                        .append(neweventtype)
	                        .append("-")
	                        .append(oldeventId)
	                        .append("-")
	                        .append(oldeventtype).toString());
	                SafeSend(socket,Req);
	                Timer timer = new Timer(socket,false);
	                Thread thread = new Thread(timer);
	                thread.start();
	                while(count < 4 && !timer.timeout) {
	                    count = registerListener(socket, resultSet);
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                socket.close();
	            }

	            if (resultSet.size() < 4){
	                tellRMCrash(resultSet);
	            }
	            return majority(resultSet);
	        }
	}

    @Override
    public void shutdown() {

    }

    private void tellRMCrash(Map<String, String> resultSet) {
        if (!resultSet.containsKey("1")) {
            String msg = "1 " + Failure.ServerCrash;
            sendReq(msg);
        } else if (!resultSet.containsKey("2")) {
            String msg = "2 " + Failure.ServerCrash;
            sendReq(msg);
        } else if (!resultSet.containsKey("3")) {
            String msg = "3 " + Failure.ServerCrash;
            sendReq(msg);
        } else if (!resultSet.containsKey("4")) {
            String msg = "4 " + Failure.ServerCrash;
            sendReq(msg);
        }
    }

    private DatagramPacket packet(String rmAddress, byte[] data, int replica) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(rmAddress);
        return new DatagramPacket(data,0, data.length, address, replica);
    }

    private void sendReq(String msg) {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] data = msg.getBytes();
            multicastCrashMsg(socket,data);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void multicastCrashMsg(DatagramSocket socket, byte[] data){
        try {
            socket.send(packet(AddressInfo.ADDRESS_INFO.RM1address, data,Replica.REPLICA.replica1));
            socket.send(packet(AddressInfo.ADDRESS_INFO.RM2address, data,Replica.REPLICA.replica2));
            socket.send(packet(AddressInfo.ADDRESS_INFO.RM3address, data,Replica.REPLICA.replica3));
            socket.send(packet(AddressInfo.ADDRESS_INFO.RM4address, data,Replica.REPLICA.replica4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String majority(Map<String,String> resultSet) {
    	//System.out.println("Candidate: "+resultSet);
        Map<String,Integer> map = new HashMap<>();
        resultSet.forEach((k,v) -> {
            if (map.containsKey(v)){
                map.put(v, map.get(v) + 1);
            } else {
                map.put(v, 1);
            }
        });

        Integer vote = 0;
        String candidate = "";
        for (Map.Entry<String, Integer> entry :
                map.entrySet()) {
            if (entry.getValue() > vote){
                candidate = entry.getKey();
                vote = entry.getValue();
            }
        }

        findSoftwareFail(candidate, vote, resultSet);
        //System.out.println("Candidate: "+candidate);
        return candidate;
    }

    private void findSoftwareFail(String candidate, Integer vote, Map<String, String> resultSet) {
        if (vote == 4)
            return;
        String failServerNum = null;
        for (Map.Entry<String, String> entry : resultSet.entrySet()){
            if (!entry.getValue().equals(candidate)){
                failServerNum = entry.getKey();
            }
        }
        if (null != failServerNum){
            for (Map.Entry<String,Integer> entry:
                 softwareFailCounter.entrySet()) {
                if (!entry.getKey().equals(failServerNum)){
                    entry.setValue(0);
                } else if(entry.getKey().equals(failServerNum)){
                    entry.setValue(entry.getValue() + 1);
                }
            }
        }
        if (softwareFailCounter.get(failServerNum) != null && softwareFailCounter.get(failServerNum) == 3){
            sendToRM(failServerNum);
        }
    }

    private void sendToRM(String crashServerNum) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            String msg = Failure.SoftWareFailure.toString();

            byte[] data = msg.getBytes();

            if (crashServerNum.equals("1")){
                InetAddress address = InetAddress.getByName(AddressInfo.ADDRESS_INFO.RM1address);
                DatagramPacket packet = new DatagramPacket(data, 0, data.length,address,6001 );
                socket.send(packet);
            } else if (crashServerNum.equals("2")){
                InetAddress address = InetAddress.getByName(AddressInfo.ADDRESS_INFO.RM2address);
                DatagramPacket packet = new DatagramPacket(data, 0, data.length,address,6002);
                socket.send(packet);
            } else if (crashServerNum.equals("3")){
                InetAddress address = InetAddress.getByName(AddressInfo.ADDRESS_INFO.RM3address);
                DatagramPacket packet = new DatagramPacket(data, 0, data.length,address,6003 );
                socket.send(packet);
            } else {
                InetAddress address = InetAddress.getByName(AddressInfo.ADDRESS_INFO.RM4address);
                DatagramPacket packet = new DatagramPacket(data, 0, data.length,address,6004 );
                socket.send(packet);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        socket.close();
    }



    public void sendRequest(String message) throws Exception {

        //TODO:Sequener Ip;
        InetAddress address = InetAddress.getByName("localhost");

        byte[] data = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, SequencerPort.SEQUENCER_PORT.sequencerPort);

        DatagramSocket socket = new DatagramSocket();
        socket.send(sendPacket);

    }

    private String registerListener2(DatagramSocket socket) {
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        String result ="";
        try {
            socket.receive(packet);
            result = new String(packet.getData(), 0 , packet.getLength());

            System.out.println("receive BookingSchedule: " + result);


        } catch (SocketException e){

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private int registerListener(DatagramSocket socket,  Map<String,String> resultSet) {
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
        	//System.out.println("Wating...");
            socket.receive(packet);
            //System.out.println("Message received!");
            //String result = new String(packet.getData(), 0 , packet.getLength());


			   byte[] d=packet.getData();
			   int dlen = packet.getLength();
			   String result = new String(d,0,dlen,"UTF-8");

            System.out.println("Receive from Replica" + result);
            System.out.println();
           // 1: {CONFERENCES=[TORA010119]}
            String[] res = result.split(":");
            resultSet.put(res[0], res[1]);

        } catch (SocketException e){

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultSet.size();
    }

    public void listen(int udpPort) throws IOException {
        DatagramSocket socket = new DatagramSocket(udpPort);
        DatagramPacket packet = null;
        byte[] data = null;

        while(true)
        {
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);

            String receiveMessage = new String(packet.getData(), 0, packet.getLength());

        }
    }

	

}

enum Failure {
    SoftWareFailure,
    ServerCrash,
    BackUp,
}
