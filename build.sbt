import Build._

name := "scalaLDAVis"
version := "0.1"

unmanagedSourceDirectories in Compile += baseDirectory.value / "examples" / "src" / "scala"
resourceDirectory in Compile := baseDirectory.value / "resources"
resourceDirectory in Test := baseDirectory.value / "resources"

lazy val localRoot = Project(id = "scalaLDAVis", base = file("."))
                  .settings(BuildSettings.clusterSettings)

lazy val clusterRoot = Project(id = "scalaLDAVis-local", base = file("."))
  .settings(BuildSettings.intelliJSettings)

