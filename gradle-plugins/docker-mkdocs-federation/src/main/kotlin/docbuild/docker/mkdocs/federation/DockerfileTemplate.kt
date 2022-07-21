package docbuild.docker.mkdocs.federation

object DockerfileTemplate {

    private const val copySourceLines = "#{COPY_SOURCE_LINES}"
    private const val buildLines = "#{BUILD_LINES}"
    private const val copySiteLines = "#{COPY_SITE_LINES}"

    fun render(template: String, projectNames: List<String>): String {
        return template
            .replace(copySourceLines, projectNames.joinToString("\n") { "COPY ./$it-mkdocs /$it-mkdocs" })
            .replace(buildLines, projectNames.joinToString("\n    ") { "&& cd /$it-mkdocs && mkdocs build -d site \\" })
            .replace(copySiteLines, projectNames.joinToString("\n") { "COPY --from=static-site /$it-mkdocs/site /$it" })
    }

    const val template =
"""FROM python@sha256:869b8a1b543b5b29d1cf71ef08fd36e0da1eccd4993a422fc6f5b321043ab42a as mkdocs

RUN pip install --upgrade pip \
    && pip install \
      mkdocs==1.3.0 \
      mkdocs-material==8.3.9 \
    && pip freeze

FROM mkdocs as static-site

$copySourceLines

RUN ls \
    $buildLines
    && echo "Finished building sites"

FROM nginx@sha256:20a1077e25510e824d6f9ce7af07aa02d86536848ddab3e4ef7d1804608d8125

$copySiteLines

COPY ./nginx.conf /etc/nginx/nginx.conf
"""
}