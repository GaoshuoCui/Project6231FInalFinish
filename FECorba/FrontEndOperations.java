package FECorba;


/**
* FECorba/FrontEndOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/TLIN/eclipse-workspace/a2.zip_expanded/a2/CORBAa2Lin/FrontEndCorba.idl
* Monday, August 5, 2019 1:22:00 o'clock AM EDT
*/

public interface FrontEndOperations 
{
  String addEvent (String managerId, String eventId, String eventtype, int capacity);
  String removeEvent (String managerId, String eventId, String eventtype);
  String[] listEventAvailability (String managerId, String eventtype);
  String bookevent (String customerId, String eventId, String eventtype);
  String dropevent (String customerId, String eventId);
  String[] getbookingSchedule (String customerId);
  String swapEvent (String customerId, String neweventId, String neweventtype, String oldeventId, String oldeventtype);
  void shutdown ();
} // interface FrontEndOperations
