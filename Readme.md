# Searcher Bot [![Build Status](https://travis-ci.com/SamosadovArtem/searchBot.svg?token=qnvcRrUMF2GcwbChxqya&branch=master)](https://travis-ci.com/github/SamosadovArtem/searchBot) [![Coverage Status](https://coveralls.io/repos/github/SamosadovArtem/searchBot/badge.svg?branch=master)](https://coveralls.io/github/SamosadovArtem/searchBot?branch=master)
Telegram bot which helps you to find articles by keywords

#How to run
- Presentation mode

The simplest way to run search bot is running [docker-compose](https://github.com/SamosadovArtem/searchBot/blob/master/docker/docker-compose.yml) 
file with your own bot token.

In order to do it you can use either `docker compose up` or `sbt dockerComposeUp -useStaticPorts` command

- Development mode

If you want to run bot locally you need to update [application.conf](https://github.com/SamosadovArtem/searchBot/blob/master/main/src/main/resources/application.conf)
with your db config data and run it with `sbt run` command

#How to use

Search Bot has 2 command:
- `/scan {url}`
- `/find {keyword}`

Scan command will retrieve keywords from the provided url and save article.
In order to find article you need to call Find command with needed keyword.
e.g.

`/scan https://docs.scala-lang.org/`

`/find scala`

 

