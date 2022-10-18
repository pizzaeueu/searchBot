package seatch_bot.domain

import com.bot4s.telegram.models.ChatType.Channel
import com.bot4s.telegram.models.{Chat, Message}
import com.search_bot.domain.Messages
import com.search_bot.domain.Messages.{
  CommandNotSupported,
  GetArticle,
  ScanArticle
}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MessagesSpec extends AnyFlatSpec with Matchers {

  "Of method" should "generate valid scan command for /scan message" in {
    val url = "test.url"
    val mockChat = Chat(id = 1, `type` = Channel)
    val mockMessage = Message(
      messageId = 1,
      date = 1,
      chat = mockChat,
      text = Some(s"/scan $url"),
    )

    Messages.of(mockMessage) shouldBe (ScanArticle(url, mockChat.id))

  }

  "Of method" should "generate valid get command for /find message" in {
    val keyword = "keyword"
    val mockChat = Chat(id = 1, `type` = Channel)
    val mockMessage = Message(
      messageId = 1,
      date = 1,
      chat = mockChat,
      text = Some(s"/find $keyword"),
    )

    Messages.of(mockMessage) shouldBe (GetArticle(keyword, mockChat.id))

  }

  "Of method" should "generate valid unsupported command for unsupported message" in {
    val invalidCommand = "/asdfa"
    val mockChat = Chat(id = 1, `type` = Channel)
    val mockMessage = Message(
      messageId = 1,
      date = 1,
      chat = mockChat,
      text = Some(invalidCommand),
    )

    Messages.of(mockMessage) shouldBe (CommandNotSupported(mockChat.id,
                                                           invalidCommand))

  }

}
