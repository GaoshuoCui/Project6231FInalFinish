/*
 * COMP6231 A1
 * Tianlin Yang 40010303
 * Gaoshuo Cui 40085020
 */
package ReplicaHost2;

//Define port for City TOR MTL and OTW
public enum City {
	TOR(1100), MTL(1101), OTW(1102);

	int udpPort;

	private City(int udpPort) {
		this.udpPort = udpPort;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public static  boolean cityExist(String city) {
		for (City c : City.values()) {
			if (c.toString().equals(city.toUpperCase()))
				return true;
		}
		return false;
	}
}
