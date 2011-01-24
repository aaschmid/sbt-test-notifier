import sbt._


class Plugins(info: ProjectInfo) extends PluginDefinition(info) {

  lazy val eclipse = "de.element34" % "sbt-eclipsify" % "0.7.0"

  lazy val sbtIdeaRepository = "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
  lazy val sbtIdea = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.2.0"

  lazy val sbtTestNotifier = "de.aaschmid.sbtplugin" % "sbt-test-notifier" % "0.1" //withSources()
}
