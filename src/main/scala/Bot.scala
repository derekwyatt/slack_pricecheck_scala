package com.slackpricecheck.slack

import slack.api.SlackApiClient
import slack.rtm.SlackRtmClient
import slack.models.{Hello, Message}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import akka.actor.ActorSystem
import com.slackpricecheck.itad._

object Bot {
    implicit val system = ActorSystem("slack")
    implicit val itad = ITAD(sys.env("ITAD_TOKEN"))

    var client : SlackApiClient = _
    var rtmClient : SlackRtmClient = _
    var selfId : String = _


    def connect(token: String): Unit = {
      client = SlackApiClient(token)
      rtmClient = SlackRtmClient(token)
      selfId = rtmClient.state.self.id;
    }

    def main(args: Array[String]): Unit ={
      connect(sys.env("SLACK_TOKEN"))
      rtmClient.onMessage { message =>
        if (shouldRespond(message.text)){
          val game = message.text.replaceAll(s"<@$selfId>","").trim()
          val lowestPriceFuture = itad.getLowestPrice(game)

          lowestPriceFuture onSuccess {
            case price =>
              val lowestPrice = price
              val lowest_price_fmt = "%1.2f" format lowestPrice.price_new
              rtmClient.sendMessage(message.channel, s"Found lowest price of $$${lowest_price_fmt} at ${lowestPrice.shop.name}  (${lowestPrice.url})")
          }

          lowestPriceFuture onFailure {
            case exception =>
              rtmClient.sendMessage(message.channel, s"Could not find lowest price for $game.")
          }
        }
      }
    }

    def shouldRespond(message: String): Boolean = message match {
      case msg if msg.startsWith(s"<@$selfId>") => true
      case _ => false
    }
}
