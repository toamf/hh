param(
    [string]$remoteComputer,
    [string]$userName,
    [pscredential]$credentials,
    [string]$saveLocation = "C:\Screenshots"
)

if (-not (Test-Path $saveLocation)) {
    New-Item -Path $saveLocation -ItemType Directory
}

$sessions = qwinsta /server:$remoteComputer 2>&1 | Where-Object { $_ -match "^\s+\d+\s+\d+\s+($userName)\s+" } | ForEach-Object {
    if ($_ -match "^\s+(\d+)\s+\d+\s+($userName)\s+") {
        return $matches[1]
    }
}

foreach ($sessionId in $sessions) {
    psexec \\$remoteComputer -u $credentials.UserName -p $credentials.GetNetworkCredential().Password -h -s cmd /c "if not exist C:\temp mkdir C:\temp"

    $screenshotPath = "C:\temp\screenshot_$sessionId.png"
    
    $executionResult = psexec \\$remoteComputer -i $sessionId -h -s -u $credentials.UserName -p $credentials.GetNetworkCredential().Password powershell -NoProfile -ExecutionPolicy Bypass -Command {
        Add-Type -AssemblyName System.Windows.Forms
        $bounds = [System.Windows.Forms.Screen]::PrimaryScreen.Bounds
        $bitmap = New-Object System.Drawing.Bitmap($bounds.width, $bounds.height)
        $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
        $graphics.CopyFromScreen($bounds.Location, [System.Drawing.Point]::Empty, $bounds.Size)
        $bitmap.Save("$using:screenshotPath")
    }

    if ($executionResult -eq 0) {
        Write-Output "Скриншот создан на $remoteComputer в сессии $sessionId"

        if (Test-Path "\\$remoteComputer\$screenshotPath") {
            Write-Output "Скриншот найден на удаленном ПК."

            Copy-Item "\\$remoteComputer\$screenshotPath" -Destination "$saveLocation\screenshot_${remoteComputer}_$sessionId.png"

            if (Test-Path "$saveLocation\screenshot_${remoteComputer}_$sessionId.png") {
                Write-Output "Скриншот успешно скопирован!"
            } else {
                Write-Warning "Не удалось скопировать файл. Проверьте путь $saveLocation"
            }

            Remove-Item "\\$remoteComputer\$screenshotPath"
        } else {
            Write-Warning "Не удалось создать снимок экрана для сессии $sessionId на $remoteComputer"
        }
    } else {
        Write-Warning "Ошибка при выполнении на $remoteComputer для сессии $sessionId. Код ошибки: $executionResult"
    }
}
