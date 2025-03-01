# docker build --target <target> -t <tag> .

# Stage 1: Base image.
## Start with a base image containing NodeJS so we can build Docusaurus.
FROM node:lts AS base
## Disable colour output from yarn to make logs easier to read.
ENV FORCE_COLOR=0
## Enable corepack.
RUN corepack enable
## Set the working directory to `/opt/docusaurus`.
WORKDIR /opt/docusaurus

# Stage 2a: Development mode.
# docker run --rm -d -p 3000:3000 -v $(pwd):/opt/docusaurus <tag>
FROM base AS dev
## Set the working directory to `/opt/docusaurus`.
WORKDIR /opt/docusaurus
## Expose the port that Docusaurus will run on.
EXPOSE 3000
## Run the development server.
CMD [ -d "node_modules" ] && npm run start -- --host 0.0.0.0 --poll 1000 || npm install && npm run start -- --host 0.0.0.0 --poll 1000

# Stage 2b: Production build mode.
FROM base AS prod
## Set the working directory to `/opt/docusaurus`.
WORKDIR /opt/docusaurus
## Copy over the source code.
COPY . /opt/docusaurus/
## Install dependencies with `--immutable` to ensure reproducibility.
RUN npm ci
## Build the static site.
RUN npm run build

# Stage 3a: Serve with `docusaurus serve`.
# docker run --rm -d -p 3000:3000 <tag>
FROM prod AS serve
## Expose the port that Docusaurus will run on.
EXPOSE 3000
## Run the production server.
CMD ["npm", "run", "serve", "--", "--host", "0.0.0.0", "--no-open"]

# Stage 3b: Serve with Caddy.
# docker run --rm -d -p 80:80 -p 443:443 <tag>
FROM caddy:2-alpine AS caddy
## Copy the Caddyfile.
# COPY --from=prod /opt/docusaurus/Caddyfile /etc/caddy/Caddyfile
## Copy the Docusaurus build output.
COPY --from=prod /opt/docusaurus/build /var/docusaurus

# Stage 3c: Serve with nginx.
# docker run --rm -d -p 8443:8443 <tag>
FROM registry.access.redhat.com/ubi9/httpd-24 AS nginx
## Copy the Docusaurus build output.
COPY --from=prod /opt/docusaurus/build /var/www/html
RUN chgrp -R 0 /opt/app-root/src && chmod -R g=u /opt/app-root/src
CMD ["run-httpd"]
