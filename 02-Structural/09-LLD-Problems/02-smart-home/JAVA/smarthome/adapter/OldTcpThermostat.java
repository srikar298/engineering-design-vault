package smarthome.adapter;

/**
 * <h1>The Adaptee</h1>
 * 
 * <p>A 15-year-old legacy Thermostat. It knows nothing about HTTP or the modern 
 * IPlatform interface. It speaks raw TCP socket commands.
 */
public class OldTcpThermostat {
    
    public void openSocket() {
        System.out.println("   [Legacy TCP] Opening socket connection on port 8080...");
    }
    
    public void sendTcpPayload(byte[] payload) {
        System.out.println("   [Legacy TCP] Sending raw bytes: " + new String(payload));
    }
    
    public void closeSocket() {
        System.out.println("   [Legacy TCP] Closing socket.");
    }
}
