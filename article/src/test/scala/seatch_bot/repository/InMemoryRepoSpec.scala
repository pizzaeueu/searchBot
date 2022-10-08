package seatch_bot.repository

import cats.effect.IO
import cats.effect.Ref
import cats.implicits.{catsSyntaxOptionId, none}
import com.evolutiongaming.catshelper.testkit.PureTest
import com.search_bot.domain.Article.{Article, ArticleUrl, ArticleWords, ChatId}
import com.search_bot.repository.ArticleRepository
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class InMemoryRepoSpec extends AnyFlatSpec with Matchers with MockFactory {

  "In memory repo" should "retrieve data by keyword for chat" in {
    PureTest.ioTest { _ =>
      val keyword = "Test"
      val keywordList = List(keyword)
      val chatId = 1L
      val article = Article(
        ArticleUrl("test"),
        ChatId(chatId),
        ArticleWords(keywordList)
      )

      val res = for {
        ref <- Ref.of[IO, Vector[Article]](Vector())
        repo <- IO.pure(ArticleRepository.inMemory(ref))
        _ <- repo.saveArticle(article)
        out <- repo.getByKeywordForChat(keyword, chatId)
      } yield out

      res.map(_ shouldBe List(article))

    }
  }

  "In memory repo" should "retrieve data by url for chat" in {
    PureTest.ioTest { _ =>
      val chatId = 1L
      val url = "www.test.com"
      val article = Article(
        ArticleUrl(url),
        ChatId(chatId),
        ArticleWords(List("Test"))
      )

      val res = for {
        ref <- Ref.of[IO, Vector[Article]](Vector())
        repo <- IO.pure(ArticleRepository.inMemory(ref))
        _ <- repo.saveArticle(article)
        out <- repo.getByUrlForChat(url, chatId)
      } yield out

      res.map(_ shouldBe article.some)

    }
  }

  "In memory repo" should "retrieve empty result by missing url for chat" in {
    PureTest.ioTest { _ =>
      val res = for {
        ref <- Ref.of[IO, Vector[Article]](Vector())
        repo <- IO.pure(ArticleRepository.inMemory(ref))
        out <- repo.getByUrlForChat("Test", 1L)
      } yield out

      res.map(_ shouldBe none)

    }
  }

}
