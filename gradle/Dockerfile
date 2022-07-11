FROM python@sha256:869b8a1b543b5b29d1cf71ef08fd36e0da1eccd4993a422fc6f5b321043ab42a as mkdocs

COPY . /mkdocs-src

RUN pip install --upgrade pip \
    && pip install mkdocs mkdocs-material \
    && cd mkdocs-src \
    && mkdocs build

FROM nginx@sha256:20a1077e25510e824d6f9ce7af07aa02d86536848ddab3e4ef7d1804608d8125

COPY --from=mkdocs /mkdocs-src/site /docs-site

COPY ./nginx.conf /etc/nginx/nginx.conf
