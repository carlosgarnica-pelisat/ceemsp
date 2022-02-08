FROM node:12.20-alpine AS build
WORKDIR /usr/src/app
COPY package.json package.json
RUN npm install @angular/cli -g
RUN npm i --silent
COPY . .
# A head-of-time compilation
RUN ng build --prod --output-path=/dist

# stage 2
FROM nginx:1.17.1-alpine
WORKDIR /usr/src/app
RUN rm -rf /usr/share/nginx/html/*
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=0 /dist /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]
