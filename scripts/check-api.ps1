param([string]$BaseUrl = 'http://localhost:8080')
$ErrorActionPreference = 'Stop'
$BaseUrl = $BaseUrl.TrimEnd('/')

function Request($Method, $Path, $Body) {
    $params = @{ Method = $Method; Uri = "$BaseUrl/api$Path" }
    if ($null -ne $Body) {
        $params.ContentType = 'application/json; charset=utf-8'
        $params.Body = [System.Text.Encoding]::UTF8.GetBytes(($Body | ConvertTo-Json))
    }
    Invoke-RestMethod @params
}

function Assert($Condition, $Message) {
    if (-not $Condition) { throw $Message }
}

function AssertMissing($Path) {
    try { Request GET $Path $null | Out-Null }
    catch {
        if ([int]$_.Exception.Response.StatusCode -eq 404) { return }
        throw
    }
    throw "Expected HTTP 404 for $Path"
}

$suffix = [guid]::NewGuid().ToString('N')
$user = $null
try {
    $user = Request POST '/users' @{ name = 'Docker test'; email = "docker-$suffix@example.com" }
    Assert ($user.id -gt 0) 'User creation failed'
    $article = Request POST '/articles' @{ authorId = $user.id; title = 'Docker article'; slug = "docker-$suffix"; content = 'Stored in MySQL' }
    $comment = Request POST '/comments' @{ authorId = $user.id; articleId = $article.id; content = 'Docker comment' }

    $readUser = Request GET "/users/$($user.id)" $null
    $readArticle = Request GET "/articles/$($article.id)" $null
    $readComment = Request GET "/comments/$($comment.id)" $null
    Assert ($readUser.email -eq $user.email) 'User read failed'
    Assert ($readArticle.content -eq 'Stored in MySQL') 'Article read failed'
    Assert ($readComment.articleId -eq $article.id) 'Comment read failed'

    $updatedUser = Request PUT "/users/$($user.id)" @{ name = 'Updated user' }
    $updatedArticle = Request PUT "/articles/$($article.id)" @{ content = 'Updated article' }
    $updatedComment = Request PUT "/comments/$($comment.id)" @{ content = 'Updated comment' }
    Assert ($updatedUser.name -eq 'Updated user') 'User update failed'
    Assert ($updatedArticle.content -eq 'Updated article') 'Article update failed'
    Assert ($updatedComment.content -eq 'Updated comment') 'Comment update failed'
    Assert ((Request GET "/articles/$($article.id)" $null).content -eq 'Updated article') 'Article update was not persisted'
    Assert ((Request GET "/comments/$($comment.id)" $null).content -eq 'Updated comment') 'Comment update was not persisted'
    Assert ((Request GET "/users/$($user.id)" $null).name -eq 'Updated user') 'User update was not persisted'

    $byAuthor = @(Request GET "/articles/by-author/$($user.id)" $null)
    $byArticle = @(Request GET "/comments/by-article/$($article.id)" $null)
    Assert ($byAuthor.id -contains $article.id) 'Article filter failed'
    Assert ($byArticle.id -contains $comment.id) 'Comment filter failed'

    Request DELETE "/comments/$($comment.id)" $null | Out-Null
    AssertMissing "/comments/$($comment.id)"
    Request DELETE "/articles/$($article.id)" $null | Out-Null
    AssertMissing "/articles/$($article.id)"
    Request DELETE "/users/$($user.id)" $null | Out-Null
    AssertMissing "/users/$($user.id)"
    $user = $null
    Write-Host 'PASS: create, read, update, filters and delete for users, articles and comments.'
}
finally {
    if ($null -ne $user) {
        Request DELETE "/users/$($user.id)" $null | Out-Null
    }
}
