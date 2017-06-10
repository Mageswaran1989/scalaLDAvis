import Build._

name := "scalaLDAVis"
version := "0.1"

unmanagedSourceDirectories in Compile += baseDirectory.value / "examples" / "src" / "scala"
unmanagedResources in Compile += baseDirectory.value / "resources"
unmanagedResources in Test += baseDirectory.value / "resources"
unmanagedResources in Runtime += baseDirectory.value / "resources"


lazy val localRoot = Project(id = "scalaLDAVis", base = file("."))
                  .settings(BuildSettings.clusterSettings)

lazy val clusterRoot = Project(id = "scalaLDAVis-local", base = file("."))
  .settings(BuildSettings.intelliJSettings)

