package de.aaschmid.sbtplugin.testnotifier

import org.scalatools.testing.Event


case class TestResult(event: Event) {
  val testName: String = event.testName
  val description: String = event.description
  val severity: Severity = Severity(event.result)
  val exception: Option[Throwable] = if (event.error == null) None else Some(event.error)

  override def toString: String = {
    testName.split("""\.""").last + // only pure name of Class
      //    (if (description.isEmpty) "" else " (" + description + ")") +
      (exception map { " -> " + _.getMessage }).getOrElse("")
  }
}
