/*
 * COMP6231 A2
 * Tianlin Yang 40010303
 * Gaoshuo Cui 40085020
 */

package MultiThreadTest;

import java.util.Scanner;

import client.User;
import functions.City;
import functions.FuntionMembers;
import functions.Role;

public class DataInitialize {

	public static void main(String[] args) throws InterruptedException {

		System.out.println("Add some events in server now.");
		//System.out.println("Customer ID lists: ");
		//System.out.println("MTLC1001\r\n" + "OTWM2345\r\n"+
				//"MTLC2001\r\n"+"OTWC2345\r\n"+"OTWC3345\r\n"+"TORC8009\r\n"+"TORC9009\r\n");
		//------------------------------------Manager first------------------------
		String id = "OTWM2345";String idT = "TORM0001";
		User userM = new User();User userMT = new User();
		String valueM = validateUser(id, userM);String valueMT = validateUser(idT, userMT);

			System.out.println("Login Successful : " + userM);
			Thread t1 = null;Thread t2 = null;Thread t3 = null;Thread t4 = null;Thread t5 = null;Thread t6 = null;Thread t7 = null;Thread t8 = null;Thread t9 = null;Thread t10 = null;
			Thread t11 = null;Thread t12 = null;Thread t13 = null;Thread t14 = null;Thread t15 = null;Thread t16 = null;
			Thread t17 = null;Thread t18 = null;Thread t19 = null;Thread t20 = null;Thread t21 = null;
				//Register 1(eventadd),OTWA100519,3(capacity),1(conference)
				t1 = new Thread(new MuiltiManagers(userM,1,"OTWA100519",3,1,args));
				t2 = new Thread(new MuiltiManagers(userM,1,"OTWA100520",3,1,args));
				t3 = new Thread(new MuiltiManagers(userM,1,"OTWA100521",3,1,args));
				t4 = new Thread(new MuiltiManagers(userM,1,"OTWA100522",3,1,args));
				t5 = new Thread(new MuiltiManagers(userMT,1,"TORA100522",3,1,args));
				t6 = new Thread(new MuiltiManagers(userMT,1,"TORA100523",3,1,args));
				t7 = new Thread(new MuiltiManagers(userMT,1,"TORA100524",3,1,args));
				t8 = new Thread(new MuiltiManagers(userMT,1,"TORA100525",3,1,args));
				t9 = new Thread(new MuiltiManagers(userMT,1,"TORA100625",3,1,args));
				t10 = new Thread(new MuiltiManagers(userMT,1,"TORA100626",3,1,args));
				t11 = new Thread(new MuiltiManagers(userM,1,"OTWA100625",3,1,args));
				t12 = new Thread(new MuiltiManagers(userM,1,"OTWA100626",3,1,args));
				
			t1.start();
			t2.start();
			t3.start();
			t4.start();
			t5.start();
			t6.start();
			t7.start();
			t8.start();
			t9.start();
			t10.start();
			t11.start();
			t12.start();
			System.out.println("Server initilize finished!");
			
			System.out.println("Add some Swap events in server now.");
			t13 = new Thread(new MuiltiManagers(userMT,1,"TORM120725",1,1,args));
			t14 = new Thread(new MuiltiManagers(userMT,1,"TORM120726",1,1,args));
			t15 = new Thread(new MuiltiManagers(userM,1,"OTWM120725",1,1,args));
			t16 = new Thread(new MuiltiManagers(userM,1,"OTWM120726",3,1,args));
			
			t17 = new Thread(new MuiltiManagers(userMT,1,"TORM100725",3,1,args));
			t18 = new Thread(new MuiltiManagers(userMT,1,"TORM100726",3,1,args));
			t19 = new Thread(new MuiltiManagers(userMT,1,"TORM100727",3,1,args));
			t20 = new Thread(new MuiltiManagers(userMT,1,"TORM100728",3,1,args));
			t21 = new Thread(new MuiltiManagers(userM,1,"OTWM120620",3,1,args));

			
			t13.start();
			t14.start();
			t15.start();
			t16.start();
			t17.start();
			t18.start();
			t19.start();
			t20.start();
			t21.start();
		System.out.println("Server Swap initilize finished!");
	}
		//--------------------------------------------------------------------------


	private static String validateUser(final String id, final User user) {
		String returnValue = null, city, role, value;
		int userId;
		// string length !=8
		if (id.length() != 8)
			return "Seems to be an invalid id(length not equal to8).";

		city = id.substring(0, 3);
		role = id.substring(3, 4);
		value = id.substring(4);

		// validate city
		if (!FuntionMembers.cityMatch(city))
			return "Your city('" + city + "') isn't recognized.";
		// validate role
		else if (!FuntionMembers.roleMatch(role))
			return "Your role('" + role + "') isn't recognized.";

		try {
			// validate user id (integer value)
			userId = Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return "Your id('" + value + "') isn't recognized.";
		}
		returnValue = "success";
		user.setcity(City.valueOf(city.toUpperCase()));
		user.setRole(Role.fromString(role.toUpperCase()));
		user.setId(userId);
		return returnValue;
	}

}
