name := "Scala Slick Examples"
version := "1.0.0"
scalaVersion := "2.13.3"
libraryDependencies ++= Seq(
"com.typesafe.slick" %% "slick" % "3.3.3",
"com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
"org.slf4j" % "slf4j-nop" % "1.6.4",
"mysql" % "mysql-connector-java" % "8.0.28",
//"ch.qos.logback" % "logback-classic" % "1.2.3",
"org.apache.commons" % "commons-lang3" % "3.7",
//
// "core" module - IO, IOApp, schedulers
// This pulls in the kernel and std modules automatically.
"org.typelevel" %% "cats-effect" % "3.3.5",
// concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
"org.typelevel" %% "cats-effect-kernel" % "3.3.5",
// standard "effect" library (Queues, Console, Random etc.)
"org.typelevel" %% "cats-effect-std" % "3.3.5",
"org.typelevel" %% "cats-effect-testing-specs2" % "1.4.0" % Test,
// better monadic for compiler plugin as suggested by documentation
//compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
"org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test,
)