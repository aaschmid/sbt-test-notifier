import de.aaschmid.sbtplugin.testnotifier.TestNotify

import de.element34.sbteclipsify._
import sbt.{Artifact, PluginProject, ProjectInfo, Resolver}

class Project(info: ProjectInfo) extends PluginProject(info) with Eclipsify with IdeaProject with TestNotify {

  private def withScalaVersion(libName: String) = libName + "_" + buildScalaVersion


  // Settings

  override def compileOptions = Unchecked :: Deprecation :: ExplainTypes ::
    compileOptions("-encoding", "utf8", "-Xexperimental").toList


  // Repos

  val specsRepo = Resolver.url("specs-repo", new java.net.URL("http://specs.googlecode.com/svn/maven2"))


  // Dependencies

  lazy val testInterface = "org.scala-tools.testing" % "test-interface" % "0.5"

//  lazy val scalacheck = "org.scala-tools.testing" % withScalaVersion("scalacheck") % "1.6" % "test->default" withSources()
  lazy val specs = "org.scala-tools.testing" % withScalaVersion("specs") % "1.6.1" % "test->default" //withSources()
  lazy val mockito = "org.mockito" % "mockito-all" % "1.8.0" % "test->default" withSources() // TODO find correct version


  // Publish

  // TODO
//  override def packageDocsJar = defaultJarPath("-javadoc.jar")
//  override def packageSrcJar= defaultJarPath("-sources.jar")
//  val sourceArtifact = Artifact.sources(artifactID)
//  val docsArtifact = Artifact.javadoc(artifactID)
//  override def packageToPublishActions = packageDocs :: packageSrc :: super.packageToPublishActions.toList

//  lazy val publishTo = Resolver.file("publish repo", new java.io.File("/home/schmida/.m2/repository"))
}
