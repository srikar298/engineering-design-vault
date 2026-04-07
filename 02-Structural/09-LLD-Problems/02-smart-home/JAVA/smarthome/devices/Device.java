package smarthome.devices;

import smarthome.platforms.IPlatform;

/**
 * <h1>Bridge Abstraction Interface</h1>
 * 
 * <p>Holds the reference to the underlying Platform Implementation.
 */
public abstract class Device {
    protected IPlatform platform;

    public Device(IPlatform platform) {
        this.platform = platform;
    }

    public void setPlatform(IPlatform platform) {
        this.platform = platform;
    }

    // High level abstractions
    public abstract void turnOn();
    public abstract void turnOff();
}
