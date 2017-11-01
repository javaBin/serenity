import com.trueaccord.scalapb.compiler.{Version => SbVersion}
import play.sbt.PlayImport
import sbt.{Resolver, _}

object Versions {
  val akka            = "2.5.6"
  val silhouette      = "4.0.0"
  val scalaVersion    = "2.11.11"
  val protoBuf        = "3.4.0"
  val playSlick       = "2.1.0"
  val postgresql      = "42.1.4"
  val sangria         = "1.3.2"
  val sangriaPlayJson = "1.0.4"
}

object Dependencies {

  val resolvers = DefaultOptions.resolvers(snapshot = true) ++ Seq(
    "Atlassian Releases" at "https://maven.atlassian.com/public/",
    Resolver.jcenterRepo
  )

  private val akka = Seq(
    "com.typesafe.akka" %% "akka-actor" % Versions.akka
  )

  private val persistence = akka ++ Seq(
    "com.typesafe.akka"         %% "akka-persistence"                    % Versions.akka,
    "com.typesafe.akka"         %% "akka-persistence-query" % Versions.akka,
    "com.typesafe.akka"         %% "akka-remote"                         % Versions.akka,
    "org.iq80.leveldb"          % "leveldb"                              % "0.9",
    "com.github.dnvriend"       %% "akka-persistence-jdbc"               % "2.4.18.2",
    "org.postgresql"            % "postgresql"                           % Versions.postgresql,
    "org.fusesource.leveldbjni" % "leveldbjni-all"                       % "1.8",
    "com.google.protobuf"       % "protobuf-java"                        % Versions.protoBuf
  )

  private val sangria = Seq(
    "org.sangria-graphql" %% "sangria"           % Versions.sangria,
    "org.sangria-graphql" %% "sangria-play-json" % Versions.sangriaPlayJson
  )

  private val test = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play"          % "2.0.1"       % Test,
    "com.typesafe.akka"      %% "akka-testkit"                % Versions.akka % Test,
    "com.github.dnvriend"    %% "akka-persistence-inmemory"   % "1.3.18"      % Test,
    "org.scalamock"          %% "scalamock-scalatest-support" % "3.6.0"       % Test
  )

  private val silhouette = Seq(
    "com.mohiva" %% "play-silhouette"                 % Versions.silhouette,
    "com.mohiva" %% "play-silhouette-password-bcrypt" % Versions.silhouette,
    "com.mohiva" %% "play-silhouette-persistence"     % Versions.silhouette,
    "com.mohiva" %% "play-silhouette-crypto-jca"      % Versions.silhouette,
    "com.mohiva" %% "play-silhouette-testkit"         % Versions.silhouette % "test"
  )

  private val root = Seq(
    "com.iheart"        %% "ficus"                 % "1.4.3",
    "net.codingwell"    %% "scala-guice"           % "4.1.0",
    "org.postgresql"    % "postgresql"             % Versions.postgresql,
    "com.typesafe.play" %% "play-slick"            % Versions.playSlick,
    "com.typesafe.play" %% "play-slick-evolutions" % Versions.playSlick,
    PlayImport.evolutions
  )

  val playDependencies = akka ++ persistence ++ test ++ silhouette ++ sangria ++ root

  val protobufDependencies = Seq(
    "com.trueaccord.scalapb" %% "scalapb-runtime" % SbVersion.scalapbVersion % "protobuf"
  )
}
