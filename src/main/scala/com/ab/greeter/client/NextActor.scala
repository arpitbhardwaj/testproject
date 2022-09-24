package com.ab.greeter.client

import akka.actor.{Actor, ActorLogging}
import example.myapp.helloworld.grpc.{HelloReply, HelloRequest}

import scala.concurrent.Promise
case object StopNextActor
class NextActor extends Actor with ActorLogging{

  val promise: Promise[HelloReply] = Promise[HelloReply]()

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"Starting actor: ${self.path.name}")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info(s"Stopping actor: ${self.path.name}")
  }

  def receive: Receive = {
    case request: HelloRequest =>
      val originator = sender()
      log.info(s"NextActor ${self.path} received Request: $request")
      originator ! promise.future
    case reply: HelloReply =>
      log.info(s"NextActor ${self.path} received Response: $reply")
      promise.success(reply)
    case StopNextActor =>
      log.info(s"Stopping NextActor: ${self.path}")
      context.stop(self)
  }
}
