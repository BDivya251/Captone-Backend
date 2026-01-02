<#
Runs all Spring Boot services in separate PowerShell windows with one command.
Usage:  powershell -ExecutionPolicy Bypass -File .\run-all-services.ps1
Optional: -SkipBuild (currently still runs spring-boot:run but kept for future use)
#>
param(
  [switch]$SkipBuild
)

function Start-ServiceProject {
  param(
    [string]$Name,
    [string]$Path
  )
  if (-not (Test-Path $Path)) {
    Write-Warning "Path not found: $Path"
    return
  }

  $cmd = "./mvnw spring-boot:run -DskipTests"
  Write-Host "Starting $Name ..." -ForegroundColor Cyan

  # Use WorkingDirectory to handle spaces; invoke mvnw with explicit call operator
  Start-Process -FilePath powershell `
    -ArgumentList '-NoExit','-Command',"& $cmd" `
    -WorkingDirectory $Path `
    -WindowStyle Minimized
}

$root = Split-Path -Parent $MyInvocation.MyCommand.Path

$services = @(
  @{ name = 'config-server';           path = Join-Path $root 'config-server' }
  @{ name = 'eureka-server';           path = Join-Path $root 'eureka-server' }
  @{ name = 'user-management-service'; path = Join-Path $root 'user-management-service' }
  @{ name = 'inventory-service';       path = Join-Path $root 'inventory-service-management' }
  @{ name = 'vehicle-management';      path = Join-Path $root 'vehicle-management-service' }
  @{ name = 'service-registry';        path = Join-Path $root 'service-registry-service' }
  @{ name = 'api-gateway';             path = Join-Path $root 'api-gateway' }
)

foreach ($svc in $services) {
  Start-ServiceProject -Name $svc.name -Path $svc.path
  Start-Sleep -Seconds 2 # small stagger so config/eureka start first
}

Write-Host "All start commands issued. Check each window for logs." -ForegroundColor Green
