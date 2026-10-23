package com.edad.hardtrack.dto;

import java.util.List;

public class ReceiptDTO {
	private List<ReceiptItemDTO> items;
	private int totalItems;

	public ReceiptDTO() {
	}

	public ReceiptDTO(List<ReceiptItemDTO> items) {
		this.items = items;
		this.totalItems = items != null ? items.size() : 0;
	}

	public List<ReceiptItemDTO> getItems() {
		return items;
	}

	public void setItems(List<ReceiptItemDTO> items) {
		this.items = items;
		this.totalItems = items != null ? items.size() : 0;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}
}
