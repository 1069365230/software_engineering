# Base image
FROM node:19-alpine as build-stage

# Setting working directory
WORKDIR /app

# Installing dependencies
COPY package*.json ./

RUN npm cache clear --force

RUN npm install

# Copying rest of the client-side code
COPY . .


# Building app
RUN npm run build

# Stage 2
FROM nginx:1.19.0-alpine as production-stage

# Copying compiled app to nginx
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80

# Running nginx
CMD ["nginx", "-g", "daemon off;"]

