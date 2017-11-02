resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.url(
  "bintray-kipsigman-sbt-plugins",
  url("http://dl.bintray.com/kipsigman/sbt-plugins")
)(Resolver.ivyStylePatterns)

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC1")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.6")

addSbtPlugin("kipsigman" % "sbt-elastic-beanstalk" % "0.1.4")

// Formatting and style checking
addSbtPlugin("com.geirsson"   % "sbt-scalafmt"           % "0.6.8")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")
