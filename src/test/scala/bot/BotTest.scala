import org.scalatest.FlatSpec
import com.slackpricecheck.slack.Bot

class BotTest extends FlatSpec {


  "A bot" should "only speak when spoken to" in {
    Bot.selfId = "bot"
    assert(Bot.shouldRespond("<@bot> do stuff"))
    assert(!Bot.shouldRespond("Don't respond bot!"))
  }

}
