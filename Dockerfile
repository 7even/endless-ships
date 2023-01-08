# Prepare the boot build image and dependencies
FROM clojure:boot-alpine AS boot
RUN apk add --update git npm
RUN npm install -g yarn shadow-cljs

# Copy source files into the docker container
WORKDIR /app
COPY . .

# Update game data to the latest release
RUN git submodule set-url resources/game https://github.com/endless-sky/endless-sky.git
RUN git submodule update --init
RUN cd resources/game \
 && git checkout master \
 && git checkout $(git tag --list "v*" --sort=-v:refname | head -n 1)

# Build
RUN boot build

# Copy build results to the final image
FROM nginx:alpine
COPY --from=boot /app/build /usr/share/nginx/html

EXPOSE 80
