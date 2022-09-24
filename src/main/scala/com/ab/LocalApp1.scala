package com.ab

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
 *
 * When a message is sent to an Actor that is terminated before receiving the message, it will be sent as a DeadLetter to the ActorSystem's EventStream.
 * When this message was sent without a sender ActorRef, sender will be system.deadLetters.
 */
object LocalApp1 extends App {
  val system1 = ActorSystem("LocalApp1")
  val system2 = ActorSystem("LocalSystem1", ConfigFactory.load("remotingBasics.conf"))
  val system3 = ActorSystem("RemoteSystem11", ConfigFactory.load("remotingBasics.conf").getConfig("remoteSystem1"))
  val system4 = ActorSystem("RemoteSystem12", ConfigFactory.load("remotingBasics.conf").getConfig("remoteSystem2"))

  val actor1 = system1.actorOf(Props[SimpleActor],"actor1") //akka://LocalApp1/user/actor1
  val actor2 = system2.actorOf(Props[SimpleActor],"actor2") //akka://LocalSystem1/user/actor2
  val actor3 = system3.actorOf(Props[SimpleActor],"actor3") //akka://RemoteSystem11@<IP>:<PORT>/user/actor3
  val actor4 = system4.actorOf(Props[SimpleActor],"actor4") //akka://RemoteSystem12@localhost:2551/user/actor4

  actor1 ! "hello11, simple actor"

  sleepAndNextLine(actor1)

  val actorSelection1 = system1.actorSelection("akka://LocalApp1/user/actor1")
  actorSelection1 ! "hello12 form the local jvm from same app"

  sleepAndNextLine(actor2)

  val actorSelection2 = system2.actorSelection("akka://LocalSystem1/user/actor2")
  actorSelection2 ! "hello2 form the local jvm from same app"

  sleepAndNextLine(actor3)

  val actorSelection31 = system3.actorSelection("akka://RemoteSystem11/user/actor3")
  actorSelection31 ! "hello31 form the local jvm from same app"

  //change the port whatever is auto selected by the config
  //val actorSelection32 = system3.actorSelection("akka://RemoteSystem11@192.168.37.49:52824/user/actor3")
  //actorSelection32 ! "hello32 form the local jvm from same app"

  sleepAndNextLine(actor4)

  val actorSelection41 = system4.actorSelection("akka://RemoteSystem12/user/actor4")
  actorSelection41 ! "hello41 form the local jvm from same app"

  val actorSelection42 = system4.actorSelection("akka://RemoteSystem12@localhost:2551/user/actor4")
  actorSelection42 ! "hello42 form the local jvm from same app"

  def sleepAndNextLine(actorRef: ActorRef):Unit = {
    Thread.sleep(100)
    println("-------------------------------------------------")
    println(actorRef)
  }
}

