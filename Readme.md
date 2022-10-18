# Searcher Bot 
[![Build Status](https://github.com/pizzaeueu/searchBot/actions/workflows/scala.yml/badge.svg?branch=master)](https://github.com/pizzaeueu/searchBot/actions/workflows/scala.yml)
[![Coverage Status](https://coveralls.io/repos/github/SamosadovArtem/searchBot/badge.svg?branch=master)](https://coveralls.io/github/SamosadovArtem/searchBot?branch=master)

Telegram bot which helps you to find articles by keywords

# How to run
- Presentation mode

The simplest way to run search bot is running [docker-compose](https://github.com/SamosadovArtem/searchBot/blob/master/docker/docker-compose.yml) 
file with your own bot token.

In order to do it you can use either `docker compose up` or `sbt dockerComposeUp -useStaticPorts` command

- Development mode

If you want to run bot locally you need to update [application.conf](https://github.com/SamosadovArtem/searchBot/blob/master/main/src/main/resources/application.conf)
with your db config data / bot token and run it with `sbt run` command

- Development mode (In memory DB)

It also possible to start application without db connection. 
In order to use this mode run `sbt "run inMemory"` command.

_warning: with `In Memory` mode your data will be available as long as your app is working._ 




# How to use

Search Bot supports 3 commands:
- `/scan {url}`
- `/find {keyword}`
- `/help` or `/start`

Scan command will retrieve keywords from the provided url and save article.
In order to find article you need to call Find command with a needed keyword.
e.g.

`/scan https://docs.scala-lang.org/`

`/find scala`

You can use either `/start` or `/help` command in order to see the list of available commands

 

