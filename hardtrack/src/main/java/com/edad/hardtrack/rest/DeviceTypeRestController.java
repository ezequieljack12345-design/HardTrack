package com.edad.hardtrack.rest;

import com.edad.hardtrack.dao.DeviceTypeDAO;
import com.edad.hardtrack.entity.DeviceType;
import com.edad.hardtrack.exception.DeviceTypeNotFoundException;
import com.edad.hardtrack.exception.ErrorResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device-types")
public class DeviceTypeRestController {

    private DeviceTypeDAO deviceTypeDAO;

    @Autowired
    public DeviceTypeRestController(DeviceTypeDAO deviceTypeDAO) {
        this.deviceTypeDAO = deviceTypeDAO;
    }

    @GetMapping
    public List<DeviceType> getAll() {
        return deviceTypeDAO.findAll();
    }

    @GetMapping("/{id}")
    public DeviceType getById(@PathVariable int id) {
        DeviceType deviceType = deviceTypeDAO.findById(id);
        if (deviceType == null) {
            throw new DeviceTypeNotFoundException(id);
        }
        return deviceType;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DeviceType deviceType) {
        if (deviceType == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "El tipo de dispositivo es obligatorio"));
        }

        String name = deviceType.getName();
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "El nombre del tipo es obligatorio"));
        }

        name = name.trim();
        if (name.length() > 45) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "El nombre no puede superar 45 caracteres"));
        }

        deviceType.setName(name);
        deviceType.setId(0);
        deviceTypeDAO.save(deviceType);
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceType);
    }

}
