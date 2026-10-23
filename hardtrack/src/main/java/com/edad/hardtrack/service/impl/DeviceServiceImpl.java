package com.edad.hardtrack.service.impl;

import com.edad.hardtrack.dao.DeviceDAO;
import com.edad.hardtrack.dto.OrderRequest;
import com.edad.hardtrack.dto.ReceiptDTO;
import com.edad.hardtrack.dto.ReceiptItemDTO;
import com.edad.hardtrack.entity.Device;
import com.edad.hardtrack.exception.DeviceNotAvailableException;
import com.edad.hardtrack.exception.InvalidOrderException;
import com.edad.hardtrack.service.DeviceService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceServiceImpl implements DeviceService {

	private DeviceDAO deviceDAO;

	@Autowired
	public DeviceServiceImpl(DeviceDAO deviceDAO) {
		this.deviceDAO = deviceDAO;
	}

	@Override
	@Transactional
	public ReceiptDTO createOrder(OrderRequest orderRequest) {
		if (orderRequest == null || orderRequest.getDeviceIds() == null || orderRequest.getDeviceIds().isEmpty()) {
			throw new InvalidOrderException("No device ids provided");
		}

		List<Integer> ids = orderRequest.getDeviceIds();
		List<Device> devices = deviceDAO.findAllByIds(ids);

		if (devices.size() != ids.size()) {
			throw new InvalidOrderException("Some devices do not exist");
		}

		for (Device device : devices) {
			if (!device.isState()) {
				throw new DeviceNotAvailableException(device.getId());
			}
		}

		List<ReceiptItemDTO> items = new ArrayList<>();
		for (Device device : devices) {
			device.setState(false);
			deviceDAO.save(device);

			String typeName = device.getType() != null ? device.getType().getName() : null;
			items.add(new ReceiptItemDTO(device.getId(), device.getDeviceDescription(), typeName));
		}

		return new ReceiptDTO(items);
	}

}
