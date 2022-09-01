package ReplicaHost3RemoteObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import ReplicaHost3.City;

public class EventSystemSTHWrong {


	public Logger logger;
	public boolean bug;
    public EventSystemSTHWrong(){ }

    public String addEvent(String eventID, String eventtype) {
        return "Fail";
    }

    public String removeEvent(String eventID, String eventtype) {
        return "Fail";
    }

    public String[] listEventAvailability(String eventtype) {
    	String Event = "1 : {Conference = Wrong 1, Bug 2, Call 3,"+eventtype;
    	String[] EventList = Event.split(",");

        return EventList;
    }



    public String bookevent(String customerID, String eventID, String eventtype) {
        return "Fail";
    }

    public String dropevent(String customerID, String eventID) {
       return "Fail";
    }

    public String[] getbookingSchedule(String customerID) {
    	String Event = "1 : {Conference = Wrong 1, Bug 2, Call 3,"+customerID;
    	String[] EventList = Event.split(",");

        return EventList;
        
    }

    public String swapEvent(String customerID, String neweventID, String oldeventID) {
        return "Fail";
    }

}

