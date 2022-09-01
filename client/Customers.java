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


import FECorba.FrontEnd;
import functions.Constants;
import functions.EventType;
import functions.FuntionMembers;
import logTool.allLogger;

/**
 * UI operation for Customers, runnable.
 * @author TLIN
 *
 */
public class Customers extends Client implements Runnable{
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	User user;
	Scanner input;
	String[] args;
	FrontEnd stub;

	/**
	 * customers constructor to initialize its object
	 * @param user
	 */
	public Customers(String[] args, User user) {
		this.user = user;
		this.args = args;
		input = new Scanner(System.in);
	}

	public void run() {

		try {
			setupLogging();//Initialize the log file.
			LOGGER.info("Client LOGIN(" + user + ")");//Record current customer
			stub = connect("frontEnd");
			System.out.println("------------------------------------------------");
			Options();//Get UI run
		} catch (RemoteException e) {
			LOGGER.severe("RemoteException Exception : " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe("IO Exception : " + e.getMessage());
			e.printStackTrace();
		} 
		


	}

	@SuppressWarnings("unchecked")
	private void Options() throws RemoteException {

		int userSelection = displayMenu(); //Call console println menu
		String evenId,type = null;
		int typen;
		EventType eventtype;
		SimpleEntry<Boolean, String> result;//Save result as key/value
		String any;
		//When user not quit call 5
		while (userSelection != 5) {

			switch (userSelection) {
			case 1:
				System.out.print("Enter the Event ID (eg. MTLA100519,TORE092319,...) : ");
				evenId = input.next().toUpperCase();
				result = FuntionMembers.validateEvent(evenId.trim(),null,null);//Check event format
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
				result = FuntionMembers.validateType(type.trim());//Check type validate
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}else {
					eventtype = EventType.valueOf(type.toUpperCase());
				}
				System.out.println((user.toString()+ evenId+eventtype.toString()));
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
				System.out.print("Enter the Event ID to drop : ");
				evenId = input.next().toUpperCase();
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
				System.out.print("Enter the Event ID to drop(eg. MTLA100519,TORE092319,...)  : ");
				String oldeventId = input.next().toUpperCase();
				result = FuntionMembers.validateEvent(oldeventId.trim());
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
				result = FuntionMembers.validateType(type.trim());//Check type validate
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}else {
					oldeventtype = EventType.valueOf(type.toUpperCase());
				}
				System.out.print("Enter the Event ID to book(eg. MTLA100519,TORE092319,...)  : ");
				String neweventId = input.next().toUpperCase();
				result = FuntionMembers.validateEvent(neweventId.trim());
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
				result = FuntionMembers.validateType(type.trim());//Check type validate
				if (!result.getKey()) {
					System.out.println(result.getValue());
					break;
				}else {
					neweventtype = EventType.valueOf(type.toUpperCase());
				}
				//System.out.println("Inputs finished!"+neweventtype.toString());//BUG------------------------------------------------------------------------
				String any5 = stub.swapEvent(user.toString(), neweventId, neweventtype.toString(), oldeventId, oldeventtype.toString());
				
				//System.out.println("Any/Result generated!");
				LOGGER.info(String.format(Constants.LOG_MSG, "swapEvent",Arrays.asList(user.toString(),neweventId,oldeventId,
						neweventtype, oldeventtype),any5,""));
				if(any5.contains("true"))
					System.out.println("SUCCESS - Event successfully swapped for "+user.toString()+".");
				else
					System.out.println("FAILURE - "+any5);
				break;
			case 5:
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
	 * Display menu
	 * @return
	 */
	private int displayMenu() {
		System.out.println("*     Select a operation     *");
		System.out.println("(1) Book a Event.");
		System.out.println("(2) Get Event Schedule.");
		System.out.println("(3) Drop a Event.");
		System.out.println("(4) Swap a Event.");
		System.out.println("(5) Quit.");
		System.out.print("Please input the number: ");
		return input.nextInt();
	}

	/**
	 * Setup logger
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




