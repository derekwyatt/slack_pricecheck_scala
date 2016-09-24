import slack.api.SlackApiClient
import slack.rtm.SlackRtmClient
import slack.models.{Hello}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import akka.actor.ActorSystem

object Bot {
  def apply(token:String): Bot = {
    new Bot(token)
  }
}

class Bot(token: String) {
    implicit val system = ActorSystem("slack")

    var client : SlackApiClient = _
    var rtmClient : SlackRtmClient = _

    def connect(): Unit = {
      client = SlackApiClient(token)
      rtmClient = SlackRtmClient(token)
    }

    def main(args: Array[String]): Unit ={

      rtmClient.onEvent{
        case e: Hello =>
          client.listGroups().onComplete {
              case Success(channels) =>
                for (channel <- channels) {
                  println("HELLLOOOO " + channel.id)
                  rtmClient.sendMessage(channel.id, "Hello!")
                }
              case Failure(err) =>
                println(err)
          }
        case _ => ;

      }

    }
}
