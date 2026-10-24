package com.edad.hardtrack.rest;

import com.edad.hardtrack.dao.DeviceDAO;
import com.edad.hardtrack.dto.OrderRequest;
import com.edad.hardtrack.dto.ReceiptDTO;
import com.edad.hardtrack.entity.Device;
import com.edad.hardtrack.exception.DeviceNotFoundException;
import com.edad.hardtrack.service.DeviceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CONTROLADOR REST - Endpoints para gestión de dispositivos
 * 
 * Reglas:
 * - GET: público
 * - POST /order: solo USER (anónimo se trata como sin rol ADMIN). ADMIN bloqueado por seguridad
 * - POST/PUT/DELETE: solo ADMIN
 */
@RestController
@RequestMapping("/api/devices")
public class DeviceRestController {

    private DeviceDAO deviceDAO;
    private DeviceService deviceService;

    @Autowired
    public DeviceRestController(DeviceDAO deviceDAO, DeviceService deviceService) {
        this.deviceDAO = deviceDAO;
        this.deviceService = deviceService;
    }

    @GetMapping
    public List<Device> getAll() {
        return deviceDAO.findAll();
    }

    @GetMapping("/{id}")
    public Device getById(@PathVariable int id) {
        Device device = deviceDAO.findById(id);
        if (device == null) {
            throw new DeviceNotFoundException(id);
        }
        return device;
    }

    @PostMapping
    public Device addDevice(@RequestBody Device device) {
        device.setId(0);
        deviceDAO.save(device);
        return device;
    } 

    @PutMapping
    public Device updateDevice(@RequestBody Device device) {
        deviceDAO.save(device);
        return device;
    }

    @DeleteMapping("/{id}")
    public String deleteDevice(@PathVariable int id) {
        Device device = deviceDAO.findById(id);
        if (device == null) {
            throw new DeviceNotFoundException(id);
        }
        deviceDAO.deleteById(id);
        return "Deleted device id - " + id;
    }

    @PostMapping("/order")
    public ReceiptDTO createOrder(@RequestBody OrderRequest orderRequest, Authentication authentication) {
        // Bloquear ADMIN explícitamente
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new org.springframework.security.access.AccessDeniedException("Administrators cannot create orders");
        }
        return deviceService.createOrder(orderRequest);
    }

}
