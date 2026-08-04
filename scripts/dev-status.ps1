# dev-status.ps1
# Script para diagnóstico profissional do ambiente de desenvolvimento do Circulou Backend
# Uso: .\scripts\dev-status.ps1

function Show-Header {
    Write-Host "`n==================================================" -ForegroundColor Cyan
    Write-Host "                CIRCULOU BACKEND" -ForegroundColor Cyan
    Write-Host "          Development Environment Status" -ForegroundColor Cyan
    Write-Host "==================================================" -ForegroundColor Cyan
}

function Get-Status-Color {
    param($Status)
    switch ($Status) {
        "OK" { return "Green" }
        "HEALTHY" { return "Green" }
        "RUNNING" { return "Green" }
        "WARNING" { return "Yellow" }
        "DOWN" { return "Red" }
        "FAILED" { return "Red" }
        "NOT RUNNING" { return "Red" }
        "UNHEALTHY" { return "Red" }
        default { return "White" }
    }
}

function Write-Row {
    param($Label, $Status, $Info = "")
    $dots = "." * (30 - $Label.Length)
    Write-Host "$Label$dots " -NoNewline -ForegroundColor Gray
    
    $color = Get-Status-Color $Status
    Write-Host "$Status" -NoNewline -ForegroundColor $color
    
    if ($Info) {
        Write-Host " ($Info)" -ForegroundColor Gray
    } else {
        Write-Host ""
    }
}

Show-Header

# 1. Infraestrutura Base
$dockerInfo = & docker info --format '{{.ID}}' 2>$null
$dockerStatus = if ($LASTEXITCODE -eq 0) { "OK" } else { "NOT RUNNING" }

# Melhoria na detecção do WSL: verifica se o executável responde e se há distribuições ativas
$wslList = & wsl --list --running 2>$null
$wslStatus = if ($LASTEXITCODE -eq 0 -and $null -ne $wslList) { "OK" } else { "DOWN" }

Write-Row "Docker Desktop" $dockerStatus
Write-Row "WSL2" $wslStatus
Write-Row "Docker Engine" $dockerStatus

Write-Host "--------------------------------------------------" -ForegroundColor Cyan

# 2. Containers
Write-Host "Containers`n" -ForegroundColor Yellow

$services = @("backend", "postgres", "kafka", "prometheus", "grafana", "kafka-ui")
$infraHealthy = $true
$backendContainerRunning = $false

foreach ($s in $services) {
    $inspect = & docker inspect --format '{{.State.Status}}|{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' $s 2>$null
    
    if ($LASTEXITCODE -ne 0 -or $null -eq $inspect) {
        if ($s -eq "backend") {
            Write-Row "Backend" "NOT RUNNING" "(Expected for IntelliJ)"
        } else {
            Write-Row ([char]::ToUpper($s[0]) + $s.Substring(1)) "NOT RUNNING"
            $infraHealthy = $false
        }
        continue
    }

    $parts = $inspect.Split('|')
    $state = if ($parts.Count -gt 0) { $parts[0].ToUpper() } else { "UNKNOWN" }
    $health = if ($parts.Count -gt 1) { $parts[1].ToUpper() } else { "NONE" }

    $displayStatus = $state
    if ($health -ne "NONE" -and $health -ne "") {
        $displayStatus = $health
    }

    Write-Row ([char]::ToUpper($s[0]) + $s.Substring(1)) $displayStatus
    
    if ($s -eq "backend" -and $state -eq "RUNNING") {
        $backendContainerRunning = $true
    }
    
    # Se infraestrutura essencial não estiver healthy/running
    if ($s -ne "backend" -and $displayStatus -ne "HEALTHY" -and $displayStatus -ne "RUNNING") {
        $infraHealthy = $false
    }
}

Write-Host "--------------------------------------------------" -ForegroundColor Cyan

# 3. Portas
Write-Host "Ports`n" -ForegroundColor Yellow

$ports = @{
    5432 = "PostgreSQL";
    8080 = "Backend";
    8081 = "Kafka UI";
    9090 = "Prometheus";
    3000 = "Grafana";
    29092 = "Kafka Ext"
}

$backendIntelliJ = $false

foreach ($port in ($ports.Keys | Sort-Object)) {
    $conn = Get-NetTCPConnection -LocalPort $port 2>$null
    if ($conn) {
        $process = Get-Process -Id $conn.OwningProcess 2>$null
        $pName = if ($process) { $process.ProcessName } else { "Unknown" }
        
        $status = "OK"
        $info = $ports[$port]
        
        # Identificação especial para a porta 8080
        if ($port -eq 8080) {
            if ($pName -match "java|idea") {
                $status = "OK"
                $info = "Backend (IntelliJ)"
                $backendIntelliJ = $true
            } elseif ($pName -match "com.docker.backend|docker") {
                $status = "OK"
                $info = "Backend (Docker)"
            } else {
                $status = "WARNING"
                $info = "Occupied by $pName"
            }
        }
        
        Write-Row "$port" $status $info
    } else {
        Write-Row "$port" "DOWN" $ports[$port]
        if ($port -ne 8080) { $infraHealthy = $false }
    }
}

Write-Host "--------------------------------------------------" -ForegroundColor Cyan

# 4. Acesso Rápido
Write-Host "Quick Access`n" -ForegroundColor Yellow

Write-Host "Swagger" -ForegroundColor Gray
Write-Host "http://localhost:8080/swagger-ui/index.html`n"

Write-Host "Prometheus" -ForegroundColor Gray
Write-Host "http://localhost:9090`n"

Write-Host "Grafana" -ForegroundColor Gray
Write-Host "http://localhost:3000`n"

Write-Host "Kafka UI" -ForegroundColor Gray
Write-Host "http://localhost:8081"

Write-Host "==================================================" -ForegroundColor Cyan

# Resumo Final
$backendOk = ($backendContainerRunning -or $backendIntelliJ)

if ($dockerStatus -ne "OK" -or $wslStatus -ne "OK" -or -not $infraHealthy) {
    Write-Host "`n[!] O ambiente possui falhas criticas. Execute dev-recover.ps1 para restaurar." -ForegroundColor Red
} elseif (-not $backendOk) {
    Write-Host "`n[!] Infraestrutura OK, mas o Backend nao foi detectado na porta 8080." -ForegroundColor Yellow
    Write-Host "[?] Inicie o projeto pelo IntelliJ para completar o ambiente." -ForegroundColor Gray
} else {
    Write-Host "`n[v] Ambiente saudavel e operacional." -ForegroundColor Green
}
