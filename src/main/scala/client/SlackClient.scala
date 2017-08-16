package com.pricecheck.client

import slack.models.Message
import slack.api.SlackApiClient
import slack.rtm.SlackRtmClient
import akka.actor.ActorSystem
import scala.concurrent.Future

class SlackClient extends Client {

  connect()

  implicit val system = ActorSystem("slack")
  var client : SlackApiClient = _
  var rtmClient : SlackRtmClient = _
  var selfId : String = _


  def connect(): Unit = {
    client = SlackApiClient(sys.env("SLACK_TOKEN"))
    rtmClient = SlackRtmClient(sys.env("SLACK_TOKEN"))
    selfId = rtmClient.state.self.id
  }

  def sendMessage(target: String, message: String): Future[Long] = {
    rtmClient.sendMessage(target, message)
  }

  def onMessage(f: (Message) => Unit): Unit = {
    rtmClient.onMessage(f)
  }

  def self(): String = selfId
}
