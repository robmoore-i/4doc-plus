FROM python@sha256:869b8a1b543b5b29d1cf71ef08fd36e0da1eccd4993a422fc6f5b321043ab42a as mkdocs

RUN pip install --upgrade pip \
    && pip install \
      mkdocs==1.3.0 \
      mkdocs-material==8.3.9 \
    && pip freeze

FROM mkdocs as static-site

COPY . /mkdocs-src

RUN cd mkdocs-src && mkdocs build -d site

FROM nginx@sha256:20a1077e25510e824d6f9ce7af07aa02d86536848ddab3e4ef7d1804608d8125

COPY --from=static-site /mkdocs-src/site /docs-site

COPY ./nginx.conf /etc/nginx/nginx.conf
