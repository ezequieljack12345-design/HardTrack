package com.edad.hardtrack.exception;

public class DeviceTypeNotFoundException extends RuntimeException {
    public DeviceTypeNotFoundException(String message) {
        super(message);
    }

    public DeviceTypeNotFoundException(int id) {
        super("DeviceType id not found - " + id);
    }
}

