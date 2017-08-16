package com.pricecheck.client

//How do I get rid of this here?
import slack.models.Message
import scala.concurrent.Future

trait Client {
  def connect(): Unit
  def sendMessage(target: String, message: String): Future[Any]
  def onMessage(f: (Message) => Unit): Unit
  def self():String
}
