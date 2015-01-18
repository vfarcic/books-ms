name := "books-service"

version := "1.0"

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies ++= {
  val akkaV = "2.3.0"
  val sprayV = "1.3.1"
  val specs2V = "2.3.12"
  Seq(
    "io.spray"                  %   "spray-can"       % sprayV,
    "io.spray"                  %   "spray-routing"   % sprayV,
    "io.spray"                  %%  "spray-json"      % "1.2.6",
//    "io.spray"                  %   "spray-caching"   % sprayV,
    "io.spray"                  %   "spray-testkit"   % sprayV    % "test",
    "com.typesafe.akka"         %%  "akka-actor"      % akkaV,
//    "com.typesafe.akka"         %%  "akka-testkit"    % akkaV     % "test",
    "org.specs2"                %%  "specs2-core"     % specs2V   % "test",
//    "org.specs2"                %%  "specs2-mock"     % specs2V   % "test",
    "org.mongodb"               %%  "casbah"          % "2.7.2",
    "com.novus"                 %%  "salat"           % "1.9.8",
//    "com.github.nscala-time"    %%  "nscala-time"     % "1.2.0",
    "org.slf4j"                 %   "slf4j-api"       % "1.7.7",
    "ch.qos.logback"            %   "logback-classic" % "1.0.3"
  )
}
