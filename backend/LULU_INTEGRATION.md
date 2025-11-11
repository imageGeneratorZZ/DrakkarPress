# 🖨️ Integración Lulu.com - Impresión Bajo Demanda

## 🔑 Credenciales Configuradas

✅ **Client Key**: `a10cc795-35a4-4239-ae41-f78e6abb0df0`  
✅ **Client Secret**: `sIyhz2KiOoJfHAcRxkLETMoq6LquCc87`  
✅ **Base64 Auth**: `Basic YTEwY2M3OTUtMzVhNC00MjM5LWFlNDEtZjc4ZTZhYmIwZGYwOnNJeWh6MktpT29KZkhBY1J4a0xFVE1vcTZMcXVDYzg3`

---

## 📋 Capacidades de Lulu.com

### Formatos Soportados:
- 📖 **Libros de tapa blanda** (paperback)
- 📘 **Libros de tapa dura** (hardcover)
- 📔 **Cuadernos y revistas**
- 🖼️ **Pósters y prints**

### Tamaños Estándar:
- **5" × 8"** (12.7cm × 20.32cm) - Novela compacta
- **6" × 9"** (15.24cm × 22.86cm) - Estándar industria
- **8.5" × 11"** (21.59cm × 27.94cm) - Tamaño carta
- **8" × 10"** (20.32cm × 25.4cm) - Libros ilustrados

### Opciones de Papel:
- **Crema 60#** (estándar para novelas)
- **Blanco 60#** (libros ilustrados/técnicos)
- **Blanco 70#** (premium)

### Tipos de Encuadernación:
- **Perfect Binding** (pegado)
- **Coil Binding** (espiral)
- **Saddle Stitch** (grapado - para revistas)

---

## 🔌 API Endpoints Principales

### Base URL:
```
https://api.lulu.com/v1
```

### Autenticación:
```http
Authorization: Basic YTEwY2M3OTUtMzVhNC00MjM5LWFlNDEtZjc4ZTZhYmIwZGYwOnNJeWh6MktpT29KZkhBY1J4a0xFVE1vcTZMcXVDYzg3
```

### Endpoints Clave:

#### 1. Crear Proyecto de Libro
```http
POST /print-jobs/
Content-Type: application/json

{
  "line_items": [{
    "page_count": 200,
    "pod_package_id": "0600X0900BWSTDPB060UW444MNG",
    "title": "Título del Libro",
    "cover": "https://url-to-cover.pdf",
    "interior": "https://url-to-interior.pdf",
    "quantity": 1
  }],
  "shipping_address": {
    "name": "Cliente",
    "street1": "Dirección",
    "city": "Ciudad",
    "state_code": "Estado",
    "postcode": "CP",
    "country_code": "MX"
  },
  "shipping_level": "STANDARD"
}
```

#### 2. Obtener Precios
```http
POST /print-job-cost-calculations/
Content-Type: application/json

{
  "line_items": [{
    "page_count": 200,
    "pod_package_id": "0600X0900BWSTDPB060UW444MNG",
    "quantity": 1
  }],
  "shipping_address": {
    "country_code": "MX"
  },
  "shipping_level": "STANDARD"
}
```

#### 3. Ver Catálogo de Productos
```http
GET /print-products/
```

#### 4. Verificar Estado de Orden
```http
GET /print-jobs/{print_job_id}/
```

---

## 🛠️ Implementación en DrakkarPress Backend

### 1. Crear Servicio de Lulu

```java
// backend/src/main/java/com/drakkarpress/service/LuluPrintService.java

package com.drakkarpress.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LuluPrintService {
    
    @Value("${lulu.api.url}")
    private String luluApiUrl;
    
    @Value("${lulu.api.base64}")
    private String luluAuthBase64;
    
    private final RestTemplate restTemplate;
    
    public LuluPrintService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Crear orden de impresión en Lulu
     */
    public LuluPrintJobResponse createPrintJob(LuluPrintJobRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", luluAuthBase64);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<LuluPrintJobRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<LuluPrintJobResponse> response = restTemplate.postForEntity(
            luluApiUrl + "/print-jobs/",
            entity,
            LuluPrintJobResponse.class
        );
        
        return response.getBody();
    }
    
    /**
     * Calcular costo de impresión
     */
    public LuluCostResponse calculateCost(LuluCostRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", luluAuthBase64);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<LuluCostRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<LuluCostResponse> response = restTemplate.postForEntity(
            luluApiUrl + "/print-job-cost-calculations/",
            entity,
            LuluCostResponse.class
        );
        
        return response.getBody();
    }
    
    /**
     * Obtener catálogo de productos disponibles
     */
    public LuluProductCatalog getProductCatalog() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", luluAuthBase64);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<LuluProductCatalog> response = restTemplate.exchange(
            luluApiUrl + "/print-products/",
            HttpMethod.GET,
            entity,
            LuluProductCatalog.class
        );
        
        return response.getBody();
    }
    
    /**
     * Verificar estado de orden
     */
    public LuluPrintJobStatus getPrintJobStatus(String printJobId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", luluAuthBase64);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<LuluPrintJobStatus> response = restTemplate.exchange(
            luluApiUrl + "/print-jobs/" + printJobId + "/",
            HttpMethod.GET,
            entity,
            LuluPrintJobStatus.class
        );
        
        return response.getBody();
    }
}
```

---

## 📦 Package IDs Comunes

### Tapa Blanda (Paperback) - Blanco y Negro:
```
0600X0900BWSTDPB060UW444MNG  // 6×9", BW, Crema 60#
0600X0900BWSTDPB060UW444GXX  // 6×9", BW, Blanco 60#
0500X0800BWSTDPB060UW444MNG  // 5×8", BW, Crema 60#
0850X1100BWSTDPB060UW444GXX  // 8.5×11", BW, Blanco 60#
```

### Tapa Blanda - Color:
```
0600X0900FCSTDPB060UW444GXX  // 6×9", Full Color, Blanco 60#
0850X1100FCSTDPB060UW444GXX  // 8.5×11", Full Color, Blanco 60#
```

### Tapa Dura (Hardcover):
```
0600X0900BWSTDHC060UW444GXX  // 6×9", BW, Tapa dura
0600X0900FCSTDHC060UW444GXX  // 6×9", Color, Tapa dura
```

---

## 💰 Cálculo de Precios

### Factores que Afectan el Costo:
1. **Número de páginas** (más páginas = más caro)
2. **Tamaño del libro** (tamaños más grandes = más caro)
3. **Color vs Blanco y Negro** (color = ~3x más caro)
4. **Tipo de papel** (premium = más caro)
5. **Encuadernación** (tapa dura = más cara)
6. **Cantidad** (descuentos por volumen)
7. **Destino de envío** (internacional = más caro)

### Ejemplo de Precios (USD):
- Libro 6×9", 200 páginas, BW, crema: **~$4.50**
- Libro 6×9", 200 páginas, Color: **~$12.00**
- Libro 8.5×11", 100 páginas, Color: **~$8.50**
- Tapa dura 6×9", 200 páginas, BW: **~$15.00**

### Fórmula Aproximada de Margen:
```
Precio Venta = (Costo Lulu × 2.5) + Envío + Margen DrakkarPress
```

---

## 🚀 Flujo de Integración

### 1. Usuario Compra Libro Físico:
```
Usuario → DrakkarPress Frontend
  → Backend valida compra
  → Crea orden en base de datos
  → Llama a Lulu API
  → Lulu procesa impresión
  → Lulu envía libro
  → Usuario recibe libro
```

### 2. Tracking Automático:
```
Webhook de Lulu → Backend DrakkarPress
  → Actualiza estado de orden
  → Notifica usuario por email
  → Agrega tracking number
```

---

## 🔧 Variables de Entorno Necesarias

Ya configuradas en `.env.example`:
```bash
LULU_CLIENT_KEY=a10cc795-35a4-4239-ae41-f78e6abb0df0
LULU_CLIENT_SECRET=sIyhz2KiOoJfHAcRxkLETMoq6LquCc87
LULU_API_BASE64=Basic YTEwY2M3OTUtMzVhNC00MjM5LWFlNDEtZjc4ZTZhYmIwZGYwOnNJeWh6MktpT29KZkhBY1J4a0xFVE1vcTZMcXVDYzg3
LULU_API_URL=https://api.lulu.com/v1
```

---

## 📚 Documentación Oficial

- **API Docs**: https://developers.lulu.com/
- **API Reference**: https://developers.lulu.com/reference
- **Dashboard**: https://lulu.com/commerce/
- **Support**: https://help.lulu.com/

---

## ✅ Próximos Pasos

1. [ ] Implementar `LuluPrintService.java`
2. [ ] Crear DTOs para requests/responses de Lulu
3. [ ] Integrar en flujo de compra de libros físicos
4. [ ] Configurar webhooks de Lulu
5. [ ] Probar con orden de prueba
6. [ ] Implementar cálculo de precios dinámico
7. [ ] Agregar tracking de envíos

---

**Credenciales guardadas en**: `backend/.env.example`  
**Estado**: ✅ Listas para integración  
**Próximo**: Resolver Lombok y compilar backend
