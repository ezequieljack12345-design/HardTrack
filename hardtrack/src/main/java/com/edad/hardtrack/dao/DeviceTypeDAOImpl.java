/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edad.hardtrack.dao;

import com.edad.hardtrack.entity.DeviceType;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository

public class DeviceTypeDAOImpl implements DeviceTypeDAO{
    
    private EntityManager entityManager;
        
        @Autowired
        public DeviceTypeDAOImpl(EntityManager entityManager){
            this.entityManager = entityManager;
        }
        
        @Override
        public DeviceType findById(int id){
            return entityManager.find(DeviceType.class, id);
        }
        
        
}
