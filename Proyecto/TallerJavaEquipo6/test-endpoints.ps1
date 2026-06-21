# =============================================================
# TallerJavaEquipo6 - Script de prueba de endpoints
# Actualizado con autenticacion Basic Auth
# Uso: .\test-endpoints.ps1
# =============================================================

$BASE = "http://localhost:8080/TallerJavaEquipo6/api"

$credComun       = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("12345678:clave123"))
$credProfesional = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("99887766:pass456"))
$headersComun        = @{ "Content-Type" = "application/json"; "Authorization" = "Basic $credComun" }
$headersProfesional  = @{ "Content-Type" = "application/json"; "Authorization" = "Basic $credProfesional" }
$headersPublico      = @{ "Content-Type" = "application/json" }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " MODULO CLIENTES" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "--- 1. Registrar cliente COMUN ---" -ForegroundColor Yellow
$body = @{ cedula = "12345678"; nombreCompleto = "Juan Perez"; telefono = "099123456"; contrasena = "clave123"; tipo = "COMUN" } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/clientes/registrar" -Headers $headersPublico -Body $body } catch { Write-Host "AVISO: $($_.Exception.Message)" -ForegroundColor DarkYellow }

Write-Host ""
Write-Host "--- 2. Registrar cliente PROFESIONAL ---" -ForegroundColor Yellow
$body = @{ cedula = "99887766"; nombreCompleto = "Maria Taxi"; telefono = "098000111"; contrasena = "pass456"; tipo = "PROFESIONAL"; tipoProfesional = "TAXI"; porcentajeDescuento = 15.0 } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/clientes/registrar" -Headers $headersPublico -Body $body } catch { Write-Host "AVISO: $($_.Exception.Message)" -ForegroundColor DarkYellow }

Write-Host ""
Write-Host "--- 3. Listar clientes ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method GET -Uri "$BASE/clientes" -Headers $headersPublico } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 4. Agregar tarjeta a cliente COMUN ---" -ForegroundColor Yellow
$body = @{ tipo = "TARJETA"; numero = "4111111111111234"; titular = "Juan Perez"; fechaVencimiento = "2027-12-01"; digitoVerificacion = "123"; tipoTarjeta = "VISA" } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/clientes/12345678/medioPago" -Headers $headersComun -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 5. Agregar cuenta UTE a cliente COMUN ---" -ForegroundColor Yellow
$body = @{ tipo = "UTE"; numeroCuenta = "UTE-98765" } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/clientes/12345678/medioPago" -Headers $headersComun -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 6. Agregar tarjeta a cliente PROFESIONAL ---" -ForegroundColor Yellow
$body = @{ tipo = "TARJETA"; numero = "5500005555555559"; titular = "Maria Taxi"; fechaVencimiento = "2028-06-01"; digitoVerificacion = "456"; tipoTarjeta = "MASTERCARD" } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/clientes/99887766/medioPago" -Headers $headersProfesional -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 7. Agregar cuenta UTE a cliente PROFESIONAL ---" -ForegroundColor Yellow
$body = @{ tipo = "UTE"; numeroCuenta = "UTE-11223" } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/clientes/99887766/medioPago" -Headers $headersProfesional -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 8. Reclamo cliente COMUN ---" -ForegroundColor Yellow
$body = @{ comentario = "El cargador no funciono correctamente" } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/clientes/12345678/reclamos" -Headers $headersComun -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " MODULO CARGA - SETUP" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "--- 9. Crear estacion de carga ---" -ForegroundColor Yellow
$body = @{ descripcion = "Estacion Centro"; calle = "18 de Julio 1234"; departamento = "Montevideo"; longitud = -56; latitud = -34 } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/estaciones" -Headers $headersPublico -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 10. Listar estaciones ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method GET -Uri "$BASE/cargas/estaciones" -Headers $headersPublico } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 11. Crear cargador 1 RAPIDO en estacion 1 ---" -ForegroundColor Yellow
$body = @{ idEstacion = 1; tipo = "RAPIDO"; tieneCable = $true; tipoConector = "TIPO2"; potenciaMinima = 22 } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/cargadores" -Headers $headersPublico -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 12. Crear cargador 2 LENTO en estacion 1 ---" -ForegroundColor Yellow
$body = @{ idEstacion = 1; tipo = "LENTO"; tieneCable = $false; tipoConector = "TIPO2"; potenciaMinima = 7 } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/cargadores" -Headers $headersPublico -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " CARGA 1 - cliente COMUN, cargador 1, TARJETA (idMedioPago=1)" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "--- 13. Iniciar carga 1 ---" -ForegroundColor Yellow
$body = @{ cedulaCliente = "12345678"; idCargador = 1; idMedioPago = 1 } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/iniciar" -Headers $headersComun -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 14. Ver carga activa cliente COMUN ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method GET -Uri "$BASE/cargas/activa?cedulaCliente=12345678" -Headers $headersComun } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 15. ERROR ESPERADO: Segunda carga en cargador ocupado ---" -ForegroundColor Magenta
try {
    $body = @{ cedulaCliente = "99887766"; idCargador = 1; idMedioPago = 3 } | ConvertTo-Json
    Invoke-RestMethod -Method POST -Uri "$BASE/cargas/iniciar" -Headers $headersProfesional -Body $body
} catch { Write-Host "ERROR capturado correctamente: $($_.Exception.Message)" -ForegroundColor Green }

Write-Host ""
Write-Host "--- 16. ERROR ESPERADO: Segunda carga para cliente con carga activa ---" -ForegroundColor Magenta
try {
    $body = @{ cedulaCliente = "12345678"; idCargador = 2; idMedioPago = 1 } | ConvertTo-Json
    Invoke-RestMethod -Method POST -Uri "$BASE/cargas/iniciar" -Headers $headersComun -Body $body
} catch { Write-Host "ERROR capturado correctamente: $($_.Exception.Message)" -ForegroundColor Green }

Write-Host ""
Write-Host "--- 17. Finalizar carga 1 (15.5 kWh, sin demora) pago TARJETA ---" -ForegroundColor Yellow
$body = @{ idCargador = 1; consumoKwh = 15.5; minutosDemora = 0 } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/finalizar" -Headers $headersComun -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " CARGA 2 - cliente PROFESIONAL, cargador 2, TARJETA (idMedioPago=3)" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "--- 18. Iniciar carga 2 ---" -ForegroundColor Yellow
$body = @{ cedulaCliente = "99887766"; idCargador = 2; idMedioPago = 3 } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/iniciar" -Headers $headersProfesional -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 19. Ver carga activa cliente PROFESIONAL ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method GET -Uri "$BASE/cargas/activa?cedulaCliente=99887766" -Headers $headersProfesional } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 20. Finalizar carga 2 (8.0 kWh, 10 min demora) pago TARJETA ---" -ForegroundColor Yellow
$body = @{ idCargador = 2; consumoKwh = 8.0; minutosDemora = 10 } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/finalizar" -Headers $headersProfesional -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " CARGA 3 - cliente COMUN, cargador 1, UTE (idMedioPago=2)" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "--- 21. Iniciar carga 3 ---" -ForegroundColor Yellow
$body = @{ cedulaCliente = "12345678"; idCargador = 1; idMedioPago = 2 } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/iniciar" -Headers $headersComun -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 22. Ver carga activa cliente COMUN ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method GET -Uri "$BASE/cargas/activa?cedulaCliente=12345678" -Headers $headersComun } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 23. Finalizar carga 3 (20.0 kWh, 5 min demora) pago UTE ---" -ForegroundColor Yellow
$body = @{ idCargador = 1; consumoKwh = 20.0; minutosDemora = 5 } | ConvertTo-Json
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/finalizar" -Headers $headersComun -Body $body } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " HISTORICOS" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "--- 24. Historico cliente COMUN ---" -ForegroundColor Yellow
$uri = "$BASE/cargas/historico?cedulaCliente=12345678&fechaIni=2026-01-01&fechaFin=2026-12-31"
try { Invoke-RestMethod -Method GET -Uri $uri -Headers $headersComun } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- 25. Historico cliente PROFESIONAL ---" -ForegroundColor Yellow
$uri = "$BASE/cargas/historico?cedulaCliente=99887766&fechaIni=2026-01-01&fechaFin=2026-12-31"
try { Invoke-RestMethod -Method GET -Uri $uri -Headers $headersProfesional } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " METRICAS EN INFLUXDB" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "--- 26. Verificar metricas en InfluxDB ---" -ForegroundColor Yellow
try {
    $result = Invoke-RestMethod -Uri "http://localhost:8086/query?db=metricasTallerJava&q=SHOW+MEASUREMENTS" -Method GET
    $result.results[0].series[0].values | ForEach-Object { Write-Host "  Metrica: $_" -ForegroundColor Green }
} catch { Write-Host "ERROR verificando InfluxDB: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " FIN DE PRUEBAS" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host ""
Read-Host "Presione ENTER para cerrar"
