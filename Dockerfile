# Prepare the clojure build image and dependencies
FROM docker.io/clojure:temurin-21-alpine AS build
RUN apk add --update git npm
RUN npm install -g shadow-cljs

# Copy source files into the docker container
WORKDIR /app
COPY . .

# Update game data to the latest release
RUN git submodule update --init

## Temporarily use master branch
RUN cd resources/game && git checkout master

## Replace with the following lines when the next stable game release (after 0.10.8) comes out:
# RUN cd resources/game \
#  && git checkout $(git tag --list "v*" --sort=-v:refname | head -n 1)

# Build
RUN clojure -J-Xmx8g -X:clj:build

# Copy build results to the final image
FROM docker.io/nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
