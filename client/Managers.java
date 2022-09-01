/*
 * COMP6231 A2
 * Tianlin Yang 40010303
 * Gaoshuo Cui 40085020
 */
package client;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

import org.omg.CORBA.Any;

import FECorba.FrontEnd;
import functions.Constants;
import functions.EventType;
import functions.FuntionMembers;
import functions.Role;
import logTool.allLogger;

/**
 * UI operation for Manager, runnable.
 * @author TLIN
 *
 */
public class Managers extends Client implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	User user;
	Scanner input;
	FrontEnd stub;
	String[] args;
	/**
	 * Ctor
	 * @param user <code>User</code> class object
	 */
	public Managers(String[] args,User user) {
		this.user = user;
		this.args = args;
		input = new Scanner(System.in);
	}


	public void run() {

		try {
			setupLogging();
			LOGGER.info("MANAGER LOGIN("+user+")");
			stub = connect("frontEnd");
			Options();
		} catch (RemoteException e) {
			LOGGER.severe("RemoteException Exception : "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe("IO Exception : "+e.getMessage());
			e.printStackTrace();
		} 

	}

	/**
	 * This method call options
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private void Options() throws RemoteException {

		int userSelection = displayMenu();
		String customerId, eventId, type = null;
		EventType eventtype;
		int eventCapacity = 0;
		SimpleEntry<Boolean, String> result;
		String[] eventMap;
		String[] eventList;
		String status;
		Any any;
		
		/* Executes the loop until the managers quits the application i.e. presses 8
		 * 
		 */
		while (userSelection != 8) {
			
			switch (userSelection) {
			case 1:
				System.out.print("Enter the event ID : ");
				eventId = input.next().toUpperCase();
				result = FuntionMembers.validateEvent(eventId.trim(), this.user.getcity(),null);
				int typen;
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}

				System.out.print("Event Capacity : ");
				eventCapacity = input.nextInt();
				if(eventCapacity<1) {
					System.out.println("Event Capacity needs to be atleast 1.");
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

				status = stub.addEvent(user.toString(), eventId, eventtype.toString(), eventCapacity);
				System.out.println("Addevent return string: " + status);
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
				LOGGER.info(String.format(Constants.LOG_MSG, 
						"removeEvent",Arrays.asList(user,eventId,eventtype),status,Constants.EMPTYSTRING));
				if(status.contains("true"))
					System.out.println("SUCCESS - "+eventId+" removed successfully for "+eventtype+".");
				else
					System.out.println("FAILURE - "+eventId+" is not offered in  "+eventtype+".");
				break;
				
			case 3:
				System.out.print("Enter the EventType for event schedule(1.Conferences|2.Seminars|3.TradeShow|4.All) : ");
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
				System.out.print("Enter EventType(1.Conferences|2.Seminars|3.TradeShows) : ");
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
				System.out.print("Enter the Event ID to drop : ");
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
//swap course
				EventType oldeventtype;
				EventType neweventtype;
				System.out.print("Enter the Customer ID(eg.MTLC1111) : ");
				customerId = input.next().toUpperCase();
				result = FuntionMembers.validateUser(customerId.trim(), Role.Customer,this.user.getcity());
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}
				System.out.print("Enter the Event ID to drop(eg. MTLA2342,OTWE2345,...) : ");
				String oldeventId = input.next().toUpperCase();
				result = FuntionMembers.validateEvent(oldeventId.trim());
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
					oldeventtype = EventType.valueOf(type.toUpperCase());
				}
				System.out.print("Enter the Event ID to book (eg. MTLA2342,OTWE2345,...) : ");
				String neweventId = input.next().toUpperCase();
				result = FuntionMembers.validateEvent(neweventId.trim());
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
					neweventtype = EventType.valueOf(type.toUpperCase());
				}
				
				String any5 = stub.swapEvent(customerId, neweventId, neweventtype.toString(), oldeventId, oldeventtype.toString());
				
				LOGGER.info(String.format(Constants.LOG_MSG, "swapEvent",Arrays.asList(customerId,neweventId,oldeventId,oldeventtype,
						neweventtype),any5,""));
				if(any5.contains("true"))
					System.out.println("SUCCESS - Event successfully swapped for "+customerId+".");
				else
					System.out.println("FAILURE - "+any5);
				break;
			case 8:
				break;
			default:
				System.out.println("Please select a valid operation.");
				break;

			}

			System.out.println("\n\n");
			userSelection = displayMenu();
		}
		System.out.println("HAVE A NICE DAY!");
	}

	
	/**
	 * Display menu for manager
	 * @return
	 */
	private int displayMenu() {
		System.out.println("*     Select a operation     *");
		System.out.println("(1) Add a event.");
		System.out.println("(2) Remove a event.");
		System.out.println("(3) List Event Availability.");
		System.out.println("(4) Book a event for customer.");
		System.out.println("(5) Get Booking Schedule for customer.");
		System.out.println("(6) Drop a event for customer.");
		System.out.println("(7) Swap a event for customer.");
		System.out.println("(8) Quit.");
		System.out.print("Please input the number: ");
		
		return input.nextInt();
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
