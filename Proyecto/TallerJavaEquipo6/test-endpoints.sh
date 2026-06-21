#!/bin/bash
# =============================================================
# TallerJavaEquipo6 - Script de prueba de endpoints
# Uso: chmod +x test-endpoints.sh && ./test-endpoints.sh
# =============================================================

BASE="http://localhost:8080/TallerJavaEquipo6/api"

AUTH_COMUN="12345678:clave123"
AUTH_PROFESIONAL="99887766:pass456"

# colores
CYAN='\033[0;36m'
YELLOW='\033[0;33m'
GREEN='\033[0;32m'
MAGENTA='\033[0;35m'
RED='\033[0;31m'
NC='\033[0m'

call() {
    local METHOD=$1
    local URL=$2
    local AUTH=$3
    local BODY=$4
    local EXPECTED_ERROR=${5:-false}

    if [ -n "$AUTH" ] && [ -n "$BODY" ]; then
        HTTP_CODE=$(curl -s -o /tmp/resp.txt -w "%{http_code}" -X "$METHOD" "$URL" \
            -u "$AUTH" -H "Content-Type: application/json" -d "$BODY")
    elif [ -n "$AUTH" ]; then
        HTTP_CODE=$(curl -s -o /tmp/resp.txt -w "%{http_code}" -X "$METHOD" "$URL" \
            -u "$AUTH" -H "Content-Type: application/json")
    elif [ -n "$BODY" ]; then
        HTTP_CODE=$(curl -s -o /tmp/resp.txt -w "%{http_code}" -X "$METHOD" "$URL" \
            -H "Content-Type: application/json" -d "$BODY")
    else
        HTTP_CODE=$(curl -s -o /tmp/resp.txt -w "%{http_code}" -X "$METHOD" "$URL" \
            -H "Content-Type: application/json")
    fi

    RESP=$(cat /tmp/resp.txt)

    if [ "$EXPECTED_ERROR" = "true" ]; then
        echo -e "${GREEN}  ERROR capturado correctamente (HTTP $HTTP_CODE): $RESP${NC}"
    elif [ "$HTTP_CODE" -ge 200 ] && [ "$HTTP_CODE" -lt 300 ]; then
        echo -e "  HTTP $HTTP_CODE: $RESP"
    else
        echo -e "${RED}  ERROR HTTP $HTTP_CODE: $RESP${NC}"
    fi
}

echo ""
echo -e "${CYAN}======================================================${NC}"
echo -e "${CYAN} MODULO CLIENTES${NC}"
echo -e "${CYAN}======================================================${NC}"

echo ""
echo -e "${YELLOW}--- 1. Registrar cliente COMUN ---${NC}"
call POST "$BASE/clientes/registrar" "" \
    '{"cedula":"12345678","nombreCompleto":"Juan Perez","telefono":"099123456","contrasena":"clave123","tipo":"COMUN"}'

echo ""
echo -e "${YELLOW}--- 2. Registrar cliente PROFESIONAL ---${NC}"
call POST "$BASE/clientes/registrar" "" \
    '{"cedula":"99887766","nombreCompleto":"Maria Taxi","telefono":"098000111","contrasena":"pass456","tipo":"PROFESIONAL","tipoProfesional":"TAXI","porcentajeDescuento":15.0}'

echo ""
echo -e "${YELLOW}--- 3. Listar clientes ---${NC}"
call GET "$BASE/clientes" "" ""

echo ""
echo -e "${YELLOW}--- 4. Agregar tarjeta a cliente COMUN ---${NC}"
call POST "$BASE/clientes/12345678/medioPago" "$AUTH_COMUN" \
    '{"tipo":"TARJETA","numero":"4111111111111234","titular":"Juan Perez","fechaVencimiento":"2027-12-01","digitoVerificacion":"123","tipoTarjeta":"VISA"}'

echo ""
echo -e "${YELLOW}--- 5. Agregar cuenta UTE a cliente COMUN ---${NC}"
call POST "$BASE/clientes/12345678/medioPago" "$AUTH_COMUN" \
    '{"tipo":"UTE","numeroCuenta":"UTE-98765"}'

echo ""
echo -e "${YELLOW}--- 6. Agregar tarjeta a cliente PROFESIONAL ---${NC}"
call POST "$BASE/clientes/99887766/medioPago" "$AUTH_PROFESIONAL" \
    '{"tipo":"TARJETA","numero":"5500005555555559","titular":"Maria Taxi","fechaVencimiento":"2028-06-01","digitoVerificacion":"456","tipoTarjeta":"MASTERCARD"}'

echo ""
echo -e "${YELLOW}--- 7. Agregar cuenta UTE a cliente PROFESIONAL ---${NC}"
call POST "$BASE/clientes/99887766/medioPago" "$AUTH_PROFESIONAL" \
    '{"tipo":"UTE","numeroCuenta":"UTE-11223"}'

echo ""
echo -e "${YELLOW}--- 8. Reclamo cliente COMUN ---${NC}"
call POST "$BASE/clientes/12345678/reclamos" "$AUTH_COMUN" \
    '{"comentario":"El cargador no funciono correctamente"}'

echo ""
echo -e "${CYAN}======================================================${NC}"
echo -e "${CYAN} MODULO CARGA - SETUP${NC}"
echo -e "${CYAN}======================================================${NC}"

echo ""
echo -e "${YELLOW}--- 9. Crear estacion de carga ---${NC}"
call POST "$BASE/cargas/estaciones" "" \
    '{"descripcion":"Estacion Centro","calle":"18 de Julio 1234","departamento":"Montevideo","longitud":-56,"latitud":-34}'

echo ""
echo -e "${YELLOW}--- 10. Listar estaciones ---${NC}"
call GET "$BASE/cargas/estaciones" "" ""

echo ""
echo -e "${YELLOW}--- 11. Crear cargador 1 RAPIDO en estacion 1 ---${NC}"
call POST "$BASE/cargas/cargadores" "" \
    '{"idEstacion":1,"tipo":"RAPIDO","tieneCable":true,"tipoConector":"TIPO2","potenciaMinima":22}'

echo ""
echo -e "${YELLOW}--- 12. Crear cargador 2 LENTO en estacion 1 ---${NC}"
call POST "$BASE/cargas/cargadores" "" \
    '{"idEstacion":1,"tipo":"LENTO","tieneCable":false,"tipoConector":"TIPO2","potenciaMinima":7}'

echo ""
echo -e "${CYAN}======================================================${NC}"
echo -e "${CYAN} CARGA 1 - cliente COMUN, cargador 1, TARJETA (idMedioPago=1)${NC}"
echo -e "${CYAN}======================================================${NC}"

echo ""
echo -e "${YELLOW}--- 13. Iniciar carga 1 ---${NC}"
call POST "$BASE/cargas/iniciar" "$AUTH_COMUN" \
    '{"cedulaCliente":"12345678","idCargador":1,"idMedioPago":1}'

echo ""
echo -e "${YELLOW}--- 14. Ver carga activa cliente COMUN ---${NC}"
call GET "$BASE/cargas/activa?cedulaCliente=12345678" "$AUTH_COMUN" ""

echo ""
echo -e "${MAGENTA}--- 15. ERROR ESPERADO: Segunda carga en cargador ocupado ---${NC}"
call POST "$BASE/cargas/iniciar" "$AUTH_PROFESIONAL" \
    '{"cedulaCliente":"99887766","idCargador":1,"idMedioPago":3}' true

echo ""
echo -e "${MAGENTA}--- 16. ERROR ESPERADO: Segunda carga para cliente con carga activa ---${NC}"
call POST "$BASE/cargas/iniciar" "$AUTH_COMUN" \
    '{"cedulaCliente":"12345678","idCargador":2,"idMedioPago":1}' true

echo ""
echo -e "${YELLOW}--- 17. Finalizar carga 1 (15.5 kWh, sin demora) pago TARJETA ---${NC}"
call POST "$BASE/cargas/finalizar" "$AUTH_COMUN" \
    '{"idCargador":1,"consumoKwh":15.5,"minutosDemora":0}'

echo ""
echo -e "${CYAN}======================================================${NC}"
echo -e "${CYAN} CARGA 2 - cliente PROFESIONAL, cargador 2, TARJETA (idMedioPago=3)${NC}"
echo -e "${CYAN}======================================================${NC}"

echo ""
echo -e "${YELLOW}--- 18. Iniciar carga 2 ---${NC}"
call POST "$BASE/cargas/iniciar" "$AUTH_PROFESIONAL" \
    '{"cedulaCliente":"99887766","idCargador":2,"idMedioPago":3}'

echo ""
echo -e "${YELLOW}--- 19. Ver carga activa cliente PROFESIONAL ---${NC}"
call GET "$BASE/cargas/activa?cedulaCliente=99887766" "$AUTH_PROFESIONAL" ""

echo ""
echo -e "${YELLOW}--- 20. Finalizar carga 2 (8.0 kWh, 10 min demora) pago TARJETA ---${NC}"
call POST "$BASE/cargas/finalizar" "$AUTH_PROFESIONAL" \
    '{"idCargador":2,"consumoKwh":8.0,"minutosDemora":10}'

echo ""
echo -e "${CYAN}======================================================${NC}"
echo -e "${CYAN} CARGA 3 - cliente COMUN, cargador 1, UTE (idMedioPago=2)${NC}"
echo -e "${CYAN}======================================================${NC}"

echo ""
echo -e "${YELLOW}--- 21. Iniciar carga 3 ---${NC}"
call POST "$BASE/cargas/iniciar" "$AUTH_COMUN" \
    '{"cedulaCliente":"12345678","idCargador":1,"idMedioPago":2}'

echo ""
echo -e "${YELLOW}--- 22. Ver carga activa cliente COMUN ---${NC}"
call GET "$BASE/cargas/activa?cedulaCliente=12345678" "$AUTH_COMUN" ""

echo ""
echo -e "${YELLOW}--- 23. Finalizar carga 3 (20.0 kWh, 5 min demora) pago UTE ---${NC}"
call POST "$BASE/cargas/finalizar" "$AUTH_COMUN" \
    '{"idCargador":1,"consumoKwh":20.0,"minutosDemora":5}'

echo ""
echo -e "${CYAN}======================================================${NC}"
echo -e "${CYAN} HISTORICOS${NC}"
echo -e "${CYAN}======================================================${NC}"

echo ""
echo -e "${YELLOW}--- 24. Historico cliente COMUN ---${NC}"
call GET "$BASE/cargas/historico?cedulaCliente=12345678&fechaIni=2026-01-01&fechaFin=2026-12-31" "$AUTH_COMUN" ""

echo ""
echo -e "${YELLOW}--- 25. Historico cliente PROFESIONAL ---${NC}"
call GET "$BASE/cargas/historico?cedulaCliente=99887766&fechaIni=2026-01-01&fechaFin=2026-12-31" "$AUTH_PROFESIONAL" ""

echo ""
echo -e "${CYAN}======================================================${NC}"
echo -e "${CYAN} METRICAS EN INFLUXDB${NC}"
echo -e "${CYAN}======================================================${NC}"

echo ""
echo -e "${YELLOW}--- 26. Verificar metricas en InfluxDB ---${NC}"
MEASUREMENTS=$(curl -s "http://localhost:8086/query?db=metricasTallerJava&q=SHOW+MEASUREMENTS")
echo "$MEASUREMENTS" | grep -o '"[a-zA-Z]*"' | grep -v 'values\|name\|columns' | tr -d '"' | while read m; do
    echo -e "${GREEN}  Metrica: $m${NC}"
done

echo ""
echo -e "${CYAN}======================================================${NC}"
echo -e "${CYAN} FIN DE PRUEBAS${NC}"
echo -e "${CYAN}======================================================${NC}"
echo ""
read -p "Presione ENTER para cerrar"
