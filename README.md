# Endless Sky reference site generator

This is a tool for generating a website with reference information about the [Endless Sky](https://endless-sky.github.io/) game. Currently the generated site only includes the ships data.

The generator works by parsing the game's sources (specifically `data/*ships.txt` files) to get all ships' attributes so the information always stays up-to-date.

## Installation

The generator uses [Boot](http://boot-clj.com/) so you need to [install](https://github.com/boot-clj/boot#install) it first (`brew install boot-clj` on OS X). It installs all the other Clojure dependencies upon first launch.

The client-side part of the site is being compiled using [yarn](https://yarnpkg.com) so you need to [install](https://yarnpkg.com/en/docs/install) it as well (`brew install yarn` on OS X).

## Generating the site

Just run the `boot build` command from the root directory of the application. It will put the generated files under the `build/` directory, ready to be deployed to the server.

## Roadmap / what can be improved

* add a separate page for detailed ship description (including the ship sprite from the game)
* parse outfits as well to generate an outfit table
