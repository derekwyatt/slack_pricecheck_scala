package com.slackpricecheck.slack

import slack.api.SlackApiClient
import slack.rtm.SlackRtmClient
import slack.models.{Hello, Message}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import akka.actor.ActorSystem
import com.slackpricecheck.itad._

object Bot {
  def apply(token:String): Bot = {
    new Bot(token)
  }
}

class Bot(token: String) {
    implicit val system = ActorSystem("slack")
    implicit val itad = ITAD(sys.env("ITAD_TOKEN"))

    var client : SlackApiClient = _
    var rtmClient : SlackRtmClient = _
    var selfId : String = _


    def connect(): Unit = {
      client = SlackApiClient(token)
      rtmClient = SlackRtmClient(token)
      selfId = rtmClient.state.self.id;
    }

    def main(args: Array[String]): Unit ={
      connect()
      rtmClient.onMessage { message =>
        if (shouldRespond(message.text)){
          val game = message.text.replaceAll(s"<@$selfId>","").trim()
          val lowest_price = itad.getLowestPrice(game).get
          val lowest_price_fmt = "%1.2f" format lowest_price.price_new
          rtmClient.sendMessage(message.channel, s"Found lowest price of $$${lowest_price_fmt} at ${lowest_price.shop.name}  (${lowest_price.url})")
        }
      }
    }

    def shouldRespond(message: String): Boolean = message match {
      case msg if msg.startsWith(s"<@$selfId>") => true
      case _ => false
    }
}
