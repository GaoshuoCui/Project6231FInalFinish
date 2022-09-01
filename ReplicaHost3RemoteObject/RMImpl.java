/*
 * COMP6231 A2
 * Tianlin Yang 40010303
 * Gaoshuo Cui 40085020
 */
package ReplicaHost3RemoteObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import ReplicaHost3.City;
import functions.FuntionMembers;



public class RMImpl  {
	
//Standard initialize for same CORBA FrontEnd.
	private SimpleEntry<Boolean, String> BEresult;
	private HashMap<String, ArrayList<String>> BSchedule;
	private HashMap<String, HashMap<String, HashMap<String, Object>>> ServerDatabase;
	private SimpleEntry<Boolean, String> DEventresult;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private City city;//City port for replica 3
	private ReentrantLock rl;


	public RMImpl(String city){//Ctor
		this.city = City.valueOf(city);
		ServerDatabase = new HashMap<>();
		this.rl = new ReentrantLock(true); 
	}
	
	//Loggers
	private void addEventLogger(String managerId, String eventId, String eventtype, 
			int capacity, boolean status, String msg) {
		LOGGER.info(String.format(
				"METHOD[%s]; PARAMETERS%s; STATUS[%s]; SERVER_MESSAGE[%s]", 
				"addEvent",
				Arrays.asList(managerId, eventId, eventtype, capacity), 
				status, msg));
		
	}
	
	//Save event msg/bool for an City
	private SimpleEntry<Boolean, String> CityContainsKeyImpl(String eventId, String eventtype, int capacity) {
		boolean status = false;
		String msg = "";
		HashMap<String, HashMap<String, Object>> event = ServerDatabase.get(eventtype);		
		if (event.containsKey(eventId)) {
			status = false;
			msg = "Event already exists for "+eventtype+", update the capacity to "+capacity+".";
					event.remove(eventId);
					HashMap<String, Object> eventDetails = new HashMap<>();eventDetails.put("capacity", capacity);eventDetails.put("customersEnrolled", 0);
					eventDetails.put("customerIds", new HashSet<String>());
					event.put(eventId, eventDetails);
		} else {
			HashMap<String, Object> eventDetails = new HashMap<>();eventDetails.put("capacity", capacity);eventDetails.put("customersEnrolled", 0);
				eventDetails.put("customerIds", new HashSet<String>());
				event.put(eventId, eventDetails);
			status = true;
			msg = eventId + " Added.";
		}
		SimpleEntry<Boolean, String> resultEntry = new SimpleEntry<Boolean, String>(status,msg);

		return resultEntry;
	
		
	}
	//Validate reply.
	private String replyValidate(boolean status, String msg,String name) {
		String reply;
		if (status == true) {
			reply="true - "+name+" Finish!"+msg;
		}else {reply="false- "+name+" fail!"+msg;}
		//this.udpToFE(status, "addEvent");
		System.out.println("RMImpl- "+reply);
		return reply;
	}
	//Add Event as FrontEnd Same Port------------------------------------------------------------------------------------------------
	public synchronized String addEvent(String managerId, String eventId, String eventtype, int capacity) {
		boolean status = false;
		String msg = "";
		if (ServerDatabase.containsKey(eventtype)) {
			
			SimpleEntry<Boolean, String> reentry = CityContainsKeyImpl(eventId, eventtype, capacity);
			
			status = reentry.getKey();
			
			msg = reentry.getValue();
			
		} else {
			HashMap<String, Object> eventDetails = new HashMap<>();
			eventDetails.put("capacity", capacity);eventDetails.put("customersEnrolled", 0);
			eventDetails.put("customerIds", new HashSet<String>());HashMap<String, HashMap<String, Object>> event = new HashMap<>();
			event.put(eventId, eventDetails);
			this.ServerDatabase.put(eventtype, event);

			status = true;
			msg = eventId + " Added.";
		}
		addEventLogger(managerId, eventId, eventtype, capacity, status, msg);		
		String reply = replyValidate(status,msg,"Add Event");
		return reply;
	}

	private SimpleEntry<Boolean, String> removeValidate(String eventId, String eventtype) {
		String msg = "";
		Boolean status;
		HashMap<String, HashMap<String, Object>> event;
		if (ServerDatabase.containsKey(eventtype)) { event = ServerDatabase.get(eventtype);
			if (event.containsKey(eventId)) {
				synchronized(this) {event.remove(eventId);}
				status = true;
				msg = eventId + " removed";
			} else {
				status = false;
				msg = eventtype + "doesn't offer this event yet.";
			}
		} else {
			status = false;
			msg = eventtype + "doesn't have any event yet.";
		}
		SimpleEntry<Boolean, String> resultEntry = new SimpleEntry<Boolean, String>(status,msg);
		return resultEntry;
	}
	
	
	
	//Remove Event as FrontEnd Same Port------------------------------------------------------------------------------------------------
	public String removeEvent(String managerId, String eventId, String eventtype){
		boolean status = false;
		String msg = "";
		SimpleEntry<Boolean, String> reentry = removeValidate(eventId,eventtype);
		status = reentry.getKey();	
		msg = reentry.getValue();
		LOGGER.info(String.format("METHOD[%s]; PARAMETERS%s; STATUS[%s]; SERVER_MESSAGE[%s]", "removeEvent",
				Arrays.asList(managerId, eventId, eventtype), status, msg));
		String reply = replyValidate(status,msg,"Remove Event");
		return reply;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Integer> listCurrent(String managerId, String eventtype){
		HashMap<String, Integer> result = new HashMap<>();
		result.putAll(listAt(eventtype));
		// Call other Server here.
		SendUDPForGetOthersList(eventtype);
		LOGGER.info(String.format("METHOD[%s]; PARAMETERS%s; STATUS[%s]; SERVER_MESSAGE[%s]", "listEventAvailability",Arrays.asList(managerId, eventtype), result != null, result));
		return result;
	}



	@SuppressWarnings("unchecked")
	public synchronized String bookevent(String customerId, String eventId, String eventtype){
		//Result inil
		boolean status = true;
		String msg = null;
		SimpleEntry<Boolean, String> result = null;

		result = OutofCityValidator(customerId, eventId, eventtype);

		if (result == null) result = new SimpleEntry<Boolean, String>(status, msg);
		LOGGER.info(String.format("METHOD[%s]; PARAMETERS%s; STATUS[%s]; SERVER_MESSAGE[%s]", "bookEvent",Arrays.asList(customerId, eventId, eventtype), result.getKey(), result.getValue()));

		BEresult = result;
		status = result.getKey();
		msg = result.getValue();
		String reply = replyValidate(status,msg,"Book Event");
		return reply;
	}

	
	
	

	private SimpleEntry<Boolean, String> bookCurrent(String customerId, String eventId,
			String eventtype) {
		boolean status;
		String msg;
		if (ServerDatabase.containsKey(eventtype)) {
			HashMap<String, HashMap<String, Object>> events = ServerDatabase.get(eventtype);
			if (events.containsKey(eventId)) {
				HashMap<String, Object> e = events.get(eventId);
				if (((Integer) e.get("capacity")- (Integer) e.get("customersEnrolled")) > 0) {				
					synchronized(this) {status = ((HashSet<String>) e.get("customerIds")).add(customerId);
						if (status) {
							e.put("customersEnrolled",
									(Integer) e.get("customersEnrolled") + 1);
							status = true;
							msg = "Enrollment Successful.";
						} else {
							status = false;
							msg = customerId + " is already enrolled in "+eventId+".";
						}
					}
				} else {
					status = false;
					msg = eventId + " is full.";
				}
			} else {
				status = false;
				msg = eventId + " is not offered in "+eventtype+".";
			}
		} else {
			status = false;
			msg = "No events avialable for " + eventtype + ".";
		}
		return new SimpleEntry<Boolean, String>(status, msg);
	}


	public String dropevent(String customerId, String eventId) {
		SimpleEntry<Boolean, String> result = descD(customerId, eventId);
		LOGGER.info(String.format("METHOD[%s]; PARAMETERS%s; STATUS[%s]; SERVER_MESSAGE[%s]", "dropEvent", Arrays.asList(customerId, eventId),
				result.getKey(), result.getValue()));
		String reply = "";	
		Boolean status = result.getKey();
		String msg = result.getValue();
		reply = replyValidate(status, msg, "Drop Event");
		DEventresult = result;
		return reply;
	}
	
	
	
	
	
	
	private SimpleEntry<Boolean, String> descD(String customerId, String eventId) {
		City ec = City.valueOf(eventId.substring(0, 3).toUpperCase());
		SimpleEntry<Boolean, String> result;
		if (this.city == ec) {
			result = dOT(customerId, eventId);
		} else {
			HashMap<String, String> data = new HashMap<>();data.put("customerId", customerId);data.put("eventId", eventId);
			result = (SimpleEntry<Boolean, String>) FuntionMembers.byteArrayToObject(udpCommunication(ec, data, "dropEvent"));
		}
		return result;
	}

	
	
	
	
	private synchronized SimpleEntry<Boolean, String> dOT(String customerId, String eventId) {	
		final Map<Boolean, String> t = new HashMap<>();
		if (ServerDatabase.size() > 0) {
			ServerDatabase.forEach((ci, e) -> {
				if (e.containsKey(eventId)) {
					e.forEach((e2, d) -> {
						if (e2.equals(eventId)) {
								boolean status = ((HashSet<String>) d.get("customerIds")).remove(customerId);
								if (status) {
									d.put("customersEnrolled",
											((Integer) d.get("customersEnrolled") - 1));
									t.put(true, "success");
								} else {
									t.put(false, customerId + " isn't enrolled in "+eventId+".");
								}}});
				} else {t.put(false, eventId + " isn't offered by the city yet.");}});} 
				  else {t.put(false, eventId + " isn't offered by the city yet.");} 
		if (t.containsKey(true)) {
			return new SimpleEntry<Boolean, String>(true, "Event Dropped.");
		} else {
			return new SimpleEntry<Boolean, String>(false, t.get(false));
		}
	}
	

	public String[] getbookingSchedule(String customerId){
		HashMap<String, ArrayList<String>> ResultS = new HashMap<>();
		ResultS.putAll(getSschedule(customerId));
		// inquire different cities
		for (City ci : City.values()) {
			if (ci!= this.city) {
				@SuppressWarnings("unchecked")
				HashMap<String, ArrayList<String>> sciS = (HashMap<String, ArrayList<String>>) FuntionMembers.byteArrayToObject(udpCommunication(ci, customerId, "getEventSchedule"));
				for(String et : sciS.keySet()) {
					if(ResultS.containsKey(et)) {
						ResultS.get(et).addAll(sciS.get(et));
					}else {
						ResultS.put(et, sciS.get(et));}}}}
		LOGGER.info(String.format("METHOD[%s]; PARAMETERS%s; STATUS[%s]; SERVER_MESSAGE[%s]", "getEventSchedule", Arrays.asList(customerId),
				ResultS != null, ResultS));
		String[] arrayResult = ResultS.toString().split(",");
		System.out.println("Result is : "+ ResultS.toString());
		BSchedule = ResultS;
		return arrayResult;
	}

	@SuppressWarnings({ "unchecked"})
	private HashMap<String, ArrayList<String>> getSschedule(String customerId) {
		HashMap<String, ArrayList<String>> se = new HashMap<>();
		ServerDatabase.forEach((et, events) -> {
			events.forEach((e, es) -> {if (((HashSet<String>) es.get("customerIds")).contains(customerId)) {if (se.containsKey(et)) {se.get(et).add(e);
					} else {ArrayList<String> temp = new ArrayList<>();
						temp.add(e);se.put(et, temp);
					}
				}});});
		return se;
	}

	@SuppressWarnings("unchecked")


	public String swapEvent(String customerId,  String neweventId, String neweventtype, String oldeventId, String oldeventtyped) {
		boolean status = true;
		String msg = null;
		SimpleEntry<Boolean, String> re;
		getbookingSchedule(customerId);
		HashMap<String, ArrayList<String>> sg = BSchedule;
		String oly = oldeventId.substring(0, 3).toUpperCase();
		City nty = City.valueOf(neweventId.substring(0, 3).toUpperCase());

		List<String> C = new ArrayList<>();
		List<String> s = new ArrayList<>();
		sg.forEach((ca, es) -> {es.forEach((cn) -> {City ci = City.valueOf(cn.substring(0, 3).toUpperCase());
				if (ci == this.city)
					C.add(cn);
				else
					s.add(cn);
			});});if (!C.contains(oldeventId) && !s.contains(oldeventId)) {
			status = false;
			msg = customerId + " is not enrolled in " + oldeventId;
		} else if (C.contains(neweventId) || s.contains(neweventId)) {
			status = false;
			msg = customerId + " is already enrolled in " + neweventId;
		} 
		if (!status) {
			String reply = "";
			if (status == true) {reply="true- Swap Event Finish!"+msg;
			}else {reply="false- Swap Event fail!"+msg;}
			System.out.println("RMImpl- "+reply);
			return reply;}
		if (nty == city) {
			re = swapUnCity(customerId, neweventId, neweventtype,oldeventId, oldeventtyped);
			status = re.getKey();
			msg = re.getValue();
		} else {
			HashMap<String, String> data = new HashMap<>();
			data.put("customerId", customerId);data.put("neweventId", neweventId);data.put("oldeventId", oldeventId);data.put("oldeventcity", oly);data.put("eventtype", neweventtype);
			re = (SimpleEntry<Boolean, String>) FuntionMembers
					.byteArrayToObject(udpCommunication(nty, data, "swapEvent"));
			status = re.getKey();
			msg = re.getValue();
		}
		LOGGER.info(String.format("METHOD[%s]; PARAMETERS%s; STATUS[%s]; SERVER_MESSAGE[%s]", "swapEvent", Arrays.asList(customerId, neweventId,oldeventId),
				status, msg));
		String reply = "";
		reply = replyValidate(status, msg, "Swap Event");
		return reply;
	}

	

	private SimpleEntry<Boolean, String> OutofCityValidator(String customerId, String eventId, String eventtype) {
		// TODO Auto-generated method stub
		boolean status = true;
		String msg = null;
		SimpleEntry<Boolean, String> result = null;
		
		//Try bs
		getbookingSchedule(customerId);
		HashMap<String, ArrayList<String>> bSchedule = BSchedule;
			
		List<String> cityEvents = new ArrayList<>();List<String> outOfcityEvents = new ArrayList<>();int f,max=0;List<String> maxCityInSameMonth = new ArrayList<>();	
		bSchedule.forEach((city, events) -> {events.forEach((event) -> {City ci= City.valueOf(event.substring(0, 3).toUpperCase());
				if (ci == this.city)
					cityEvents.add(event);
				else
					outOfcityEvents.add(event.substring(6, 8));});});
		City eventCity = City.valueOf(eventId.substring(0, 3).toUpperCase());
		if (city == eventCity) {
			if (cityEvents.contains(eventId)) {
				status = false;
				msg = eventId + " is already enrolled in "+eventId+".";
				result = new SimpleEntry<Boolean, String> (status,msg);
			}
			if (status) {
				result = bookCurrent(customerId, eventId, eventtype);
			}

		} else {
			Set<String> uniqueSet = new HashSet<String>(outOfcityEvents);
			for (String temp1 : uniqueSet) {
				 f = Collections.frequency(outOfcityEvents, temp1);
				 if (max<=f) {
					max = f;
				}
			}
			for (String temp1 : uniqueSet) {
				f = Collections.frequency(outOfcityEvents, temp1);
				if (max<=f) {
					maxCityInSameMonth.add(temp1);
				}
			}
			if ( max >= 3 && maxCityInSameMonth.contains(eventId.substring(6, 8)) ) {
				status = false;
				msg = customerId + " is already enrolled in " + 3 + " out-of-city events in same month.";
				result = new SimpleEntry<Boolean, String> (status,msg);
				
			} else {
				for (City ci : City.values()) {
					if (ci == eventCity) {
						HashMap<String, String> data = new HashMap<>();
						data.put("customerId", customerId);
						data.put("eventId", eventId);
						data.put("eventtype", eventtype);

						result = (SimpleEntry<Boolean, String>) FuntionMembers.byteArrayToObject(udpCommunication(eventCity, data, "bookEvent"));
					}
				}
			}
		}
		return result;
	}
	
	
	
	private SimpleEntry<Boolean, String> swapUnCity(String customerId, String neweventId, String neweventtype, String oldeventId, String oldeventtyped) {
		// TODO Auto-generated method stub
		boolean status = true;
		String msg = null;
		rl.lock();
		SimpleEntry<Boolean, String> re = checkEventAvailability(neweventId, neweventtype);
		if (re.getKey()) {
			dropevent(customerId, oldeventId);
			re = DEventresult;
			if (re.getKey()) {
				bookevent(customerId, neweventId, neweventtype);
				SimpleEntry<Boolean, String> any2 = BEresult;
				re = (SimpleEntry<Boolean, String>) any2;
				if(re.getKey()) {
					status = true;
					msg = "swapEvent"+" successfully";
				}else {
					bookevent(customerId, oldeventId, oldeventtyped);
					status = true;
					msg = "swapEvent"+" successfully";
				}
			} else {
				status = re.getKey();
				msg = re.getValue();
			}
		} else {
			status = re.getKey();
			msg = re.getValue();
		}
		rl.unlock();
	return re;
	}
	
	private void SendUDPForGetOthersList(String eventtype) {
		HashMap<String, Integer> result = new HashMap<>();
		for (City ci : City.values()) {
			if (ci != this.city) {result.putAll((HashMap<String, Integer>) FuntionMembers.byteArrayToObject(udpCommunication(ci, eventtype, "listEventAvailability")));
			}
		}
	}
	
	/**
	 * UDP Server for Inter-city communication
	 */
	public void UDPServer() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(city.getUdpPort());
			byte[] buffer = new byte[1000];// to stored the received data from the client.
			LOGGER.info(this.city + " UDP Server Started............");
			// non-terminating loop as the server is always in listening mode.
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				// Server waits for the request to come
				socket.receive(request); // request received

				byte[] response = processUDPRequest(request.getData());

				DatagramPacket reply = new DatagramPacket(response, response.length, request.getAddress(),
						request.getPort());// reply packet ready
				socket.send(reply);// reply sent
			}
		} catch (SocketException e) {
			LOGGER.severe("SocketException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe("IOException : " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}
	}
	private SimpleEntry<Boolean, String> aSwr(String customerId, String neweventId,
			String oldeventId, String oldCity, String newEventype) {
		SimpleEntry<Boolean, String> result;
		boolean status;
		String msg;

		try {
			rl.lock();
			result = checkEventAvailability(neweventId, newEventype);
			getbookingSchedule(customerId);
			HashMap<String, ArrayList<String>> bookingSchedule = BSchedule;
			List<String> cityEvents = new ArrayList<>();
			List<String> outOfcityEvents = new ArrayList<>();
			int f,max=0;
			List<String> maxCityInSameMonth = new ArrayList<>();
			City CustomerCi = City.valueOf(customerId.substring(0, 3).toUpperCase());
			
			bookingSchedule.forEach((city, events) -> {
				events.forEach((event) -> {
					City ci= City.valueOf(event.substring(0, 3).toUpperCase());
					if (ci == CustomerCi)
						cityEvents.add(event);			
					else
					outOfcityEvents.add(event.substring(6, 8));
				});
			});
				Set<String> uniqueSet = new HashSet<String>(outOfcityEvents);
				for (String temp1 : uniqueSet) {
					 f = Collections.frequency(outOfcityEvents, temp1);
					 if (max<=f) {
						max = f;
					}
				}
				for (String temp1 : uniqueSet) {
					f = Collections.frequency(outOfcityEvents, temp1);
					if (max<=f) {
						maxCityInSameMonth.add(temp1);
					}
				}
				if ( max >= 3 && maxCityInSameMonth.contains(neweventId.substring(6, 8)) ) {
					status = false;
					msg = customerId + " is already enrolled in " + 3
							+ " out-of-city events in same month.";
				} else {
			
			if (result.getKey()) {
				HashMap<String, String> data = new HashMap<>();
				data.put("customerId", customerId);
				data.put("eventId", oldeventId);
				dropevent(customerId, oldeventId);
				result = DEventresult;
				if (result.getKey()) {
					bookevent(customerId, neweventId, newEventype);
					SimpleEntry<Boolean, String> any2 = BEresult;
					result = (SimpleEntry<Boolean, String>) any2;
					if(result.getKey()) {
						status = true;
						msg = "swapEvent"+" successfully";
					}else {
						bookevent(customerId,oldeventId,newEventype);
						status = false;
						msg = "swapEvent"+" unsuccessful";
					}
				} else {
					status = result.getKey();
					msg = result.getValue();
				}
			} else {
				status = result.getKey();
				msg = result.getValue();
			}
			return new SimpleEntry<Boolean, String>(status, msg);
		} 
				return new SimpleEntry<Boolean, String>(status, msg);	}finally {
			rl.unlock();
		}
	}
	/**
	 * Handles the UDP request for information
	 * @param data
	 * @return
	 */
	private byte[] processUDPRequest(byte[] data) {

		byte[] response = null;
		HashMap<String, Object> request = (HashMap<String, Object>) FuntionMembers.byteArrayToObject(data);

		for (String key : request.keySet()) {

			LOGGER.info("Received UDP Socket call for method[" + key + "] with parameters[" + request.get(key) + "]");
			switch (key) {
			case "listEventAvailability":
				String type = (String) request.get(key);
				response = FuntionMembers.objectToByteArray(listAt(type));
				break;
			case "bookEvent":
				HashMap<String, String> info = (HashMap<String, String>) request.get(key);
				response = FuntionMembers.objectToByteArray(bookCurrent(info.get("customerId"),
						info.get("eventId"), info.get("eventtype")));
				break;
			case "getEventSchedule":
				String studentId = (String) request.get(key);
				response = FuntionMembers.objectToByteArray(getSschedule(studentId));
				break;
			case "dropEvent":
				info = (HashMap<String, String>) request.get(key);
				response = FuntionMembers
						.objectToByteArray(dOT(info.get("customerId"), info.get("eventId")));
				break;
			case "swapEvent":
				info = (HashMap<String, String>) request.get(key);
				response = FuntionMembers.objectToByteArray(aSwr(info.get("customerId"),
						info.get("neweventId"), info.get("oldeventId"),
						info.get("oldeventcity"), info.get("eventtype")));
				break;
			}
		}

		return response;
	}
	

	private SimpleEntry<Boolean, String> checkEventAvailability(String eventId, String eventtype) {

		boolean status = true;
		String msg = "";
		if (ServerDatabase.containsKey(eventtype )) {
			HashMap<String, HashMap<String, Object>> events = ServerDatabase.get(eventtype );

			if (events.containsKey(eventId)) {
				HashMap<String, Object> eventDetails = events.get(eventId);

				if (((Integer) eventDetails.get("capacity")
						- (Integer) eventDetails.get("customersEnrolled")) > 0) {
					status = true;

				} else {
					status = false;
					msg = eventId + " is full.";
				}
			} else {
				status = false;
				msg = eventId + " is not offered in " + eventtype + " type.";
			}
		} else {
			status = false;
			msg = "No events avialable for " + eventtype  + " type.";
		}

		return new SimpleEntry<Boolean, String>(status, msg);
	}

	/**
	 * Creates & sends the UDP request
	 * @param dept
	 * @param info
	 * @param method
	 * @return
	 */
	private byte[] udpCommunication(City ci, Object info, String method) {

		LOGGER.info("Making UPD Socket Call to " + ci + " Server for method : " + method);

		// UDP SOCKET CALL AS CLIENT
		HashMap<String, Object> data = new HashMap<>();
		byte[] response = null;
		data.put(method, info);
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			byte[] message = FuntionMembers.objectToByteArray(data);
			InetAddress remoteUdpHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, message.length, remoteUdpHost, ci.getUdpPort());
			socket.send(request);
			byte[] buffer = new byte[65556];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			socket.receive(reply);
			response = reply.getData();
		} catch (SocketException e) {
			LOGGER.severe("SocketException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe("IOException : " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}

		return response;
	}

	@SuppressWarnings("unchecked")
	public String[] listEventAvailability(String managerId, String eventtype) {
		HashMap<String, Integer> r = new HashMap<>();
		r.putAll(listAt(eventtype));
		for (City c : City.values()) {
			if (c != this.city) {
				r.putAll((HashMap<String, Integer>)FuntionMembers.byteArrayToObject(udpCommunication(c, eventtype, "listEventAvailability")));}}
		LOGGER.info(String.format("METHOD[%s]; PARAMETERS%s; STATUS[%s]; SERVER_MESSAGE[%s]", "listEventAvailability",
				Arrays.asList(managerId, eventtype), r != null, r));
		String[] ar = r.toString().split(",");
		System.out.println("Result is : "+ r.toString());
		return ar;
	}

	private HashMap<String, Integer> listAt(String eventtype) {
		HashMap<String, Integer> result = new HashMap<>();
		if (ServerDatabase.containsKey(eventtype)) {ServerDatabase.get(eventtype).forEach((event, eventDetails) -> result.put(event, (Integer) eventDetails.get("capacity")
							- (Integer) eventDetails.get("customersEnrolled")));}
		return result;
	}
	

}
