/*
 * COMP6231 A1
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

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;


import FECorba.FrontEnd;
import FECorba.FrontEndHelper;
import client.User;
import functions.Constants;
import functions.EventType;
import functions.FuntionMembers;
import functions.Role;
import logTool.allLogger;

/**
 * The <code>AdvisorClient</code> class contains the code to handle and perform
 * the operations related to the Advisor.
 * This class implements <code>Runnable</code> so that each advisor login can be
 * handled on a separate thread. 
 */
public class MuiltiManagers implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	User user;
	Scanner input;
	int no;
	String eID;
	int cap;
	int typeN;
	String[] args;
	FrontEnd stub;
	
	/**
	 * Constructor to initialize an Advisor object
	 * @param user <code>User</code> class object
	 */
	public MuiltiManagers(User user,int no,String eID,int cap,int typeN,String[] args) {
		this.user = user;
		input = new Scanner(System.in);
		this.no = no;
		this.eID = eID;
		this.cap = cap;
		this.typeN = typeN;
		this.args = args;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			setupLogging();
			LOGGER.info("MANAGER LOGIN("+user+")");
			stub = connect("frontEnd");
			//Register some events
			performOperations(this.no,this.eID,this.cap,this.typeN);
			
		} catch (RemoteException e) {
			LOGGER.severe("RemoteException Exception : "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe("IO Exception : "+e.getMessage());
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

	@SuppressWarnings({ "unchecked", "unused" })
	private void performOperations(int no,String eID,int cap,int typeN) throws RemoteException {

		int userSelection = no;
		String customerId, eventId, type = null;
		EventType eventtype;
		int eventCapacity = 0;
		SimpleEntry<Boolean, String> result;
		String[] eventMap;
		String[] eventList;
		String status;
		Any any;
		/* Executes the loop until the managers quits the application i.e. presses 7
		 * 
		 */
			switch (userSelection) {
			case 1:
				//System.out.print("Enter the event ID : ");
				eventId = eID;
				result = FuntionMembers.validateEvent(eventId.trim(), this.user.getcity(),null);
				int typen;
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}

				//System.out.print("Event Capacity : ");
				eventCapacity = cap;
				if(eventCapacity<1) {
					System.out.println("Event Capacity needs to be atleast 1.");
					break;
				}

				//System.out.print("Enter the EventType for the event(1.Conferences|2.Seminars|3.TradeShows) : ");
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
				result = FuntionMembers.validateType(type.trim());
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}else {
					eventtype = EventType.valueOf(type.toUpperCase());
				}
				status = stub.addEvent(user.toString(), eventId, eventtype.toString(), eventCapacity);
				LOGGER.info(String.format(Constants.LOG_MSG, "addEvent",Arrays.asList
						(user,eventId,eventtype,eventCapacity),status,Constants.EMPTYSTRING));
				if(status.contains("true"))
					System.out.println("SUCCESS - Event Added Successfully");
				else
					System.out.println("FAILURE = "+eventId+" is already offered in "+eventtype+", only capacity "+ eventCapacity + " would be updated.");
				break;
				
				
			case 2:
				System.out.print("Enter the event ID : ");
				eventId = input.next().toUpperCase();
				result = FuntionMembers.validateEvent(eventId.trim(), this.user.getcity(),null);
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}

				System.out.print("Enter the EventType for the event(1.Conferences|2.Seminars|3.TradeShows) : ");
				typen = input.nextInt();
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

				status = stub.removeEvent(user.toString(), eventId, eventtype.toString());
				LOGGER.info(String.format(Constants.LOG_MSG, "removeEvent",Arrays.asList(user,eventId,eventtype),status,Constants.EMPTYSTRING));
				if(status.contains("true"))
					System.out.println("SUCCESS - "+eventId+" removed successfully for "+eventtype+".");
				else
					System.out.println("FAILURE - "+eventId+" is not offered in  "+eventtype+".");
				break;
				
			case 3:
				//System.out.print("Enter the EventType for event schedule(1.Conferences|2.Seminars|3.TradeShow) : ");
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
				if (typen==4) {
					String[] anyC = stub.listEventAvailability(user.toString(), "CONFERENCES");
					String[] eventMapC = anyC;
					
					String[] anyS = stub.listEventAvailability(user.toString(), "SEMINARS");
					String[] eventMapS = anyS;
					
					String[] anyT = stub.listEventAvailability(user.toString(), "TRADESHOWS");
					String[] eventMapT = anyT;
					
					
					LOGGER.info(String.format(Constants.LOG_MSG, "listEventAvailability",
							Arrays.asList(user, "Conferences"), eventMapC!=null,eventMapC));
					if(eventMapC!=null) {
						System.out.print("Conferences");
						for (String event : eventMapC){
	                        System.out.print(" " + event);
	                    }
					}
					else
						System.out.println("There was some problem in getting the Event schedule. Please try again later.");
					
					
					LOGGER.info(String.format(Constants.LOG_MSG, "listEventAvailability",
							Arrays.asList(user, "Seminars"), eventMapS!=null,eventMapS));
					if(eventMapS!=null) {
						System.out.print("  Seminars");
						for (String event : eventMapS){
	                        System.out.print(" " + event);
	                    }
					}
					else
						System.out.println("There was some problem in getting the Event schedule. Please try again later.");
					
					
					LOGGER.info(String.format(Constants.LOG_MSG, "listEventAvailability",
							Arrays.asList(user, "TradeShows"), eventMapT!=null,eventMapT));

					if(eventMapT!=null) {
						System.out.print("  TradeShows");
						for (String event : eventMapT){
	                        System.out.print(" " + event);
	                    }
					}
					else
						System.out.println("There was some problem in getting the Event schedule. Please try again later.");
					break;
				}
				result = FuntionMembers.validateType(type.trim());
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}else {
					eventtype = EventType.valueOf(type.toUpperCase());
				}

				String[] any2 = stub.listEventAvailability(user.toString(), eventtype.toString());
				eventMap = any2;
				
				LOGGER.info(String.format(Constants.LOG_MSG, "listEventAvailability",
						Arrays.asList(user, eventtype), eventMap!=null,eventMap));
				if(eventMap!=null) {
					System.out.print(type);
					for (String event : eventMap){
                        System.out.print(" " + event);
                    }
				}
				else
					System.out.println("There was some problem in getting the Event schedule. Please try again later.");
				
				break;
			case 4:
				System.out.print("Enter the Customer ID(eg. MTLC1111) : ");
				customerId = input.next().toUpperCase();
				result = FuntionMembers.validateUser(customerId.trim(), Role.Customer,this.user.getcity());
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}
				System.out.print("Enter the Event ID (eg. MTLA2342,OTWE2345,...) : ");
				eventId = input.next().toUpperCase();
				result = FuntionMembers.validateEvent(eventId.trim());
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}
				System.out.print("Enter EventType(FALL|WINTER|SUMMER) : ");
				type = input.next();
				result = FuntionMembers.validateType(type.trim());
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}else {
					eventtype = EventType.valueOf(type.toUpperCase());
				}
				
String any3 = stub.bookevent(customerId, eventId, eventtype.toString());
				
				LOGGER.info(String.format(Constants.LOG_MSG, "BookEvent",Arrays.asList(customerId,eventId
						,eventtype),any3,""));
				if(any3.contains("true"))
					System.out.println("SUCCESS - "+customerId+" successfully booked in "+eventId+".");
				else
					System.out.println("FAILURE - "+any3);
				
				break;

			case 5:
				System.out.print("Enter the Customer ID(eg. MTLC1111) : ");
				customerId = input.next().toUpperCase();
				result = FuntionMembers.validateUser(customerId.trim(), Role.Customer,this.user.getcity());
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}

				eventList = stub.getbookingSchedule(customerId);

				LOGGER.info(String.format(Constants.LOG_MSG, "getBookingSchedule",Arrays.asList(customerId),eventList!=null,eventList));
				if(eventList!=null)
					
					for (String event : eventList){
                        System.out.print(" " + event);
                    }
				
				else
					System.out.println("There was some problem in getting the event schedule. Please try again later.");
				
				break;
			case 6:
				System.out.print("Enter the Customer ID(eg.MTLC1111) : ");
				customerId = input.next().toUpperCase();
				result = FuntionMembers.validateUser(customerId.trim(), Role.Customer,this.user.getcity());
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}
				System.out.print("Enter the Course ID to drop(eg. COMP2342,SOEN2345,...) : ");
				eventId = input.next().toUpperCase();
				result = FuntionMembers.validateEvent(eventId.trim(), null,null);
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}
String any4 = stub.dropevent(customerId, eventId);
				
				LOGGER.info(String.format(Constants.LOG_MSG, "dropEvent",Arrays.asList(customerId,eventId)
						,any4," "));
				if(any4.contains("true"))
					System.out.println("SUCCESS - Event successfully dropped for "+customerId+".");
				else
					System.out.println("FAILURE - "+any4);
				
				break;
			case 7:
				//swap
				break;
			case 8:
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
		File files = new File(Constants.MANAGER_LOG_DIRECTORY);
        if (!files.exists()) 
            files.mkdirs(); 
        files = new File(Constants.MANAGER_LOG_DIRECTORY+user+".log");
        if(!files.exists())
        	files.createNewFile();
        allLogger.setup(files.getAbsolutePath());
	}

}

