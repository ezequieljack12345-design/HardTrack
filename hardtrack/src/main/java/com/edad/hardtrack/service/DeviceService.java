package com.edad.hardtrack.service;

import com.edad.hardtrack.dto.OrderRequest;
import com.edad.hardtrack.dto.ReceiptDTO;

public interface DeviceService {
	ReceiptDTO createOrder(OrderRequest orderRequest);
}
