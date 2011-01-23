package de.aaschmid.sbtplugin.testnotifier

import sbt.{Result, TestEvent, TestsListener}


class NotifyingTestsListener(settings: TestNotifySettings) extends TestsListener {
  import scala.collection.mutable.ListBuffer


  private[testnotifier] val testResults = new ListBuffer[TestResult]()
  private var startMillies: Long = 0


  override def doInit() {
    testResults.clear()
    startMillies = System.currentTimeMillis
  }

  override def startGroup(name: String) = ()

  override def testEvent(event: TestEvent) {
    event.detail foreach {
      testResults += TestResult(_)
    }
  }

  override def endGroup(name: String, t: Throwable) = ()

  override def endGroup(name: String, result: Result.Value) = ()

  override def doComplete(result: Result.Value) {
    import sbt.Process._

    val duration = System.currentTimeMillis - startMillies
    val worst = Severity.worst(testResults.toList map { _.severity })

    val summary = settings.summary(worst)
    val body = settings.body(duration, testResults.toList)

    if (settings.printNotification) {
      println(summary + "\n" + body)
    }

    settings.notificationCommand :: settings.notificationParams(worst, summary, body) !
  }
}
