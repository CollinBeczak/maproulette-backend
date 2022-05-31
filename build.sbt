import java.io.{BufferedWriter, FileWriter}

import scala.io.Source

name := "MapRouletteAPI"

version := "4.0.0"

scalaVersion := "2.13.8"

packageName in Universal := "MapRouletteAPI"

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

compileScalastyle := scalastyle.in(Compile).toTask("").value
(scalastyleConfig in Compile) := baseDirectory.value / "conf/scalastyle-config.xml"

lazy val `MapRouletteV2` = (project in file(".")).enablePlugins(PlayScala, SbtWeb, SwaggerPlugin)

swaggerDomainNameSpaces := Seq(
  "org.maproulette.framework.model",
  "org.maproulette.models",
  "org.maproulette.exception",
  "org.maproulette.session",
  "org.maproulette.actions",
  "org.maproulette.data"
)

swaggerOutputTransformers := Seq(envOutputTransformer)

swaggerRoutesFile := "generated.routes"

pipelineStages := Seq(gzip)

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  jdbc,
  jdbc % Test,
  ehcache,
  ws,
  evolutions,
  specs2 % Test,
  filters,
  guice,
  "org.sangria-graphql"     %% "sangria-play-json"  % "2.0.2",
  "org.sangria-graphql"     %% "sangria"            % "3.0.0",
  "com.typesafe.play"       %% "play-json-joda"     % "2.9.2",
  "com.typesafe.play"       %% "play-json"          % "2.9.2",
  "org.scalatestplus.play"  %% "scalatestplus-play" % "5.1.0" % Test,

  // NOTE: There is a breaking change with swagger-ui starting at v4.1.3 where the 'url'
  //       parameter is disabled for security reasons.
  //       See https://github.com/swagger-api/swagger-ui/issues/4872
  "org.webjars"             % "swagger-ui"          % "4.1.2",

  "org.playframework.anorm" %% "anorm"              % "2.6.10",
  "org.playframework.anorm" %% "anorm-postgres"     % "2.6.10",
  "org.postgresql"          % "postgresql"          % "42.3.4",
  "net.postgis"             % "postgis-jdbc"        % "2021.1.0",
  "joda-time"               % "joda-time"           % "2.10.14",
  "com.vividsolutions"      % "jts"                 % "1.13",
  "org.wololo"              % "jts2geojson"         % "0.14.3",
  "org.apache.commons"      % "commons-lang3"       % "3.12.0",
  "commons-codec"           % "commons-codec"       % "1.14",
  "com.typesafe.play"       %% "play-mailer"        % "8.0.1",
  "com.typesafe.play"       %% "play-mailer-guice"  % "8.0.1",
  "com.typesafe.akka"       %% "akka-cluster-tools" % "2.6.19",
  "com.typesafe.akka"       %% "akka-cluster-typed" % "2.6.19",
  "com.typesafe.akka"       %% "akka-slf4j"         % "2.6.19",
  "net.debasishg"           %% "redisclient"        % "3.42"
)

// Override play-json to 2.9.2 because org.sangria-graphql:sangria-play-json_2.13:2.0.2
// is pulling in play-json:2.10.0-RC5 and a release candidate is not desired.
dependencyOverrides += "com.typesafe.play" %% "play-json" % "2.9.2"

resolvers ++= Seq(
  Resolver.bintrayRepo("scalaz", "releases"),
  "Atlassian Releases" at "https://maven.atlassian.com/public/",
  Resolver.sonatypeRepo("snapshots")
)

scalacOptions ++= Seq(
  // Show warning feature details in the console
  "-feature",
  // Enable routes file splitting
  "-language:reflectiveCalls"
)

javaOptions in Test ++= Option(System.getProperty("config.file")).map("-Dconfig.file=" + _).toSeq

javaOptions in Compile ++= Seq(
  "-Xmx2G",
  // Increase stack size for compilation
  "-Xss32M"
)

lazy val generateRoutesFile = taskKey[Unit]("Builds the API V2 Routes File")
generateRoutesFile := {
  val generatedFile = baseDirectory.value / "conf/generated.routes"
  if (!generatedFile.exists()) {
    generatedFile.createNewFile()

    // The apiv2.routes file should always be last as it contains the catch all routes
    val routeFiles = Seq(
      "challenge.api",
      "changes.api",
      "comment.api",
      "data.api",
      "keyword.api",
      "notification.api",
      "project.api",
      "review.api",
      "snapshot.api",
      "task.api",
      "user.api",
      "virtualchallenge.api",
      "virtualproject.api",
      "bundle.api",
      "team.api",
      "follow.api",
      "leaderboard.api",
      "v2.api"
    )
    println(s"Generating Routes File from ${routeFiles.mkString(",")}")
    val writer = new BufferedWriter(new FileWriter(generatedFile))
    routeFiles.foreach(file => {
      val currentFile = Source.fromFile(baseDirectory.value / "conf/v2_route" / file).getLines()
      for (line <- currentFile) {
        writer.write(s"$line\n")
      }
    })
    writer.close()
  }
}

(compile in Compile) := ((compile in Compile) dependsOn generateRoutesFile).value

lazy val deleteRoutesFile = taskKey[Unit]("Deletes the generated Routes File")
deleteRoutesFile := {
  val generatedFile = baseDirectory.value / "conf/generated.routes"
  if (generatedFile.exists()) {
    println(s"Deleting the Routes file ${generatedFile.getAbsolutePath}")
    generatedFile.delete()
  }
}

cleanFiles := (baseDirectory.value / "conf" / "generated.routes") +: cleanFiles.value

lazy val regenerateRoutesFile = taskKey[Unit]("Regenerates the routes file")
regenerateRoutesFile := Def.sequential(deleteRoutesFile, generateRoutesFile).value
