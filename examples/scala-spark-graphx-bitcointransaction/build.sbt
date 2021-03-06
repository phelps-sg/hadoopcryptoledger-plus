
import sbt._
import Keys._
import scala._


lazy val root = (project in file("."))
.settings(
    name := "example-hcl-spark-scala-graphx-bitcointransaction",
    version := "0.1"
)
 .configs( IntegrationTest )
  .settings( Defaults.itSettings : _*)
 

scalacOptions += "-target:jvm-1.8"

crossScalaVersions := Seq("2.11.10")

resolvers += Resolver.mavenLocal

fork  := true



assemblyJarName in assembly := "example-hcl-spark-scala-graphx-bitcointransaction.jar"

libraryDependencies += "com.github.zuinnote" % "hadoopcryptoledger-fileformat" % "1.2.1" % "compile"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.4" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-graphx" % "2.4.4" % "provided"

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.7.0" % "provided"

libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.0.1" % "it"


libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.7.0" % "it" classifier "" classifier "tests"

libraryDependencies += "org.apache.hadoop" % "hadoop-hdfs" % "2.7.0" % "it" classifier "" classifier "tests"

libraryDependencies += "org.apache.hadoop" % "hadoop-minicluster" % "2.7.0" % "it"


libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % "test,it"
