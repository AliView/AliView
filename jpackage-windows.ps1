$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Parent $MyInvocation.MyCommand.Path

if (-not $env:JAVA_HOME) {
  Write-Error "JAVA_HOME is not set. Point it to a JDK 21 install."
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

[xml]$Pom = Get-Content (Join-Path $RootDir "pom.xml")
$Ns = New-Object System.Xml.XmlNamespaceManager($Pom.NameTable)
$Ns.AddNamespace("m", $Pom.DocumentElement.NamespaceURI)
$AppVersion = $Pom.SelectSingleNode("//m:project/m:version", $Ns).InnerText
if ($env:APP_VERSION) { $AppVersion = $env:APP_VERSION }

$InputJar = Join-Path $RootDir "target\aliview.jar"
$BuildDir = Join-Path $RootDir "target\jpackage-windows"
$InputDir = Join-Path $BuildDir "input"
$RuntimeDir = Join-Path $BuildDir "runtime"
$Types = $env:JPACKAGE_TYPES
if (-not $Types) { $Types = "app-image,msi,exe" }

Remove-Item -Recurse -Force $BuildDir -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $InputDir | Out-Null

Write-Host "Building fat jar..."
& mvn -DskipTests package

if (-not (Test-Path $InputJar)) {
  Write-Error "Expected jar not found: $InputJar"
}

Copy-Item $InputJar (Join-Path $InputDir "aliview.jar")

Write-Host "Computing module list with jdeps..."
$JdepsJar = Join-Path $BuildDir "jdeps-aliview.jar"
Copy-Item (Join-Path $InputDir "aliview.jar") $JdepsJar

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
  --output $RuntimeDir

$IconPath = Join-Path $RootDir "icon_images\win\alignment_multi.ico"

$TypeList = $Types.Split(",") | ForEach-Object { $_.Trim() } | Where-Object { $_ }
foreach ($Type in $TypeList) {
  $Args = @(
    "--type", $Type,
    "--name", $AppName,
    "--app-version", $AppVersion,
    "--vendor", $AppVendor,
    "--about-url", $AppUrl,
    "--input", $InputDir,
    "--main-jar", "aliview.jar",
    "--main-class", "aliview.AliView",
    "--icon", $IconPath,
    "--runtime-image", $RuntimeDir,
    "--file-associations", (Join-Path $RootDir "jpackage\file-associations.properties"),
    "--dest", $BuildDir,
    "--win-upgrade-uuid", $WinUpgradeUuid,
    "--win-menu",
    "--win-menu-group", $AppName,
    "--win-shortcut",
    "--win-dir-chooser",
    "--java-options", "-Xmx1024m",
    "--java-options", "-Xms128m"
  )


  Write-Host "Packaging with jpackage: $Type"
  & $Jpackage @Args
}

Write-Host "Done: $BuildDir"
