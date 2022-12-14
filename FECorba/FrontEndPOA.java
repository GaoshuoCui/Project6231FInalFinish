package FECorba;


/**
* FECorba/FrontEndPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/TLIN/eclipse-workspace/a2.zip_expanded/a2/CORBAa2Lin/FrontEndCorba.idl
* Monday, August 5, 2019 1:22:00 o'clock AM EDT
*/

public abstract class FrontEndPOA extends org.omg.PortableServer.Servant
 implements FECorba.FrontEndOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("addEvent", new java.lang.Integer (0));
    _methods.put ("removeEvent", new java.lang.Integer (1));
    _methods.put ("listEventAvailability", new java.lang.Integer (2));
    _methods.put ("bookevent", new java.lang.Integer (3));
    _methods.put ("dropevent", new java.lang.Integer (4));
    _methods.put ("getbookingSchedule", new java.lang.Integer (5));
    _methods.put ("swapEvent", new java.lang.Integer (6));
    _methods.put ("shutdown", new java.lang.Integer (7));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // FECorba/FrontEnd/addEvent
       {
         String managerId = in.read_string ();
         String eventId = in.read_string ();
         String eventtype = in.read_string ();
         int capacity = in.read_long ();
         String $result = null;
         $result = this.addEvent (managerId, eventId, eventtype, capacity);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // FECorba/FrontEnd/removeEvent
       {
         String managerId = in.read_string ();
         String eventId = in.read_string ();
         String eventtype = in.read_string ();
         String $result = null;
         $result = this.removeEvent (managerId, eventId, eventtype);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 2:  // FECorba/FrontEnd/listEventAvailability
       {
         String managerId = in.read_string ();
         String eventtype = in.read_string ();
         String $result[] = null;
         $result = this.listEventAvailability (managerId, eventtype);
         out = $rh.createReply();
         FECorba.listHelper.write (out, $result);
         break;
       }

       case 3:  // FECorba/FrontEnd/bookevent
       {
         String customerId = in.read_string ();
         String eventId = in.read_string ();
         String eventtype = in.read_string ();
         String $result = null;
         $result = this.bookevent (customerId, eventId, eventtype);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 4:  // FECorba/FrontEnd/dropevent
       {
         String customerId = in.read_string ();
         String eventId = in.read_string ();
         String $result = null;
         $result = this.dropevent (customerId, eventId);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 5:  // FECorba/FrontEnd/getbookingSchedule
       {
         String customerId = in.read_string ();
         String $result[] = null;
         $result = this.getbookingSchedule (customerId);
         out = $rh.createReply();
         FECorba.listHelper.write (out, $result);
         break;
       }

       case 6:  // FECorba/FrontEnd/swapEvent
       {
         String customerId = in.read_string ();
         String neweventId = in.read_string ();
         String neweventtype = in.read_string ();
         String oldeventId = in.read_string ();
         String oldeventtype = in.read_string ();
         String $result = null;
         $result = this.swapEvent (customerId, neweventId, neweventtype, oldeventId, oldeventtype);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 7:  // FECorba/FrontEnd/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:FECorba/FrontEnd:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public FrontEnd _this() 
  {
    return FrontEndHelper.narrow(
    super._this_object());
  }

  public FrontEnd _this(org.omg.CORBA.ORB orb) 
  {
    return FrontEndHelper.narrow(
    super._this_object(orb));
  }


} // class FrontEndPOA
