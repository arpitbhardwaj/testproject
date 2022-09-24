package com.ab.greeter.server

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.ab.greeter.client.NextActor
import com.google.protobuf.timestamp.Timestamp
import com.typesafe.config.ConfigFactory
import example.myapp.helloworld.grpc.HelloReply

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object EventProcessor extends App {

  val system1 = ActorSystem("RemoteSystem1", ConfigFactory.load("remotingGrpc.conf").getConfig("remoteSystem1"))
  val actor1 = system1.actorOf(Props[NextActor],"NextActor1")
  implicit val timeout = Timeout(3 seconds)
  import system1.dispatcher

  val reply = HelloReply(s"Hello, Arpit", Some(Timestamp.apply(123456, 123)))
  val actorSelection = system1.actorSelection("akka://RemoteSystem2@localhost:2554/user/NextActor2")
  println(actorSelection)
  val remoteActorRefFuture = actorSelection.resolveOne()
  remoteActorRefFuture.onComplete {
    case Success(actorRef) => actorRef ! reply
    case Failure(exception) => println(s"I've failed to resolve the actor because: $exception")
  }



}
