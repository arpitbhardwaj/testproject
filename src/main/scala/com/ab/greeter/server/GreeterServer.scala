package com.ab.greeter.server


import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import example.myapp.helloworld.grpc._
import scala.concurrent.{ExecutionContext, Future}


object GreeterServer {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("HelloWorld", ConfigFactory.load("remotingGrpc.conf").getConfig("grpcSystem"))
    new GreeterServer(system).run()
  }

}

class GreeterServer(system: ActorSystem) {
  def run(): Future[Http.ServerBinding] = {
    implicit val sys: ActorSystem = system
    implicit val ec: ExecutionContext = sys.dispatcher

    val service: HttpRequest => Future[HttpResponse] =
      GreeterServiceHandler(new GreeterServiceImpl())

    val binding = Http().newServerAt("127.0.0.1", 8080).bind(service)

    binding.foreach { binding => println(s"gRPC server bound to: ${binding.localAddress}") }

    binding
  }

}