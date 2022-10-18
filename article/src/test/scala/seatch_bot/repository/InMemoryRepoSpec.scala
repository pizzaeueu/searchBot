package seatch_bot.repository

import cats.effect.IO
import cats.effect.Ref
import cats.implicits.{catsSyntaxOptionId, none}
import com.search_bot.domain.Article.{Article, ArticleUrl, ArticleWords, ChatId}
import com.search_bot.repository.ArticleRepository
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.unsafe.IORuntime

class InMemoryRepoSpec extends AnyFlatSpec with Matchers with MockFactory {

  implicit val runtime = IORuntime.global

  "In memory repo" should "retrieve data by keyword for chat" in {
    val keyword = "Test"
    val keywordList = List(keyword)
    val chatId = 1L
    val article = Article(
      ArticleUrl("test"),
      ChatId(chatId),
      ArticleWords(keywordList),
    )

    val res = for {
      ref <- Ref.of[IO, Vector[Article]](Vector())
      repo <- IO.pure(ArticleRepository.inMemory(ref))
      _ <- repo.saveArticle(article)
      out <- repo.getByKeywordForChat(keyword, chatId)
    } yield out

    res.unsafeRunSync() shouldBe List(article)

  }

  "In memory repo" should "retrieve data by url for chat" in {
    val chatId = 1L
    val url = "www.test.com"
    val article = Article(
      ArticleUrl(url),
      ChatId(chatId),
      ArticleWords(List("Test")),
    )

    val res = for {
      ref <- Ref.of[IO, Vector[Article]](Vector())
      repo <- IO.pure(ArticleRepository.inMemory(ref))
      _ <- repo.saveArticle(article)
      out <- repo.getByUrlForChat(url, chatId)
    } yield out

    res.unsafeRunSync() shouldBe article.some
  }

  "In memory repo" should "retrieve empty result by missing url for chat" in {
    val res = for {
      ref <- Ref.of[IO, Vector[Article]](Vector())
      repo <- IO.pure(ArticleRepository.inMemory(ref))
      out <- repo.getByUrlForChat("Test", 1L)
    } yield out

    res.unsafeRunSync() shouldBe none

  }

}
