def getDockerHubTags(repository) {
    def url = "https://hub.docker.com/v2/repositories/${repository}/tags"
    def response = sh(script: "curl -s ${url}", returnStdout: true).trim()
    def tags = []
    def json = readJSON(text: response)
    
    json.results.each { result ->
        tags.add(result.name)
    }
    
    return tags
}

def repository = 'vkorotetskiy/lab'
def availableTags = getDockerHubTags(repository)

return availableTags
