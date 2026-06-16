# =============================================================
# TallerJavaEquipo6 - Script de cargas extra
# Requiere haber ejecutado test-endpoints.ps1 primero
# Uso: powershell -ExecutionPolicy Bypass -File ".\test-cargas-extra.ps1"
# =============================================================

$BASE = "http://localhost:8080/TallerJavaEquipo6/api"

$credComun       = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("12345678:clave123"))
$credProfesional = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("99887766:pass456"))
$headersComun       = @{ "Content-Type" = "application/json"; "Authorization" = "Basic $credComun" }
$headersProfesional = @{ "Content-Type" = "application/json"; "Authorization" = "Basic $credProfesional" }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " CARGA EXTRA A - cliente COMUN, cargador 1, TARJETA" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "--- Iniciar carga ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/iniciar" -Headers $headersComun -Body '{"cedulaCliente":"12345678","idCargador":1,"idMedioPago":1}' } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- Ver carga activa ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method GET -Uri "$BASE/cargas/activa?cedulaCliente=12345678" -Headers $headersComun } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- Finalizar carga (5.0 kWh, sin demora) ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/finalizar" -Headers $headersComun -Body '{"idCargador":1,"consumoKwh":5.0,"minutosDemora":0}' } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " CARGA EXTRA B - cliente PROFESIONAL, cargador 2, UTE" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "--- Iniciar carga ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/iniciar" -Headers $headersProfesional -Body '{"cedulaCliente":"99887766","idCargador":2,"idMedioPago":4}' } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- Ver carga activa ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method GET -Uri "$BASE/cargas/activa?cedulaCliente=99887766" -Headers $headersProfesional } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- Finalizar carga (12.0 kWh, 3 min demora) ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/finalizar" -Headers $headersProfesional -Body '{"idCargador":2,"consumoKwh":12.0,"minutosDemora":3}' } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " CARGA EXTRA C - cliente COMUN, cargador 1, UTE" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "--- Iniciar carga ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/iniciar" -Headers $headersComun -Body '{"cedulaCliente":"12345678","idCargador":1,"idMedioPago":2}' } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- Finalizar carga (8.5 kWh, sin demora) ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/finalizar" -Headers $headersComun -Body '{"idCargador":1,"consumoKwh":8.5,"minutosDemora":0}' } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " CARGA SIMULTANEA - ambos cargadores ocupados" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "--- Iniciar carga COMUN en cargador 1 ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/iniciar" -Headers $headersComun -Body '{"cedulaCliente":"12345678","idCargador":1,"idMedioPago":1}' } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- Iniciar carga PROFESIONAL en cargador 2 ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/iniciar" -Headers $headersProfesional -Body '{"cedulaCliente":"99887766","idCargador":2,"idMedioPago":3}' } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host ">>> MIRAR GRAFANA AHORA: Cargas Activas deberia ser 2" -ForegroundColor Magenta
Read-Host "Presione ENTER para finalizar las cargas simultaneas"

Write-Host ""
Write-Host "--- Finalizar carga COMUN ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/finalizar" -Headers $headersComun -Body '{"idCargador":1,"consumoKwh":20.0,"minutosDemora":0}' } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "--- Finalizar carga PROFESIONAL ---" -ForegroundColor Yellow
try { Invoke-RestMethod -Method POST -Uri "$BASE/cargas/finalizar" -Headers $headersProfesional -Body '{"idCargador":2,"consumoKwh":15.0,"minutosDemora":5}' } catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " ESTADO FINAL EN INFLUXDB" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

Write-Host ""
foreach ($metric in @("cargasActivas","cargasRealizadas","pagosTarjeta","pagosUTE","erroresTarjeta")) {
    try {
        $result = Invoke-RestMethod -Uri "http://localhost:8086/query?db=metricasTallerJava&q=SELECT+last(value)+FROM+$metric" -Method GET
        $val = $result.results[0].series[0].values[0][1]
        Write-Host "  $metric = $val" -ForegroundColor Green
    } catch {
        Write-Host "  $metric = sin datos" -ForegroundColor DarkYellow
    }
}

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host " FIN DE PRUEBAS EXTRA" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host ""
Read-Host "Presione ENTER para cerrar"
