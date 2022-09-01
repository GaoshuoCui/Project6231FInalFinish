package ReplicaHost3;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;

import ReplicaHost3RemoteObject.RMImpl;
import ReplicaHost3RemoteObject.EventSystemSTHWrong;

import functions.Constants;
import logTool.allLogger;



public class Replica3 {

    public boolean bugFree;
    public Logger logger;
    public RMImpl MTLServer;
    public RMImpl OTWServer;
    public RMImpl TORServer;
    public EventSystemSTHWrong MTLWrong;
    public EventSystemSTHWrong OTWWrong;
    public EventSystemSTHWrong TORWrong;

    public Replica3(Logger logger , RMImpl MTLServer, RMImpl OTWServer, RMImpl TORServer, 
    		EventSystemSTHWrong MTLWrong, EventSystemSTHWrong OTWWrong, EventSystemSTHWrong TORWrong){
        this.logger = logger;
        this.MTLServer = MTLServer;
        this.OTWServer = OTWServer;
        this.TORServer = TORServer;
        this.MTLWrong = MTLWrong;
        this.OTWWrong = OTWWrong;
        this.TORWrong = TORWrong;
        this.bugFree = false;
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

    public void startReplica(int replica1Port) throws IOException {
        DatagramSocket socket = new DatagramSocket(replica1Port);
        DatagramPacket packet = null;
        byte[] data = null;
        logger.info(" Replica3 Server Start");
        while(true)
        {
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            logger.info("Replica3 Recv Message :" + new String(packet.getData(), 0, packet.getLength()));

            String recvMsg = new String(packet.getData(), 0 , packet.getLength());
            String city = recvMsg.split(":")[0];

            Thread thread = new Thread(new MainThread(socket, packet, getCity(city), 
            		getWrongCity(city), bugFree));
            thread.start();
        }
    }

    private RMImpl getCity(String city) {
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
    	
        Logger MTLLogger = Logger.getLogger("MTL.Server3.log");
        configLogger("MTLServer3",MTLLogger);

        Logger OTWLogger = Logger.getLogger("OTW.Server3.log");
        configLogger("OTWServer3",OTWLogger);

        Logger TORLogger = Logger.getLogger("TOR.Server3.log");
        configLogger("TORServer3",TORLogger);

        Logger replicaLogger = Logger.getLogger("replica4.log");
        configLogger("replica4", replicaLogger);
        ORB orb = ORB.init(new String[]{"-ORBInitialHost", "localhost", 
        		"-ORBInitialPort", "1050"}, null);  
        
        RMImpl MTLServer = new RMImpl("MTL");

        RMImpl OTWServer = new RMImpl("OTW");

        RMImpl TORServer = new RMImpl("TOR");

        EventSystemSTHWrong MTLWrong = new EventSystemSTHWrong();
        EventSystemSTHWrong OTWWrong = new EventSystemSTHWrong();
        EventSystemSTHWrong TORWrong = new EventSystemSTHWrong();

        
        
        
        
        Replica3 replica3 = new Replica3(replicaLogger , MTLServer , OTWServer , TORServer, MTLWrong, OTWWrong, TORWrong);

        Runnable replicaTask = () -> {
            try {
                replica3.startReplica(3333);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        
        Runnable replicaMTLServer = () -> {
            replica3.serverInitilize(MTLServer, MTLLogger, "MTL");
        };
        Runnable replicaOTWServer = () -> {
			replica3.serverInitilize(OTWServer, OTWLogger, "OTW");
        };
        Runnable replicaTORServer = () -> {
			replica3.serverInitilize(TORServer, TORLogger, "TOR");
        };

        Runnable softwareListener = () -> {
            try {
                replica3.startSoftFailPort(8883);
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

        replica3.reRunReplica(8083, 5003);

    }
    
 
    
	 private void serverInitilize(RMImpl nameImpl, Logger Log, String name) {
    	RMImpl stub;
		try {
			setupLogging(name);

			stub = nameImpl;
		// start the city's UDP server for inter-city communication
		// the UDP server is started on a new thread
			new Thread(() -> {
				((RMImpl) stub).UDPServer();
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
