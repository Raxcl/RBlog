FROM node:14-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci || npm i
COPY . .
ARG VUE_APP_URL=/RBlog/
ENV outputDir=dist
ENV VUE_APP_URL=${VUE_APP_URL}
RUN npm run build

FROM nginx:alpine
WORKDIR /usr/share/nginx/html
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/ ./


