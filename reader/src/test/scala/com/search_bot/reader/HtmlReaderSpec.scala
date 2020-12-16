package com.search_bot.reader

import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HtmlReaderSpec extends AnyFlatSpec with Matchers with MockFactory {
  // private val app = HttpApp[IO](r =>
  //   Response[IO](Ok).withEntity(r.body).pure[IO])
  // val client: Client[IO] = Client.fromHttpApp(app)
//
  // private val fakeHtmlParser = mock[HtmlParser[IO]]
  // private val fakeClient = mock[Client[IO]]
//
//
  // "HtmlReader" should "load keywords for valid url" in {
  //   implicit val cs: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)
  //   val reader = HtmlReader.of[IO](fakeClient, fakeHtmlParser)
  //   val url = "www.test.com"
//
  //   (fakeClient.expect[String](url) _).expect()
//
  //   val res = reader.retrieveKeywords("www.test.com").unsafeRunSync()
  // }

}
