package com.edad.hardtrack.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.edad.hardtrack.dao.DeviceDAO;
import com.edad.hardtrack.dto.OrderRequest;
import com.edad.hardtrack.dto.ReceiptDTO;
import com.edad.hardtrack.entity.Device;
import com.edad.hardtrack.entity.DeviceType;
import com.edad.hardtrack.exception.DeviceNotAvailableException;
import com.edad.hardtrack.exception.InvalidOrderException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeviceServiceImplTest {

    @Mock
    private DeviceDAO deviceDAO;

    private DeviceServiceImpl deviceService;

    @BeforeEach
    void setUp() {
        deviceService = new DeviceServiceImpl(deviceDAO);
    }

    @Test
    void createOrder_shouldThrowWhenOrderRequestIsNull() {
        assertThrows(InvalidOrderException.class, () -> deviceService.createOrder(null));
        verify(deviceDAO, never()).findAllByIds(any());
    }

    @Test
    void createOrder_shouldThrowWhenDeviceIdsNull() {
        OrderRequest request = new OrderRequest(null);
        assertThrows(InvalidOrderException.class, () -> deviceService.createOrder(request));
        verify(deviceDAO, never()).findAllByIds(any());
    }

    @Test
    void createOrder_shouldThrowWhenDeviceIdsEmpty() {
        OrderRequest request = new OrderRequest(Collections.emptyList());
        assertThrows(InvalidOrderException.class, () -> deviceService.createOrder(request));
        verify(deviceDAO, never()).findAllByIds(any());
    }

    @Test
    void createOrder_shouldThrowWhenSomeDevicesDoNotExist() {
        OrderRequest request = new OrderRequest(Arrays.asList(1, 2));
        Device device = new Device(new DeviceType("Laptop"), "Dell XPS", true);
        device.setId(1);
        when(deviceDAO.findAllByIds(Arrays.asList(1, 2))).thenReturn(Collections.singletonList(device));

        assertThrows(InvalidOrderException.class, () -> deviceService.createOrder(request));
        verify(deviceDAO).findAllByIds(Arrays.asList(1, 2));
        verify(deviceDAO, never()).save(any());
    }

    @Test
    void createOrder_shouldThrowWhenDeviceNotAvailable() {
        OrderRequest request = new OrderRequest(Arrays.asList(1));
        Device device = new Device(new DeviceType("Phone"), "iPhone", false);
        device.setId(1);
        when(deviceDAO.findAllByIds(Arrays.asList(1))).thenReturn(Collections.singletonList(device));

        DeviceNotAvailableException ex = assertThrows(DeviceNotAvailableException.class,
                () -> deviceService.createOrder(request));
        assertTrue(ex.getMessage().contains("1"));
        verify(deviceDAO).findAllByIds(Arrays.asList(1));
        verify(deviceDAO, never()).save(any());
    }

    @Test
    void createOrder_shouldCreateOrderWhenValid() {
        OrderRequest request = new OrderRequest(Arrays.asList(1, 2));
        DeviceType type = new DeviceType("Laptop");
        Device device1 = new Device(type, "Dell XPS", true);
        device1.setId(1);
        Device device2 = new Device(type, "HP Spectre", true);
        device2.setId(2);

        when(deviceDAO.findAllByIds(Arrays.asList(1, 2))).thenReturn(Arrays.asList(device1, device2));

        ReceiptDTO receipt = deviceService.createOrder(request);

        assertNotNull(receipt);
        assertEquals(2, receipt.getTotalItems());
        assertEquals(2, receipt.getItems().size());
        assertEquals(1, receipt.getItems().get(0).getDeviceId());
        assertEquals("Dell XPS", receipt.getItems().get(0).getDeviceDescription());
        assertEquals("Laptop", receipt.getItems().get(0).getDeviceTypeName());

        assertEquals(false, device1.isState());
        assertEquals(false, device2.isState());

        verify(deviceDAO).findAllByIds(Arrays.asList(1, 2));
        verify(deviceDAO, times(2)).save(any(Device.class));
    }
}
