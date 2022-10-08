package com.search_bot

import cats.effect.Ref
import cats.effect.{Async, Resource}
import com.search_bot.bot.SearchBot
import com.search_bot.configuration.{DBConfiguration, SearchBotConfiguration}
import com.search_bot.configuration.SearchBotConfiguration.DatabaseConfig
import com.search_bot.domain.Article.Article
import com.search_bot.domain.BotToken
import com.search_bot.reader.HtmlReader
import com.search_bot.repository.ArticleRepository
import com.search_bot.service.{HtmlParser, MessageService}
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import org.http4s.blaze.client.BlazeClientBuilder

object Program {
  def dsl[F[_]: Async](inMemory: Boolean): Resource[F, Unit] = {
    for {
      (botTokenConfig, databaseConfig) <- makeConf()
      articleRepo <- if (inMemory) makeInMemoryRepos()
      else makeRepos(databaseConfig)
      messageService <- makeServices(articleRepo)
      server <- Resource.eval(AsyncHttpClientCatsBackend[F]())
      bot <- SearchBot.make(botTokenConfig, server, messageService)
      _ <- Resource.eval(bot.run())
    } yield ()
  }

  def makeConf[F[_]: Async]()
    : Resource[F, (BotToken, SearchBotConfiguration.DatabaseConfig)] =
    for {
      botTokenConfig <- Resource.eval(SearchBotConfiguration.getBotToken)
      databaseConfig <- Resource.eval(SearchBotConfiguration.getDatabaseConfig)
    } yield (botTokenConfig, databaseConfig)

  def makeRepos[F[_]: Async](
      config: DatabaseConfig): Resource[F, ArticleRepository[F]] =
    for {
      _ <- Resource.eval(db.Configuration.migrate(config))
      transactor <- DBConfiguration.getDbConnectionResource(
        config
      )
      articleRepo = ArticleRepository.of(transactor)
    } yield articleRepo

  def makeInMemoryRepos[F[_]: Async]() =
    for {
      ref <- Resource.eval(Ref.of[F, Vector[Article]](Vector()))
    } yield ArticleRepository.inMemory(ref)

  def makeServices[F[_]: Async](
      articleRepository: ArticleRepository[F]): Resource[F, MessageService[F]] =
    for {
      clientResource <- BlazeClientBuilder[F].resource
      parser = HtmlParser.make
      articleReader = HtmlReader.of(clientResource, parser)
      messageService = MessageService.of(articleRepository, articleReader)
    } yield messageService
}
