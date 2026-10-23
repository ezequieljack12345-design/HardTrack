package com.edad.hardtrack.dto;

public class ReceiptItemDTO {
	private int deviceId;
	private String deviceDescription;
	private String deviceTypeName;

	public ReceiptItemDTO() {
	}

	public ReceiptItemDTO(int deviceId, String deviceDescription, String deviceTypeName) {
		this.deviceId = deviceId;
		this.deviceDescription = deviceDescription;
		this.deviceTypeName = deviceTypeName;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceDescription() {
		return deviceDescription;
	}

	public void setDeviceDescription(String deviceDescription) {
		this.deviceDescription = deviceDescription;
	}

	public String getDeviceTypeName() {
		return deviceTypeName;
	}

	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
	}
}
