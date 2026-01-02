<#
Stops all Spring Boot services started from this repo by matching process command lines.
Usage:  powershell -ExecutionPolicy Bypass -File .\stop-all-services.ps1
#>

function Stop-ServiceProcess {
  param(
    [string]$Name,
    [string]$Path
  )
  $stopped = 0
  $patterns = @(
    [regex]::Escape($Path),
    [regex]::Escape((Join-Path $Path "mvnw"))
  )

  $procs = Get-CimInstance Win32_Process |
    Where-Object {
      ($_.Name -match 'java|mvnw|cmd|powershell') -and
      ($patterns | Where-Object { $_ -match $_.ToString() -and $_.ToString() -ne '' } | ForEach-Object { $_ }) -and
      ($patterns | Where-Object { $_ -match $_.ToString() } | ForEach-Object { $_ })
    }

  foreach ($p in $procs) {
    if ($p.CommandLine -match [regex]::Escape($Path)) {
      try {
        Stop-Process -Id $p.ProcessId -Force -ErrorAction Stop
        Write-Host "Stopped $Name (PID $($p.ProcessId))" -ForegroundColor Yellow
        $stopped++
      } catch {
        Write-Warning "Failed to stop $Name (PID $($p.ProcessId)): $_"
      }
    }
  }

  if ($stopped -eq 0) {
    Write-Host "No matching processes found for $Name" -ForegroundColor DarkGray
  }
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
  Stop-ServiceProcess -Name $svc.name -Path $svc.path
}

Write-Host "Stop commands issued. Verify no service java processes remain." -ForegroundColor Green
