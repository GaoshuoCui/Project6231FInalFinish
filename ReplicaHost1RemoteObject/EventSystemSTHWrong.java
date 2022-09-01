package ReplicaHost1RemoteObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import functions.City;


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
    	List<String> EventList = new ArrayList<>();
       EventList.add("MTL1--2");

        return translateStringArray(EventList);
    }
    private String[] translateStringArray(List<String> courseList) {
        String[] res = new String[courseList.size()];
        for(int i = 0 ; i < courseList.size() ; i ++){
            res[i] = courseList.get(i);
        }
        return res;
    }



    public String bookevent(String customerID, String eventID, String eventtype) {
        return "Fail";
    }

    public String dropevent(String customerID, String eventID) {
       return "Fail";
    }

    public String[] getbookingSchedule(String customerID) {
    	List<String> EventList = new ArrayList<>();
        EventList.add(new String("MTL1--Morning"));
        return translateStringArray(EventList);
        
    }

    public String swapEvent(String customerID, String neweventID, String oldeventID) {
        return "Fail";
    }

}

