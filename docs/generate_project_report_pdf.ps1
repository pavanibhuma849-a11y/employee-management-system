param(
  [string]$Input = "docs/PROJECT_REPORT.md",
  [string]$Output = "docs/PROJECT_REPORT.pdf"
)

$ErrorActionPreference = "Stop"

function Has-Command([string]$Name) {
  return [bool](Get-Command $Name -ErrorAction SilentlyContinue)
}

Write-Host "Input:  $Input"
Write-Host "Output: $Output"

if (Has-Command "pandoc") {
  pandoc $Input -o $Output
  Write-Host "Created PDF via pandoc: $Output"
  exit 0
}

if (Has-Command "wkhtmltopdf") {
  $tmpHtml = Join-Path $env:TEMP "ems_project_report.html"

  if (-not (Has-Command "pandoc")) {
    throw "wkhtmltopdf detected, but HTML generation requires pandoc. Install pandoc or generate HTML manually."
  }

  pandoc $Input -o $tmpHtml
  wkhtmltopdf $tmpHtml $Output
  Write-Host "Created PDF via wkhtmltopdf: $Output"
  exit 0
}

Write-Host ""
Write-Host "No PDF generator found."
Write-Host "Options:"
Write-Host "  1) Install pandoc, then rerun this script."
Write-Host "     - Windows: winget install JohnMacFarlane.Pandoc"
Write-Host "  2) Open docs/PROJECT_REPORT.md in VS Code/Cursor (Markdown Preview) and use Print -> Microsoft Print to PDF."
exit 1

