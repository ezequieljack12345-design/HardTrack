package com.edad.hardtrack.dto;

import java.util.List;

public class OrderRequest {
	private List<Integer> deviceIds;

	public OrderRequest() {
	}

	public OrderRequest(List<Integer> deviceIds) {
		this.deviceIds = deviceIds;
	}

	public List<Integer> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(List<Integer> deviceIds) {
		this.deviceIds = deviceIds;
	}
}
