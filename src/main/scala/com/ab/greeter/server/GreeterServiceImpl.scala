package com.ab.greeter.server

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask

import scala.concurrent.Future
import akka.stream.Materializer
import akka.util.Timeout
import com.ab.greeter.client.{NextActor, StopNextActor}
import com.google.protobuf.timestamp.Timestamp
import com.typesafe.config.ConfigFactory
import example.myapp.helloworld.grpc._

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class GreeterServiceImpl(implicit mat: Materializer) extends GreeterService {

  val system2 = ActorSystem("RemoteSystem2", ConfigFactory.load("remotingGrpc.conf").getConfig("remoteSystem2"))
  import system2.dispatcher
  implicit val timeout = Timeout(3 seconds)

  override def sayHello(in: HelloRequest): Future[HelloReply] = {

    //println(s"sayHello to ${in.name}")
    //val reply = HelloReply(s"Hello, ${in.name}", Some(Timestamp.apply(123456, 123)))

    /*val actorSelection = system2.actorSelection("akka://RemoteSystem1@localhost:2553/user/NextActor")
    println(actorSelection)
    val remoteActorRefFuture = actorSelection.resolveOne()
    remoteActorRefFuture.onComplete {
      case Success(actorRef) => actorRef ! reply
      case Failure(exception) => println(s"I've failed to resolve the actor because: $exception")
    }*/

    val nextActor = system2.actorOf(Props[NextActor],"NextActor2")
    val reply = (nextActor ? in).mapTo[Future[HelloReply]].flatten
    //response.onComplete(nextActor ! StopNextActor)
    //Future.successful(reply)
    reply
  }

}