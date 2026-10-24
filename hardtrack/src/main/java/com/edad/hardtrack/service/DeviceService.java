package com.edad.hardtrack.service;

import com.edad.hardtrack.dto.OrderRequest;
import com.edad.hardtrack.dto.ReceiptDTO;

/**
 * INTERFAZ DE SERVICIO - Capa de lógica de negocio
 * 
 * ¿POR QUÉ USAR SERVICIOS?
 * - Separación de responsabilidades: Los controladores no contienen reglas de negocio
 * - Transaccionalidad: Permite agrupar operaciones en una transacción atómica (@Transactional)
 * - Reutilización: La lógica puede ser usada desde múltiples puntos
 * - Testeabilidad: Fácil de mockear en tests unitarios
 * - Orquestación: Coordina validaciones, múltiples DAOs y transformaciones DTO<->Entity
 * 
 * Ver: README_HARDTRACK_DETALLADO.md sección 7
 */
public interface DeviceService {
	/**
	 * Crea un pedido validando disponibilidad y generando recibo.
	 * 
	 * @param orderRequest DTO con lista de IDs de dispositivos a pedir
	 * @return ReceiptDTO con detalle de dispositivos pedidos
	 * @throws com.edad.hardtrack.exception.InvalidOrderException si pedido vacío o IDs inválidos
	 * @throws com.edad.hardtrack.exception.DeviceNotAvailableException si algún dispositivo no disponible
	 */
	ReceiptDTO createOrder(OrderRequest orderRequest);
}
