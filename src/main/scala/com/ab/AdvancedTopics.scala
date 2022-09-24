package com.ab

import akka.actor.{Actor, ActorLogging, ActorSystem, Address, AddressFromURIString, Deploy, PoisonPill, Props, Terminated}
import akka.remote.RemoteScope
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory

object DeployingActorsRemotely_LocalApp extends App {
  val system = ActorSystem("LocalActorSystem", ConfigFactory.load("advancedTopics.conf")
    .getConfig("localApp"))
  val remoteActor = system.actorOf(Props[SimpleActor],"remoteActor")
  remoteActor ! "hello, remote actor"

  //expected: akka://RemoteSystem@localhost:2552/user/remoteActor
  //actual: akka://RemoteActorSystem@localhost:2552/remote/akka/LocalActorSystem@localhost:2551/user/remoteActor
  println(remoteActor)

  //programmatic remote deployment
  val remoteSystemAddress:Address = AddressFromURIString("akka://RemoteActorSystem@localhost:2552/remote/akka/LocalActorSystem@localhost:2551/user/remoteActor")
  val remotelyDeployedActor = system.actorOf(
    Props[SimpleActor].withDeploy(
      Deploy(scope = RemoteScope(remoteSystemAddress))
    )
  )
  remotelyDeployedActor ! "hi, remotely deployed actor"


  //routers with routes deployed remotely
  //val poolRouter = system.actorOf(FromConfig.props(Props[SimpleActor]), "myRouterWithRemoteChildren")
  //(1 to 10).map(i => s"message $i").foreach(poolRouter ! _)

  //watching remote actors
  class ParentActor extends Actor with ActorLogging{
    override def receive: Receive = {
      case "create" =>
        log.info("Creating remote child")
        val child = context.actorOf(Props[SimpleActor], "remoteChild")
        context.watch(child)
      case Terminated(ref) =>
        log.warning(s"Child $ref terminated")
    }
  }

  val watcher = system.actorOf(Props[ParentActor], "watcher")
  watcher ! "create"

  Thread.sleep(1000)
  //val actorSel = system.actorSelection("akka://RemoteActorSystem@localhost:2552/remote/akka/LocalActorSystem@localhost:2551/user/watcher/remoteChild")
  //actorSel ! PoisonPill

  /**
   * The Phi accrual failure detector
   *  actor systems sends heartbeat messages once a connection is established
   *    sending a message
   *    deploying a remote actor
   *  if the heart beat times out its reach score(PHI) increases
   *  if the PHI score passes a threshold, the connection is quarantines = unreachable
   *  the local actor system sends Terminated messages to Death Watchers of remote actors
   *  the remote actor system must be restarted to reestablish connection
   */
}


object DeployingActorsRemotely_RemoteApp extends App {
  val remoteSystem = ActorSystem("RemoteActorSystem", ConfigFactory.load("advancedTopics.conf")
    .getConfig("remoteApp"))
}