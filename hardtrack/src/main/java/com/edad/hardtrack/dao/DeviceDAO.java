package com.edad.hardtrack.dao;

import com.edad.hardtrack.entity.Device;
import java.util.List;

public interface DeviceDAO {
    List<Device> findAll();
    Device findById(int id);
    List<Device> findAllByIds(List<Integer> ids);
    void save(Device device);
    void deleteById(int id);
}

