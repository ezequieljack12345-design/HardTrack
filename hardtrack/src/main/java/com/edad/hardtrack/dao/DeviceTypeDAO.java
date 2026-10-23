/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.edad.hardtrack.dao;

import com.edad.hardtrack.entity.DeviceType;
import java.util.List;

public interface DeviceTypeDAO {
    DeviceType findById(int id);
    List<DeviceType> findAll();
}