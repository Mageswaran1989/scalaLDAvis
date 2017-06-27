import Build._

unmanagedSourceDirectories in Compile += baseDirectory.value / "examples" / "src"
unmanagedResourceDirectories in Compile += baseDirectory.value / "resources"
unmanagedResources in Compile += baseDirectory.value / "resources"
unmanagedResources in Test += baseDirectory.value / "resources"
unmanagedResources in Runtime += baseDirectory.value / "resources"


lazy val localRoot = Project(id = "scalaLDAVis", base = file("."))
                  .settings(BuildSettings.clusterSettings)

lazy val clusterRoot = Project(id = "scalaLDAVis-local", base = file("."))
  .settings(BuildSettings.intelliJSettings)

