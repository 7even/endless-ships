# Endless Sky reference site generator

This is a tool for generating a website with reference information about the [Endless Sky](https://endless-sky.github.io/) game.

The generator works by parsing the game's sources (specifically `data/*.txt` files) to get all game data so the information always stays up-to-date.

## Installation (docker)

*Does not work at the moment (contributions are welcome).*

You can install, build, and run this quickly with [Docker](https://www.docker.com/).

Clone the project:

```sh
$ git clone git@github.com:7even/endless-ships.git
$ cd endless-ships
```

Build and run (this may take a few minutes to complete):

```sh
docker run --rm -d -p 8000:80 $(docker build -q .)
```

Then connect to it with [http://localhost:8000](http://localhost:8000).

To take it down afterwards, you can use `docker kill $(docker ps -q)`.

## Installation (manual)

Alternatively, you can build it manually in your own environment.

First you need to clone the project and pull the game which is linked as a git submodule:

``` sh
$ git clone git@github.com:7even/endless-ships.git
$ cd endless-ships
$ git submodule update --init
```

The generator uses [tools.deps](https://clojure.org/guides/deps_and_cli/) so you need to [install](https://clojure.org/guides/install_clojure) it (`brew install clojure` on OS X). It installs all the other Clojure dependencies upon first launch.

The client-side part of the site is being compiled using [npm](https://www.npmjs.com/) so you need to [install](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm) it as well (`brew install node` on OS X).

## Generating the site

Just run the `clj -X:clj:build` command from the root directory of the application. It will put the generated files under the `build/` directory, ready to be deployed to the server.

## Viewing the site locally

If you don't want to deploy the site to the server you can view it locally - there's [a bunch of ways](https://gist.github.com/willurd/5720255) to start a simple HTTP server for serving static files. For example here's how one would do that with Ruby:

``` sh
$ cd build # the generated site will be available at build/
$ ruby -rwebrick -e'WEBrick::HTTPServer.new(:Port => 8000, :DocumentRoot => Dir.pwd).start'
```

Then point your browser to [http://localhost:8000](http://localhost:8000).
