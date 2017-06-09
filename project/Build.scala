import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin.autoImport._
import sbtassembly.PathList

object Dependencies {
  val sparkVersion = "2.1.0"

  val log = "org.slf4j" % "slf4j-log4j12" % "1.7.10"
  val config = "com.typesafe" % "config" % "1.2.1"

  val scalaz =  "org.scalaz" %% "scalaz-core" % "7.2.13"
  val shapeless =  "com.chuusai" %% "shapeless" % "2.3.2"
  val scalatest = "org.scalatest" % "scalatest_2.11" % "3.0.1" % "test"

  val spray = "io.spray" %%  "spray-json" % "1.3.3"

  val includeME = Seq(log, config, scalaz, shapeless, scalatest, spray)
  val spark = Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,
    "org.apache.spark" %% "spark-mllib" % sparkVersion)

  val sparkProvided = spark.map(x => x % "provided")
}

object BuildSettings {
  val buildOrganization = "Imaginea"
  val buildVersion = "0.1"
  val buildScalaVersion = "2.11.8"

  val commonBuildSettings = Defaults.coreDefaultSettings ++ Seq(
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    resolvers += Resolver.jcenterRepo,
    resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases",
    assemblyMergeStrategy in assembly := {
      case PathList(xs@_*) if xs.last == "UnusedStubClass.class" => MergeStrategy.first
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )

  val intelliJSettings = commonBuildSettings ++ Seq(
    libraryDependencies ++= Dependencies.spark ++ Dependencies.includeME,
    target := baseDirectory.value / "target-local")

  val clusterSettings = commonBuildSettings ++ Seq(libraryDependencies ++= Dependencies.sparkProvided ++ Dependencies.includeME)
}