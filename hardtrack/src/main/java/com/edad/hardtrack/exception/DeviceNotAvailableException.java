package com.edad.hardtrack.exception;

public class DeviceNotAvailableException extends RuntimeException {
    public DeviceNotAvailableException(String message) {
        super(message);
    }

    public DeviceNotAvailableException(int id) {
        super("Device with id " + id + " is not available");
    }
}

