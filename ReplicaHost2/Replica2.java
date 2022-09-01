package ReplicaHost2;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;

import ReplicaHost2RemoteObject.EventSystemImplementation;
import ReplicaHost2RemoteObject.EventSystemSTHWrong;

import functions.Constants;
import logTool.allLogger;



public class Replica2 {

    public boolean bugFree;
    public Logger logger;
    public EventSystemImplementation MTLServer;
    public EventSystemImplementation OTWServer;
    public EventSystemImplementation TORServer;
    public EventSystemSTHWrong MTLWrong;
    public EventSystemSTHWrong OTWWrong;
    public EventSystemSTHWrong TORWrong;

    public Replica2(Logger logger , EventSystemImplementation MTLServer, EventSystemImplementation OTWServer, EventSystemImplementation TORServer, 
    		EventSystemSTHWrong MTLWrong, EventSystemSTHWrong OTWWrong, EventSystemSTHWrong TORWrong){
        this.logger = logger;
        this.MTLServer = MTLServer;
        this.OTWServer = OTWServer;
        this.TORServer = TORServer;
        this.MTLWrong = MTLWrong;
        this.OTWWrong = OTWWrong;
        this.TORWrong = TORWrong;
        this.bugFree = true;
    }

    private void reRunReplica(int RepBackPort, int rmBackPort){

        try {
            InetAddress address = InetAddress.getByName("localhost");
            DatagramSocket socket = new DatagramSocket(RepBackPort);
            byte[] data = FailureEnum.BackUp.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(data,0 ,data.length,address, rmBackPort);
            socket.send(packet);

            while (true){
                byte[] buffer = new byte[1024];
                DatagramPacket packet1 = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet1);
                String msg = new String(packet1.getData(), 0 , packet1.getLength());
                String city = msg.split(":")[0];

                Thread thread = new Thread(new BackupThread(socket, packet1, getCity(city)));
                thread.start();
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReplica(int replica4Port) throws IOException {
        DatagramSocket socket = new DatagramSocket(replica4Port);
        DatagramPacket packet = null;
        byte[] data = null;
        logger.info(" Replica2 Server Start");
        while(true)
        {
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            logger.info("Replica2 Recv Message :" + new String(packet.getData(), 0, packet.getLength()));

            String recvMsg = new String(packet.getData(), 0 , packet.getLength());
            String city = recvMsg.split(":")[0];

            Thread thread = new Thread(new MainThread(socket, packet, getCity(city), 
            		getWrongCity(city), bugFree));
            thread.start();
        }
    }

    private EventSystemImplementation getCity(String city) {
        if (city.equals("MTL"))
            return this.MTLServer;
        else if(city.equals("OTW"))
            return this.OTWServer;
        else
            return this.TORServer;
    }

    private EventSystemSTHWrong getWrongCity(String city) {
        if (city.equals("MTL"))
            return this.MTLWrong;
        else if(city.equals("OTW"))
            return this.OTWWrong;
        else
            return this.TORWrong;
    }

    public static void configLogger(String city , Logger logger) throws IOException {
        logger.setLevel(Level.ALL);
        FileHandler MTLFileHandler = new FileHandler(city + ".log");
        MTLFileHandler.setFormatter(new LoggerFormatter());
        logger.addHandler(MTLFileHandler);
    }


    public void startSoftFailPort(int port) throws IOException {
        DatagramSocket socket = new DatagramSocket(port);
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        while (true){
            socket.receive(packet);
            this.bugFree = true;
        }
    }
    
   
    public static void main(String[] args) throws IOException {
    	
        Logger MTLLogger = Logger.getLogger("MTL.server2.log");
        configLogger("MTLserver2",MTLLogger);

        Logger OTWLogger = Logger.getLogger("OTW.server2.log");
        configLogger("OTWserver2",OTWLogger);

        Logger TORLogger = Logger.getLogger("TOR.server2.log");
        configLogger("TORserver2",TORLogger);

        Logger replicaLogger = Logger.getLogger("replica4.log");
        configLogger("replica4", replicaLogger);
        ORB orb = ORB.init(new String[]{"-ORBInitialHost", "localhost", 
        		"-ORBInitialPort", "1050"}, null);  
        
        EventSystemImplementation MTLServer = new EventSystemImplementation("MTL");

        EventSystemImplementation OTWServer = new EventSystemImplementation("OTW");

        EventSystemImplementation TORServer = new EventSystemImplementation("TOR");

        EventSystemSTHWrong MTLWrong = new EventSystemSTHWrong();
        EventSystemSTHWrong OTWWrong = new EventSystemSTHWrong();
        EventSystemSTHWrong TORWrong = new EventSystemSTHWrong();

        
        
        
        
        Replica2 replica2 = new Replica2(replicaLogger , MTLServer , OTWServer , TORServer, MTLWrong, OTWWrong, TORWrong);

        Runnable replicaTask = () -> {
            try {
                replica2.startReplica(2222);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        
        Runnable replicaMTLServer = () -> {
            replica2.serverInitilize(MTLServer, MTLLogger, "MTL");
        };
        Runnable replicaOTWServer = () -> {
			replica2.serverInitilize(OTWServer, OTWLogger, "OTW");
        };
        Runnable replicaTORServer = () -> {
			replica2.serverInitilize(TORServer, TORLogger, "TOR");
        };

        Runnable softwareListener = () -> {
            try {
                replica2.startSoftFailPort(8882);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        
        Thread t1 = new Thread(replicaTask);
        Thread t2 = new Thread(softwareListener);
        Thread t3 = new Thread(replicaMTLServer);
        Thread t4 = new Thread(replicaOTWServer);
        Thread t5 = new Thread(replicaTORServer);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        replica2.reRunReplica(8082, 5002);

    }
    
 
    
	 private void serverInitilize(EventSystemImplementation nameImpl, Logger Log, String name) {
    	EventSystemImplementation stub;
		try {
			setupLogging(name);

			stub = nameImpl;
		// start the city's UDP server for inter-city communication
		// the UDP server is started on a new thread
			new Thread(() -> {
				((EventSystemImplementation) stub).UDPServer();
			}).start();
			System.out.println(name+" server started!");

		} catch (Exception e) {
			// TODO - catch only the specific exception
			e.printStackTrace();
		}
    }

	private static void setupLogging(String name) throws IOException {
		File files = new File(Constants.SERVER_LOG_DIRECTORY);
        if (!files.exists()) 
            files.mkdirs(); 
        files = new File(Constants.SERVER_LOG_DIRECTORY+name+"_Server.log");
        if(!files.exists())
        	files.createNewFile();
        allLogger.setup(files.getAbsolutePath());
	}
}
