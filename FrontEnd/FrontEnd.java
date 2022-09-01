package FrontEnd;

import FECorba.FrontEndHelper;
import PortInformation.Replica;
import ReplicaHost1.LoggerFormatter;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrontEnd {
    private FrontEndImpl servent;
    public Logger logger;
    private Integer sequenceNumber;
    
    public FrontEnd(Logger logger, FrontEndImpl servent){
        this.servent = servent;
        this.logger = logger;
    }
    
    public void startCorbaServer(String frontEnd){
        try{
            // create and initialize the ORB
            ORB orb = ORB.init(new String[]{"-ORBInitialHost", "localhost", "-ORBInitialPort", "1050"}, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            FrontEndImpl EventSystemImpl = this.servent;
            EventSystemImpl.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(EventSystemImpl);
            FECorba.FrontEnd href = FrontEndHelper.narrow(ref);

            // get the root naming context
            // NameService invokes the name service
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            String name = frontEnd;
            NameComponent path[] = ncRef.to_name( name );
            ncRef.rebind(path, (org.omg.CORBA.Object) href);

            logger.info(frontEnd + "server ready and waiting ...");

            // wait for invocations from clients
            orb.run();
            
        }

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("Server Exiting ...");
    }

    public static void configLogger(String city , Logger logger) throws IOException {
        logger.setLevel(Level.ALL);
        FileHandler cHandler = new FileHandler(city + ".log");
        cHandler.setFormatter(new LoggerFormatter());
        logger.addHandler(cHandler);
    }

    public static void main(String[] args) throws IOException {
        Logger cLogger = Logger.getLogger("Client.log");
        configLogger("Client",cLogger);

        FrontEndImpl frontEnd = new FrontEndImpl();
        FrontEnd server = new FrontEnd(cLogger,frontEnd);
        System.out.println("FrontEnd Start!");
        server.startCorbaServer("frontEnd");
        
    }
}
