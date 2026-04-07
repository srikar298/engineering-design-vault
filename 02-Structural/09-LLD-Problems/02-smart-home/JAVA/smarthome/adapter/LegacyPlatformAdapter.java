package smarthome.adapter;

import smarthome.platforms.IPlatform;

/**
 * <h1>The Adapter</h1>
 * 
 * <p>Implements the modern IPlatform interface so it can be cleanly bridged to 
 * the modern Device abstractions. Translates modern HTTP-style commands into 
 * legacy raw TCP protocols.
 */
public class LegacyPlatformAdapter implements IPlatform {
    
    private final OldTcpThermostat legacyThermostat;

    public LegacyPlatformAdapter() {
        this.legacyThermostat = new OldTcpThermostat();
    }

    @Override
    public void executeAction(String action, String value) {
        System.out.println("   [Adapter] Translating modern command ('" + action + "') to TCP...");
        
        legacyThermostat.openSocket();
        
        // Translate the command
        String tcpString = "CMD=" + action.toUpperCase() + ";VAL=" + value;
        legacyThermostat.sendTcpPayload(tcpString.getBytes());
        
        legacyThermostat.closeSocket();
    }
}
