param(
    [string]$ClientUrl = 'http://localhost:8080',
    [string]$ServerUrl = 'http://localhost:8081'
)
$ErrorActionPreference = 'Stop'
$ClientUrl = $ClientUrl.TrimEnd('/')
$ServerUrl = $ServerUrl.TrimEnd('/')
$suffix = [guid]::NewGuid().ToString('N')
$user = $null
try {
    $user = Invoke-RestMethod -Method Post -Uri "$ServerUrl/api/users" -ContentType application/json `
        -Body (@{name='Feign user';email="feign-$suffix@example.com"} | ConvertTo-Json)
    $article = Invoke-RestMethod -Method Post -Uri "$ServerUrl/api/articles" -ContentType application/json `
        -Body (@{authorId=$user.id;title='Feign article';slug="feign-$suffix";content='Remote content'} | ConvertTo-Json)
    $result = Invoke-RestMethod "$ClientUrl/api/remote/users/$($user.id)/articles"
    if ($result.user.id -ne $user.id -or $result.user.email -ne $user.email) {
        throw 'Remote user does not match'
    }
    if (@($result.articles).Count -ne 1 -or $result.articles[0].id -ne $article.id `
        -or $result.articles[0].content -ne 'Remote content') {
        throw 'Remote articles do not match'
    }
    Invoke-RestMethod -Method Delete -Uri "$ServerUrl/api/users/$($user.id)" | Out-Null
    try {
        Invoke-RestMethod "$ClientUrl/api/remote/users/$($user.id)/articles" | Out-Null
        throw 'Expected HTTP 404'
    } catch {
        if ([int]$_.Exception.Response.StatusCode -ne 404) { throw }
    }
    $user = $null
    Write-Host 'PASS: Feign retrieved user and articles from the server; missing user returns 404.'
}
finally {
    if ($null -ne $user) {
        Invoke-RestMethod -Method Delete -Uri "$ServerUrl/api/users/$($user.id)" | Out-Null
    }
}
