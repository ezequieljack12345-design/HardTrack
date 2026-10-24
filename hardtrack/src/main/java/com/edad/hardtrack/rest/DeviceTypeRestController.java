package com.edad.hardtrack.rest;

import com.edad.hardtrack.dao.DeviceTypeDAO;
import com.edad.hardtrack.entity.DeviceType;
import com.edad.hardtrack.exception.DeviceTypeNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

}

