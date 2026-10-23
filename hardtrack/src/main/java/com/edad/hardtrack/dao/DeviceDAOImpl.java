package com.edad.hardtrack.dao;

import com.edad.hardtrack.entity.Device;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DeviceDAOImpl implements DeviceDAO {

    private EntityManager entityManager;

    @Autowired
    public DeviceDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Device> findAll() {
        TypedQuery<Device> query = entityManager.createQuery("from Device", Device.class);
        return query.getResultList();
    }

    @Override
    public Device findById(int id) {
        return entityManager.find(Device.class, id);
    }

    @Override
    public List<Device> findAllByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        TypedQuery<Device> query = entityManager.createQuery("from Device d where d.id in :ids", Device.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void save(Device device) {
        entityManager.merge(device);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        Device device = entityManager.find(Device.class, id);
        if (device != null) {
            entityManager.remove(device);
        }
    }

}

