
import scalapb.compiler.Version.scalapbVersion

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.12"

import scalapb.compiler.Version.grpcJavaVersion

lazy val akkaVersion = "2.6.19"

val googleCommonProtobuf = "com.thesamet.scalapb.common-protos" %%
  "proto-google-common-protos-scalapb_0.11" % "2.5.0-2" % "protobuf"

val googleCommonProtos = "com.thesamet.scalapb.common-protos" %%
  "proto-google-common-protos-scalapb_0.11" % "2.5.0-2"

val grpcNetty = "io.grpc" % "grpc-netty" % grpcJavaVersion
val scalaPbRuntime = "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapbVersion

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  googleCommonProtobuf,
  googleCommonProtos,
  grpcNetty
)

lazy val root = (project in file("."))
  .settings(
    name := "testproject"
  ).enablePlugins(AkkaGrpcPlugin)