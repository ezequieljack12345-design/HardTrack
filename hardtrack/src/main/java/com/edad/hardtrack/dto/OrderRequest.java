package com.edad.hardtrack.dto;

import java.util.List;

/**
 * DTO DE ENTRADA - Petición para crear pedido
 * 
 * ¿POR QUÉ USAR DTO EN LUGAR DE ENTIDAD?
 * - Solo necesitamos los IDs de dispositivos, no el objeto Device completo
 * - Define contrato claro entre cliente y API
 * - Evita sobrecarga y permite validación específica
 * - Desacopla representación de BD de datos de petición
 * 
 * Uso: POST /api/devices/order con {"deviceIds": [1, 2, 3]}
 * 
 * Ver: README_HARDTRACK_DETALLADO.md sección 5
 */
public class OrderRequest {
	private List<Integer> deviceIds;

	public OrderRequest() {
	}

	public OrderRequest(List<Integer> deviceIds) {
		this.deviceIds = deviceIds;
	}

	public List<Integer> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(List<Integer> deviceIds) {
		this.deviceIds = deviceIds;
	}
}
