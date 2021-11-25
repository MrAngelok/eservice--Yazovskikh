name := "eservice-polls"

version := "1.0"

scalaVersion := "2.13.2"

val scalikejdbcV = "3.4.2"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3" exclude("org.slf4j", "slf4j-api"),
  "com.zaxxer" % "HikariCP" % "3.4.5",
  "org.scalatest" % "scalatest_2.13" % "3.1.2" % "test",
  "org.json4s" % "json4s-jackson_2.13" % "3.6.8",
  "org.scalikejdbc"   %%  "scalikejdbc"         % scalikejdbcV exclude("commons-logging", "commons-logging"),
  "org.scalikejdbc"   %%  "scalikejdbc-config"  % scalikejdbcV exclude("commons-logging", "commons-logging"),
  "oracle" % "oracle-jdbc-connector" % "10.2.0.5.0",
  "com.zaxxer" % "HikariCP" % "3.4.5" % "test", // ???
  "com.sparkjava" % "spark-core" % "2.7.2",
  "org.pac4j" % "spark-pac4j" % "3.0.0",
  "org.pac4j" % "pac4j-oauth" % "3.2.0"
//  "com.ning" % "async-http-client" % "1.9.40"
)