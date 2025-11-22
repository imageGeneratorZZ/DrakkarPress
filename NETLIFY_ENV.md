# DrakkarPress - Variables de Entorno para Netlify

## Configuración Automática

Este archivo documenta las variables de entorno necesarias en Netlify.

### Variables Requeridas en Netlify:

No se necesitan variables de entorno en el frontend ya que usa proxy.

### API Backend

El backend debe estar desplegado en Railway o similar.
La URL del backend se configura en `netlify.toml` como proxy.

### Configuración en Netlify Dashboard:

1. Ve a: https://app.netlify.com/sites/drakkarpress/settings/deploys
2. En "Build & deploy" > "Environment variables"
3. NO se necesitan variables adicionales (el proxy maneja todo)

### URLs:

- **Frontend:** https://www.drakkarpress.com
- **Backend:** https://drakkarpress-backend.up.railway.app
- **API Proxy:** https://www.drakkarpress.com/api/*

### Testing Local:

```bash
# Frontend con Live Server
http://localhost:5500

# Backend local
http://localhost:12000
```

### Deployment:

```powershell
# Preview
.\deploy-netlify.ps1

# Producción
.\deploy-netlify.ps1 -Production
```
