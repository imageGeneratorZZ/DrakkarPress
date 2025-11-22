# Estado Migración Java

- Backend actual: Spring Boot 3.5.3, Java 21 (pom.xml)
- Build: Maven Wrapper incluido (`mvnw.cmd`)
- Dependencias clave: Web, Security, JPA, WebFlux, Actuator, Mail, Stripe, AWS S3, Micrometer.
- Health: `/api/health` (custom) y `/actuator/health` (Actuator)

## Conclusión
No se requiere migración adicional: ya está en la línea actual (Boot 3.5.x + Java 21). Mantener parches de seguridad.

## Recomendaciones
- Activar builds de CI para validar compilación (Windows/Ubuntu) en PRs.
- Revisar uso de Lombok (hay script `remove-lombok.ps1` por si se decide eliminar).
- Añadir tests de integración mínimos para `/api/health` y repositorios JPA.
- Configurar Prometheus/Grafana si se desplegará monitoreo.
