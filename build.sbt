organization in ThisBuild := "com.xebia"

scalaVersion in ThisBuild := "2.11.8"

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"
val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "3.3"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"

lazy val root = (project in file("."))
  .settings(name := "webshop-scala")
  .aggregate(
    shoppingBagApi, shoppingBagImpl,
    orderApi, orderImpl
  )

lazy val shoppingBagApi = (project in file("shopping-bag-api"))
  .settings(version := "1.0-SNAPSHOT")
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  )

lazy val shoppingBagImpl = (project in file("shopping-bag-impl"))
  .settings(version := "1.0-SNAPSHOT")
  .dependsOn(shoppingBagApi)
  .enablePlugins(LagomScala)
  .settings(lagomForkedTestSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      macwire,
      lagomScaladslKafkaBroker,
      lagomScaladslApi,
      lagomScaladslPersistenceCassandra,
      scalaTest,
      lagomScaladslTestKit)
  )

lazy val orderApi = (project in file("order-api"))
  .settings(version := "1.0-SNAPSHOT")
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  )

lazy val orderImpl = (project in file("order-impl"))
  .settings(version := "1.0-SNAPSHOT")
  .dependsOn(shoppingBagApi, orderApi)
  .enablePlugins(LagomScala)
  .settings(lagomForkedTestSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      macwire,
      lagomScaladslKafkaBroker,
      lagomScaladslApi,
      lagomScaladslPersistenceCassandra,
      scalaTest,
      lagomScaladslTestKit)
  )

lagomCassandraCleanOnStart in ThisBuild := true