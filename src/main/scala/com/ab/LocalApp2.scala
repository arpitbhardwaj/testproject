package com.ab

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, ActorSelection, ActorSystem, Identify, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object LocalApp2 extends App {
  val system1 = ActorSystem("LocalApp2")
  val system2 = ActorSystem("RemoteSystem2", ConfigFactory.load("remotingBasics.conf").getConfig("remoteSystem3"))

  //Method 1: Send msg to selected actor
  val actorSelection5 = system2.actorSelection("akka://RemoteSystem11@192.168.112.49:60000/user/actor3")
  sleepAndNextLine(actorSelection5)
  //actorSelection5 ! "hello5 form the local jvm from parallel app"

  //Method 2: Resolve the actor selection to an actor ref
  import system2.dispatcher
  implicit val timeout = Timeout(3 seconds)
  val remoteActorRefFuture = actorSelection5.resolveOne()
  remoteActorRefFuture.onComplete{
    case Success(actorRef) => actorRef ! "I've resolved you in a future, hello5 form the local jvm from parallel app"
    case Failure(exception) => println(s"I've failed to resolve the actor because: $exception")
  }

  //Method 3: Actor identification via messages
  /*
  -actor resolved will ask for an actor selection from local actor system
  actor resolver will send a Identify(42) to the actor selection
  the remote actor will automatically with ActorIdentify(42, actorRef)
   */

  class ActorResolver extends Actor with ActorLogging{
    override def preStart(): Unit = {
      val actorSelection6 = system2.actorSelection("akka://RemoteSystem12@localhost:2551/user/actor4")
      sleepAndNextLine(actorSelection6)
      actorSelection6 ! Identify(42)
    }
    override def receive: Receive = {
      case ActorIdentity(42, Some(actorRef)) =>
        actorRef ! "Thank you for identifying yourself, hello6 form the local jvm from parallel app"
    }
  }
  system2.actorOf(Props[ActorResolver], "actorResolver")

  def sleepAndNextLine(actorRef: ActorSelection):Unit = {
    Thread.sleep(100)
    println("-------------------------------------------------")
    println(actorRef)
  }
}
