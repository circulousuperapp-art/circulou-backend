# dev-start.ps1
# Script para iniciar o ambiente de desenvolvimento do Circulou Backend
# Uso: .\scripts\dev-start.ps1 [-FullReset]

param (
    [switch]$FullReset
)

Write-Host "`n[Circulou] Iniciando Ambiente de Desenvolvimento..." -ForegroundColor Cyan

# 1. Verificar Docker Desktop
$null = Get-Process "Docker Desktop" 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "[WARNING] Docker Desktop não parece estar rodando. Tentando iniciar..." -ForegroundColor Yellow
    # Nota: O comando abaixo tenta iniciar o executável padrão se estiver no path
    Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe" 2>$null
    Write-Host "[INFO] Aguarde o Docker Desktop estabilizar antes de prosseguir." -ForegroundColor Gray
}

# 2. Protocolo de Recuperação (se solicitado)
if ($FullReset) {
    Write-Host "[RESET] Chamando script de recuperação oficial..." -ForegroundColor Magenta
    .\scripts\dev-recover.ps1
    exit
}

# 3. Iniciar Docker Compose (Apenas Infraestrutura)
# Define o arquivo do Prometheus como dev por padrão se estiver rodando localmente
$env:PROMETHEUS_CONFIG = "prometheus-dev.yml"
Write-Host "[DOCKER] Subindo containers de infraestrutura (Banco, Kafka, Observabilidade)..." -ForegroundColor Green
docker compose up -d postgres zookeeper kafka kafka-ui prometheus grafana

# 4. Aguardar Health Checks
Write-Host "[HEALTH] Aguardando estabilização dos serviços (timeout 60s)..." -ForegroundColor Gray
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

# 5. Resumo e URLs
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "   INFRAESTRUTURA CIRCULOU PRONTA" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

.\scripts\dev-status.ps1

Write-Host "`n[INFO] O Backend deve ser iniciado via IntelliJ na porta 8080." -ForegroundColor Gray
Write-Host "==========================================================" -ForegroundColor Cyan
