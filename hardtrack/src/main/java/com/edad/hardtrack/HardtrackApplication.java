package com.edad.hardtrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CLASE PRINCIPAL - Punto de entrada de la aplicación HardTrack
 * 
 * @SpringBootApplication habilita:
 * - @Configuration: configuración de Spring
 * - @EnableAutoConfiguration: autoconfiguración basada en classpath
 * - @ComponentScan: escaneo de componentes en com.edad.hardtrack
 * 
 * ARQUITECTURA: REST API con capas Controller -> Service -> DAO -> Entity (JPA/Hibernate)
 * Ver documentación detallada: /HardTrack/hardtrack/README_HARDTRACK_DETALLADO.md
 * o /HardTrack/data/DOCUMENTACION_HARDTRACK.md
 * 
 * @author HardTrack
 * @see <a href="file:../../../../../../README_HARDTRACK_DETALLADO.md">Documentación completa</a>
 */
@SpringBootApplication
public class HardtrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(HardtrackApplication.class, args);
	}

}
