package com.edad.hardtrack.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.edad.hardtrack.dao.DeviceTypeDAO;
import com.edad.hardtrack.entity.DeviceType;
import com.edad.hardtrack.exception.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DeviceTypeRestControllerTest {

    @Mock
    private DeviceTypeDAO deviceTypeDAO;

    private DeviceTypeRestController controller;

    @BeforeEach
    void setUp() {
        controller = new DeviceTypeRestController(deviceTypeDAO);
    }

    @Test
    void create_shouldTrimNameAndReturnCreatedType() {
        DeviceType deviceType = new DeviceType("  Portátil  ");
        deviceType.setId(12);

        ResponseEntity<?> response = controller.create(deviceType);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Portátil", deviceType.getName());
        assertEquals(0, deviceType.getId());
        verify(deviceTypeDAO).save(deviceType);
    }

    @Test
    void create_shouldRejectBlankName() {
        ResponseEntity<?> response = controller.create(new DeviceType("   "));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        verify(deviceTypeDAO, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void create_shouldRejectNullType() {
        ResponseEntity<?> response = controller.create(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        verify(deviceTypeDAO, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void create_shouldRejectNameLongerThanDatabaseColumn() {
        ResponseEntity<?> response = controller.create(new DeviceType("a".repeat(46)));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        verify(deviceTypeDAO, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
