import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "couchplay"
    val appVersion      = "0.1-SNAPSHOT"

    val appDependencies = Seq(
      "couchbase" % "couchbase-client" % "1.1-dp3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      resolvers += "Couchbase Maven Repository" at "http://files.couchbase.com/maven2"     
    )

}
