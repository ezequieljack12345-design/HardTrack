package com.edad.hardtrack.exception;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String message) {
        super(message);
    }

    public DeviceNotFoundException(int id) {
        super("Device id not found - " + id);
    }
}

