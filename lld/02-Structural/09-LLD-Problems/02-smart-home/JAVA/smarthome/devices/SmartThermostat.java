package smarthome.devices;

import smarthome.platforms.IPlatform;

public class SmartThermostat extends Device {

    public SmartThermostat(IPlatform platform) {
        super(platform);
    }

    @Override
    public void turnOn() {
        System.out.println("\n[Thermostat] Powering on HVAC system...");
        platform.executeAction("hvac_power", "ON");
    }

    @Override
    public void turnOff() {
        System.out.println("\n[Thermostat] Shutting down HVAC...");
        platform.executeAction("hvac_power", "OFF");
    }
    
    public void setTemperature(int degrees) {
        System.out.println("\n[Thermostat] Setting target temperature to " + degrees + "°C...");
        platform.executeAction("set_temp", String.valueOf(degrees));
    }
}
