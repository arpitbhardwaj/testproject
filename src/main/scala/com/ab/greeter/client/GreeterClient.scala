package com.ab.greeter.client

import akka.actor.{ActorSystem, Props}
import akka.grpc.GrpcClientSettings
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import example.myapp.helloworld.grpc.{GreeterService, GreeterServiceClient, HelloRequest}

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


object GreeterClient {

  implicit val sys = ActorSystem("HelloWorldClient", ConfigFactory.load("remotingGrpc.conf").getConfig("grpcSystem"))
  implicit val ec = sys.dispatcher

  @throws[InterruptedException]
  def main(args: Array[String]): Unit = {

    val clientSettings = GrpcClientSettings.connectToServiceAt("127.0.0.1", 8080).withTls(false)
    val client: GreeterService = GreeterServiceClient(clientSettings)

    if (args.length == 0) System.out.println("Need one argument to proceed")
    args(0) match {
      case "greet" =>
        doGreet(client)
      case _ =>
        System.out.println("Invalid argument")
    }
  }

  private def doGreet(client: GreeterService): Unit = {
    sys.log.info("Performing request")
    val reply = client.sayHello(HelloRequest("Alice"))
    reply.onComplete {
      case Success(msg) =>
        println(s"got single reply: $msg")
      case Failure(e) =>
        println(s"Error sayHello: $e")
    }
  }
}
