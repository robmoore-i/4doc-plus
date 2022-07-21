package docbuild.docker.mkdocs.federation

object NginxConfTemplate {

    private const val locationLines = "#{LOCATION_LINES}"

    fun render(template: String, projectNames: List<String>): String {
        return template
            .replace(locationLines, projectNames.joinToString("\n\n        ") {
                """
        location /$it {
            root /;
        }""".trimStart()
            })
    }

    const val template =
        """events {}
http {
    include mime.types;

    server {
        listen 8080;
        listen [::]:8080;

        $locationLines
    }
}
"""
}