# dev-recover.ps1
# Script para recuperação total do ambiente de desenvolvimento (ADR-007)
# Uso: .\scripts\dev-recover.ps1

Write-Host "`n==================================================" -ForegroundColor Magenta
Write-Host "         CIRCULOU BACKEND - RECOVERY MODE" -ForegroundColor Magenta
Write-Host "==================================================" -ForegroundColor Magenta
Write-Host "[!] Iniciando protocolo de Cold Reset (ADR-007)" -ForegroundColor Yellow
Write-Host "[!] Este processo reiniciará o WSL e o Docker Engine." -ForegroundColor Yellow
Write-Host "--------------------------------------------------" -ForegroundColor Magenta

# 1. WSL Shutdown
Write-Host "`n[1/4] Desligando subsistema WSL2..." -ForegroundColor Cyan
wsl --shutdown 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "[!] Falha ao desligar WSL. Certifique-se de ter permissões administrativas." -ForegroundColor Red
}

# 2. Aguardar
Write-Host "[2/4] Aguardando estabilização do sistema (10s)..." -ForegroundColor Gray
Start-Sleep -Seconds 10

# 3. Docker Compose Down
Write-Host "[3/4] Removendo containers e redes órfãs..." -ForegroundColor Cyan
docker compose down 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "[!] Docker Engine pode estar iniciando. Tentando novamente em 15s..." -ForegroundColor Yellow
    Start-Sleep -Seconds 15
    docker compose down 2>$null
}

# 4. Docker Compose Up (Apenas Infraestrutura)
Write-Host "[4/4] Reiniciando infraestrutura de apoio..." -ForegroundColor Cyan
$env:PROMETHEUS_CONFIG = "prometheus-dev.yml"
docker compose up -d postgres zookeeper kafka kafka-ui prometheus grafana

# 5. Aguardar Health Checks
Write-Host "`n[HEALTH] Validando integridade dos serviços..." -ForegroundColor Yellow
$timeout = 60
$elapsed = 0
while ($elapsed -lt $timeout) {
    $unhealthy = docker ps --filter "health=unhealthy" --format "{{.Names}}"
    $starting = docker ps --filter "health=starting" --format "{{.Names}}"
    
    if (-not $starting -and -not $unhealthy) {
        break
    }
    
    Start-Sleep -Seconds 5
    $elapsed += 5
    Write-Host "." -NoNewline -ForegroundColor Gray
}
Write-Host "`n"

# 6. Status Final
Write-Host "--------------------------------------------------" -ForegroundColor Magenta
Write-Host "[v] Protocolo de recuperação finalizado." -ForegroundColor Green
Write-Host "[v] Chamando diagnóstico de status..." -ForegroundColor Gray
.\scripts\dev-status.ps1
