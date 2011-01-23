package de.aaschmid.sbtplugin.testnotifier

import sbt.{BasicScalaProject, TestReportListener}


trait TestNotify extends BasicScalaProject with TestNotifySettings {

  import java.io.File

  override def imageTargetDir: File =
    new File(super.outputPath + File.separator + super.artifactID + "-icons")

  override def summary(worst: Severity): String =
    "Test results of " + super.artifactID + ":" + version

  override def testListeners: Seq[TestReportListener] = (new NotifyingTestsListener(this)) :: super.testListeners.toList
}
