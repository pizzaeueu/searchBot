package seatch_bot.service

import cats.effect.IO
import com.bot4s.telegram.methods.SendMessage
import com.search_bot.domain.Article.{Article, ArticleUrl, ArticleWords, ChatId}
import com.search_bot.domain.Messages.{GetArticle, ScanArticle}
import com.search_bot.domain.Responses.{
  ArticleAlreadyExists,
  FailHandleMessage,
  SuccessfullySave
}
import com.search_bot.reader.HtmlReader
import com.search_bot.repository.ArticleRepository
import com.search_bot.service.MessageService
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.unsafe.IORuntime

class MessageServiceSpec extends AnyFlatSpec with Matchers with MockFactory {

  implicit val runtime = IORuntime.global

  private val fakeArticleRepo = mock[ArticleRepository[IO]]
  private val fakeHtmlReader = mock[HtmlReader[IO]]

  "Message Service" should "save articles for valid scan command" in {

    val service = MessageService.of[IO](fakeArticleRepo, fakeHtmlReader)
    val url = "url"
    val chatId = 1L
    val keywords = List("keyword")
    val message = ScanArticle(url, chatId)

    (fakeHtmlReader.retrieveKeywords _).expects(url).returns(IO(keywords))
    (fakeArticleRepo.getByUrlForChat _).expects(url, chatId).returns(IO(None))
    (fakeArticleRepo.saveArticle _)
      .expects(Article(ArticleUrl(url), ChatId(chatId), ArticleWords(keywords)))
      .returns(IO(1))

    val res = service
      .handle(message)
    res.unsafeRunSync() shouldBe SuccessfullySave(SendMessage(chatId, "Saved"))
  }

  "Message Service" should "show error for duplicated article" in {

    val service = MessageService.of[IO](fakeArticleRepo, fakeHtmlReader)
    val url = "url"
    val chatId = 1L
    val keywords = List("keyword")
    val message = ScanArticle(url, chatId)

    (fakeHtmlReader.retrieveKeywords _).expects(url).returns(IO(keywords))
    (fakeArticleRepo.getByUrlForChat _)
      .expects(url, chatId)
      .returns(IO(
        Some(Article(ArticleUrl(url), ChatId(chatId), ArticleWords(keywords)))))

    val res = service
      .handle(message)
    res.unsafeRunSync() shouldBe ArticleAlreadyExists(
      SendMessage(chatId, "Article has already saved"))

  }

  "Message Service" should "retrieve articles for valid get command" in {

    val service = MessageService.of[IO](fakeArticleRepo, fakeHtmlReader)
    val keyword = "keyword"
    val chatId = 1L
    val requestedUrl = "www.test.com"
    val message = GetArticle(keyword, chatId)
    val articles = List(
      Article(ArticleUrl(requestedUrl),
              ChatId(chatId),
              ArticleWords(List(keyword))))

    (fakeArticleRepo.getByKeywordForChat _)
      .expects(keyword, chatId)
      .returns(IO(articles))

    val res = service
      .handle(message)
    res.unsafeRunSync() shouldBe SuccessfullySave(
      SendMessage(chatId, requestedUrl))

  }

  "Message Service" should "send error message for unexpected error" in {
    val service = MessageService.of[IO](fakeArticleRepo, fakeHtmlReader)
    val keyword = "keyword"
    val chatId = 1L
    val err = new RuntimeException("Test Error")
    val message = GetArticle(keyword, chatId)

    (fakeArticleRepo.getByKeywordForChat _)
      .expects(keyword, chatId)
      .returns(IO.raiseError(err))

    val res = service
      .handle(message)

    res.unsafeRunSync() shouldBe FailHandleMessage(
      SendMessage(
        chatId,
        s"Unexpected Error during process. Please contact owner for details. Error Message - ${err.getMessage}",
      )
    )

  }
}
