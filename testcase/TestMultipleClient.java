package testcase;

import FECorba.FrontEnd;
import FECorba.FrontEndHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class TestMultipleClient {

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
            e.printStackTrace(System.out);
        }
        return frontEnd;
    }

    public static void s1(){
        Runnable t1 = () -> {
            FrontEnd servent1 = connect("frontEnd");
            System.out.println(servent1.addEvent("MTLM1111","MTLA100519","CONFERENCES", 3));
            System.out.println(servent1.bookevent("MTLC1111","MTLA100519","CONFERENCES"));
            
        };

        Runnable t2 = () -> {
            FrontEnd servent2 = connect("frontEnd");
            System.out.println(servent2.bookevent("MTLC2222","MTLA100519","CONFERENCES"));
        };

        Runnable t3 = () -> {
            FrontEnd servent3 = connect("frontEnd");
            System.out.println(servent3.bookevent("MTLC3333","MTLA100519","CONFERENCES"));
        };

        Runnable t4 = () -> {
            FrontEnd servent4 = connect("frontEnd");
            System.out.println(servent4.bookevent("MTLC4444","MTLA100519","CONFERENCES"));
        };

        Runnable t5 = () -> {
            FrontEnd servent5 = connect("frontEnd");
            System.out.println(servent5.bookevent("MTLC5555","MTLA100519","CONFERENCES"));
        };

        Thread thread1 = new Thread(t1);
        Thread thread2 = new Thread(t2);
        Thread thread3 = new Thread(t3);
        Thread thread4 = new Thread(t4);
        Thread thread5 = new Thread(t5);

        thread1.start();
        //thread2.start();
        //thread3.start();
        //thread4.start();
       // thread5.start();
    }

    public static void s2(){
        FrontEnd servent1 = connect("frontEnd");

        Runnable t1 = () -> {
            System.out.println(servent1.dropevent("MTLC1111","MTLA100519"));
        };

        Runnable t2 = () -> {
            System.out.println(servent1.bookevent("MTLC3333","MTLA100519","CONFERENCES"));
        };

        Runnable t3 = () -> {
            System.out.println(servent1.bookevent("MTLC4444","MTLA100519","CONFERENCES"));
        };

        Runnable t4 = () -> {
            System.out.println(servent1.bookevent("MTLC5555","MTLA100519","CONFERENCES"));
        };

        Runnable t5 = () -> {
            System.out.println(servent1.bookevent("MTLC6666","MTLA100519","CONFERENCES"));
        };

        Thread thread1 = new Thread(t1);
        Thread thread2 = new Thread(t2);
        Thread thread3 = new Thread(t3);
        Thread thread4 = new Thread(t4);
        Thread thread5 = new Thread(t5);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
    }

    public static void s3(){
        FrontEnd servent1 = connect("frontEnd");

        Runnable t1 = () -> {
            System.out.println(servent1.swapEvent("MTLC3333","OTWA110619","SEMINARS","MTLA100519","CONFERENCES"));
        };

        Runnable t2 = () -> {
            System.out.println(servent1.bookevent("MTLC3333","MTLA100519","CONFERENCES"));
        };

        Runnable t3 = () -> {
            System.out.println(servent1.bookevent("MTLC4444","MTLA100519","CONFERENCES"));
        };

        Runnable t4 = () -> {
            System.out.println(servent1.bookevent("MTLC5555","MTLA100519","CONFERENCES"));
        };

        Runnable t5 = () -> {
            System.out.println(servent1.bookevent("MTLC6666","MTLA100519","CONFERENCES"));
        };

        Thread thread1 = new Thread(t1);
        Thread thread2 = new Thread(t2);
        Thread thread3 = new Thread(t3);
        Thread thread4 = new Thread(t4);
        Thread thread5 = new Thread(t5);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
    }

    public static void checkdeadlock(){
        FrontEnd servent1 = connect("frontEnd");

        Runnable t1 = () -> {
            System.out.println(servent1.addEvent("OTWM1111","OTWA120819","CONFERENCES",1));
        };

        Runnable t2 = () -> {
            System.out.println(servent1.addEvent("OTWM1111","OTWA120919","TRADESHOWS",1));
        };

        Runnable t3 = () -> {
            System.out.println(servent1.bookevent("MTLC4444","OTWA120819","CONFERENCES"));
        };

        Runnable t4 = () -> {
            System.out.println(servent1.bookevent("MTLC5555","OTWA120919","TRADESHOWS"));
        };

        Runnable t5 = () -> {
            System.out.println(servent1.swapEvent("MTLC4444","OTWA120919","TRADESHOWS","OTWA120819","CONFERENCES"));
        };

        Runnable t6 = () -> {
            System.out.println(servent1.swapEvent("MTLC5555","OTWA120819","CONFERENCES","OTWA120919","TRADESHOWS"));
        };

        Thread thread1 = new Thread(t1);
        Thread thread2 = new Thread(t2);
        Thread thread3 = new Thread(t3);
        Thread thread4 = new Thread(t4);
        Thread thread5 = new Thread(t5);
        Thread thread6 = new Thread(t6);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
    }

    public static void main(String[] args) {
//        s1();
//        s2();
//        s3();
        checkdeadlock();
    }
}
