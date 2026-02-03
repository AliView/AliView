$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location (Join-Path $ScriptDir "..")

if (-not $env:JAVA_HOME) {
  Write-Error "JAVA_HOME is not set. Point it to a JDK install."
}

$Jdeps = Join-Path $env:JAVA_HOME "bin\jdeps.exe"
$Jlink = Join-Path $env:JAVA_HOME "bin\jlink.exe"
$Jpackage = Join-Path $env:JAVA_HOME "bin\jpackage.exe"
$JarTool = Join-Path $env:JAVA_HOME "bin\jar.exe"

if (-not (Test-Path $Jdeps) -or -not (Test-Path $Jlink) -or -not (Test-Path $Jpackage)) {
  Write-Error "JDK tools not found under JAVA_HOME: $env:JAVA_HOME"
}

$AppName = "AliView"
$AppVendor = "Systematic Biology, Uppsala University"
$AppUrl = "https://www.ormbunkar.se"
$WinUpgradeUuid = "DC75EEAA-05CC-4923-ADE4-0D84CBD25703"

[xml]$Pom = Get-Content "pom.xml"
$Ns = New-Object System.Xml.XmlNamespaceManager($Pom.NameTable)
$Ns.AddNamespace("m", $Pom.DocumentElement.NamespaceURI)
$AppVersion = $Pom.SelectSingleNode("//m:project/m:version", $Ns).InnerText
if ($env:APP_VERSION) {
  $AppVersion = $env:APP_VERSION
}
$parts = $AppVersion.Split(".")
$verMajor = if ($parts.Length -gt 0) { [int]$parts[0] } else { 0 }
$verMinor = if ($parts.Length -gt 1) { [int]$parts[1] } else { 0 }
$verPatch = if ($parts.Length -gt 2) { [int]$parts[2] } else { 0 }
try {
  $gitCount = [int](git rev-list --count HEAD)
  $verPatch = $gitCount % 256
} catch {
}
$AppVersion = "$verMajor.$verMinor.$verPatch"

$Types = $env:JPACKAGE_TYPES
if (-not $Types) { $Types = "msi,exe" }

Remove-Item -Recurse -Force "target\jpackage-windows" -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path "target\jpackage-windows\input" | Out-Null

Write-Host "Building fat jar..."
& mvn -DskipTests package

if (-not (Test-Path "target\aliview.jar")) {
  Write-Error "Expected jar not found: target\aliview.jar"
}

Copy-Item "target\aliview.jar" "target\jpackage-windows\input\aliview.jar"

Write-Host "Computing module list with jdeps..."
$JdepsJar = "target\jpackage-windows\jdeps-aliview.jar"
Copy-Item "target\jpackage-windows\input\aliview.jar" $JdepsJar

if (Test-Path $JarTool) {
  & $JarTool --delete --file $JdepsJar module-info.class 2>$null
  & $JarTool --delete --file $JdepsJar "META-INF/versions/*/module-info.class" 2>$null
}

$Modules = ""
try {
  $Modules = & $Jdeps --multi-release 21 --ignore-missing-deps --class-path $JdepsJar --print-module-deps $JdepsJar 2>$null
} catch {
  $Modules = ""
}

if (-not $Modules) {
  Write-Warning "jdeps failed; falling back to a conservative module list."
  $Modules = "java.desktop,java.logging,java.prefs,java.xml,java.management"
}

Write-Host "Creating runtime image with jlink..."
& $Jlink `
  --add-modules $Modules `
  --strip-debug `
  --no-header-files `
  --no-man-pages `
  --output "target\jpackage-windows\runtime"

$IconPath = "icon_images\win\alignment_multi.ico"

$TypeList = $Types.Split(",") | ForEach-Object { $_.Trim() } | Where-Object { $_ }
foreach ($Type in $TypeList) {
  $Args = @(
    "--type", $Type,
    "--name", $AppName,
    "--app-version", $AppVersion,
    "--vendor", $AppVendor,
    "--input", "target\jpackage-windows\input",
    "--main-jar", "aliview.jar",
    "--main-class", "aliview.AliView",
    "--icon", $IconPath,
    "--runtime-image", "target\jpackage-windows\runtime",
    "--file-associations", "jpackage\file-associations\nexus.properties",
    "--file-associations", "jpackage\file-associations\nex.properties",
    "--file-associations", "jpackage\file-associations\fasta.properties",
    "--file-associations", "jpackage\file-associations\fas.properties",
    "--file-associations", "jpackage\file-associations\fa.properties",
    "--file-associations", "jpackage\file-associations\afa.properties",
    "--file-associations", "jpackage\file-associations\phylip.properties",
    "--file-associations", "jpackage\file-associations\phy.properties",
    "--file-associations", "jpackage\file-associations\aln.properties",
    "--file-associations", "jpackage\file-associations\clustal.properties",
    "--file-associations", "jpackage\file-associations\clustalw.properties",
    "--file-associations", "jpackage\file-associations\clustalx.properties",
    "--file-associations", "jpackage\file-associations\msf.properties",
    "--dest", "target\jpackage-windows",
    "--win-upgrade-uuid", $WinUpgradeUuid,
    "--win-menu",
    "--win-menu-group", $AppName,
    "--win-shortcut",
    "--win-dir-chooser",
    "--java-options", "-Xmx1024m",
    "--java-options", "-Xms128m"
  )

  if ($Type -ne "app-image") {
    $Args += "--about-url"
    $Args += $AppUrl
  }


  Write-Host "Packaging with jpackage: $Type"
  & $Jpackage @Args
}

Write-Host "Done: $BuildDir"
