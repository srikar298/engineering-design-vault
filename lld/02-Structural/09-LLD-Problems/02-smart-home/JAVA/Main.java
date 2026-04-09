import smarthome.adapter.LegacyPlatformAdapter;
import smarthome.devices.Device;
import smarthome.devices.SmartThermostat;
import smarthome.platforms.GoogleNestAPI;
import smarthome.platforms.IPlatform;

/**
 * <h1>Smart Home Hub Demo</h1>
 * 
 * <p>Demonstrates the Bridge pattern (decoupling Devices from Platforms) 
 * successfully integrating an Adapter pattern to support 15-year-old hardware.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Smart Home Hub: Bridge + Adapter Demo          ");
        System.out.println("==================================================\n");

        System.out.println("--- Scenario 1: Modern Device on Modern Platform ---");
        // Bridge Composition
        IPlatform googlePlatform = new GoogleNestAPI();
        SmartThermostat livingRoomTemp = new SmartThermostat(googlePlatform);
        
        livingRoomTemp.turnOn();
        livingRoomTemp.setTemperature(22);

        System.out.println("\n--- Scenario 2: Swapping the Platform (Bridge Power) ---");
        System.out.println("Oh no! The Google API is down. Let's swap to the Legacy Thermostat in the basement.");
        
        // We use the exact same Abstraction (SmartThermostat)
        // But we inject the Adapter as the Bridge implementation!
        IPlatform legacyAdapter = new LegacyPlatformAdapter();
        livingRoomTemp.setPlatform(legacyAdapter);
        
        livingRoomTemp.setTemperature(22);
    }
}
