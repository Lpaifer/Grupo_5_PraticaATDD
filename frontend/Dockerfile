FROM node:22-alpine AS build

WORKDIR /app
ENV CYPRESS_INSTALL_BINARY=0
COPY package.json package-lock.json ./
RUN for attempt in 1 2 3; do \
      npm ci --no-audit --no-fund && exit 0; \
      if [ "$attempt" -eq 3 ]; then exit 1; fi; \
      sleep 3; \
    done
COPY . .
RUN npm run build

FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
