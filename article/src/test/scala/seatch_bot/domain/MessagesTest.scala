package seatch_bot.domain

import com.bot4s.telegram.models.ChatType.Channel
import com.bot4s.telegram.models.{Chat, Message}
import com.search_bot.domain.Messages
import com.search_bot.domain.Messages.ScanArticle
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class MessagesTest extends AnyFlatSpec with should.Matchers {

  "Of method" should "generate valid scan command for /scan message" in {
    val url = "test.url"
    val mockChat = Chat(id = 1, `type` = Channel)
    val mockMessage = Message(
      messageId = 1,
      date = 1,
      chat = mockChat,
      text = Some(s"/scan $url")
    )

    Messages.of(mockMessage) shouldBe (ScanArticle(url, mockChat.id))

  }

}
