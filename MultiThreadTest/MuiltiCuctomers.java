/*
 * COMP6231 A2
 * Tianlin Yang 40010303
 * Gaoshuo Cui 40085020
 */

package MultiThreadTest;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import FECorba.FrontEnd;
import FECorba.FrontEndHelper;
import client.User;
import functions.Constants;
import functions.EventType;
import functions.FuntionMembers;
import logTool.allLogger;

/**
 * This class implements <code>Runnable</code> so that each customer login can be
 * handled on a separate thread. 
 */

public class MuiltiCuctomers implements Runnable{
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	User user;
	Scanner input;
	int no;
	String eID;
	String eID2;
	int typeN;
	int typeN2;
	String[] args;
	FrontEnd stub;

	/**
	 * customers constructor to initialize its object
	 * @param user
	 */
	public MuiltiCuctomers(User user,int no,String eID,int typeN,String[] args,String eID2,int typeN2) {
		this.user = user;
		input = new Scanner(System.in);
		this.no = no;
		this.eID = eID;//old
		this.typeN = typeN;
		this.args = args;
		this.eID2 = eID2;//new
		this.typeN2 = typeN2;//new
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		try {
			setupLogging();//Initialize the log file.
			LOGGER.info("Client LOGIN(" + user + ")");//Record current customer
			stub = connect("frontEnd");
			//System.out.println("====================");
			
			//------------------------------Book some event for target user-------------------------------
			performOperations(this.no,this.eID,this.typeN);
			
		} catch (RemoteException e) {
			LOGGER.severe("RemoteException Exception : " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe("IO Exception : " + e.getMessage());
			e.printStackTrace();
		}

	}
	
	public static FrontEnd connect(String name){
        FrontEnd frontEnd = null;
        try{
            // create and initialize the ORB
            ORB orb = ORB.init(new String[]{"-ORBInitialPort","1050","-ORBInitialHost","localhost"}, null);

            // get the root naming context
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolve the Object Reference in Naming
            frontEnd = FrontEndHelper.narrow(ncRef.resolve_str(name));

        } catch (Exception e) {
            System.out.println("ERROR : " + e);
            e.printStackTrace(System.out);
        }
        return frontEnd;
    }

	@SuppressWarnings("unchecked")
	private void performOperations(int no,String eID,int typenum) throws RemoteException {

		int userSelection = no; 
		String evenId,type = null;
		int typen;
		EventType eventtype;
		SimpleEntry<Boolean, String> result;//Save result as key/value
		String any;
		
			switch (userSelection) {
			case 1:
				//System.out.print("Enter the Event ID (eg. MTLA100519,TORE092319,...) : ");
				evenId = eID.toUpperCase();
				result = FuntionMembers.validateEvent(evenId.trim(),null,null);
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}
				//System.out.print("Enter EventType(1.Conferences|2.Seminars|3.TradeShows) : ");
				typen = typenum;
				if (typen==1) {
					type = "Conferences";
				}
				if (typen==2) {
					type = "Seminars";
				}
				if (typen==3) {
					type = "TradeShows";
				}
				result = FuntionMembers.validateType(type.trim());
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}else {
					eventtype = EventType.valueOf(type.toUpperCase());
				}
				//System.out.println((user.toString()+ evenId+eventtype.toString()));
				String any3 = stub.bookevent(user.toString(), evenId, eventtype.toString());

				LOGGER.info(String.format(Constants.LOG_MSG, "BookEvent", Arrays.asList(user, evenId, eventtype),any3,""));
				if (any3.contains("true"))
					System.out.println("SUCCESS - " + any3);
				else
					System.out.println("FAILURE - " + any3);

				break;

			case 2:
				String[] eventList = stub.getbookingSchedule(user.toString());

				LOGGER.info(String.format(Constants.LOG_MSG, "getBookingSchedule", Arrays.asList(user),
						eventList != null, eventList));
				if (eventList != null)
					for (String event : eventList){
                        System.out.print(" " + event);
                    }
				
				else
					System.out.println("There was some problem in getting the event schedule. Please try again later.");

				break;
			case 3:
				//System.out.print("Enter the Event ID to drop : ");
				evenId = eID.toUpperCase();
				result = FuntionMembers.validateEvent(evenId.trim(),null,null);
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}
String any4 = stub.dropevent(user.toString(), evenId);
				
				LOGGER.info(String.format(Constants.LOG_MSG, "dropEvent", Arrays.asList(user, evenId),any4," "));
				if (result.getKey())
					System.out.println("SUCCESS -" + any4);
				else
					System.out.println("FAILURE - " + any4);

				break;

			case 4:
				EventType oldeventtype;
				EventType neweventtype;
				String oldeventId = eID;
				result = FuntionMembers.validateEvent(oldeventId.trim());
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}
				typen = typeN;
				if (typen==1) {
					type = "Conferences";
				}
				if (typen==2) {
					type = "Seminars";
				}
				if (typen==3) {
					type = "TradeShows";
				}
				result = FuntionMembers.validateType(type.trim());//Check type validate
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}else {
					oldeventtype = EventType.valueOf(type.toUpperCase());
				}
				String neweventId = eID2;
				result = FuntionMembers.validateEvent(neweventId.trim());
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}
				typen = typeN2;
				if (typen==1) {
					type = "Conferences";
				}
				if (typen==2) {
					type = "Seminars";
				}
				if (typen==3) {
					type = "TradeShows";
				}
				result = FuntionMembers.validateType(type.trim());//Check type validate
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}else {
					neweventtype = EventType.valueOf(type.toUpperCase());
				}
				//System.out.println("Inputs finished!");//BUG------------------------------------------------------------------------
String any5 = stub.swapEvent(user.toString(), neweventId, neweventtype.toString(), oldeventId, oldeventtype.toString());
				
				//System.out.println("Any/Result generated!");
				LOGGER.info(String.format(Constants.LOG_MSG, "swapEvent",Arrays.asList(user.toString(),neweventId,oldeventId,
						neweventtype, oldeventtype),any5,""));
				if(any5.contains("true"))
					System.out.println("SUCCESS - Event successfully swapped for "+user.toString()+".");
				else
					System.out.println("FAILURE - "+any5);
				break;
			case 5://Quit
				break;
			default:
				System.out.println("Please select a valid operation.");
				break;
			}

		}


	/**
	 * Configures the logger
	 * @throws IOException
	 */
	private void setupLogging() throws IOException {
		File files = new File(Constants.CUSTOMER_LOG_DIRECTORY);
		if (!files.exists())
			files.mkdirs();
		files = new File(Constants.CUSTOMER_LOG_DIRECTORY + user + ".log");
		if (!files.exists())
			files.createNewFile();
		allLogger.setup(files.getAbsolutePath());
	}

	
}




