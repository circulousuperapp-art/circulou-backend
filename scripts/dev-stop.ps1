# dev-stop.ps1
# Script profissional para encerramento do ambiente de desenvolvimento do Circulou Backend
# Uso: .\scripts\dev-stop.ps1

Write-Host "`n==================================================" -ForegroundColor Cyan
Write-Host "         CIRCULOU BACKEND - STOPPING" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

Write-Host "[DOCKER] Desligando infraestrutura de apoio..." -ForegroundColor Yellow
docker compose down 2>$null

Write-Host "`n[CONTAINERS] Estado atual:" -ForegroundColor Gray
docker ps -a --filter "name=postgres|zookeeper|kafka|backend|kafka-ui|prometheus|grafana" --format "table {{.Names}}\t{{.Status}}" 2>$null

Write-Host "`n[v] Ambiente encerrado com sucesso." -ForegroundColor Green
Write-Host "[INFO] Use .\scripts\dev-start.ps1 para subir o ambiente novamente." -ForegroundColor Gray
Write-Host "==================================================" -ForegroundColor Cyan
