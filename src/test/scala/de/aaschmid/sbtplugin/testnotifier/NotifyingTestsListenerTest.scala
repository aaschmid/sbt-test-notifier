package de.aaschmid.sbtplugin.testnotifier

import sbt.{Result, TestEvent, TestsListener}
import org.specs.Specification
import org.specs.mock.Mockito


object NotifyingTestsListenerTest extends Specification /*with Mockito*/ { // TODO use this Mockito but what does not work with it?

  import org.mockito.Mockito._
  import org.mockito.Matchers._
  import org.scalatools.testing.{Event, Result}


  private[this] def getNotifyingTestsListener: NotifyingTestsListener = {
    val settings = mock(classOf[TestNotifySettings])
    when(settings.notificationCommand) thenReturn ("echo")
    when(settings.notificationParams(any[Severity], anyString, anyString)) thenReturn ("sbt-test-notifier" :: Nil)

    new NotifyingTestsListener(settings)
  }


  private[this] def events(results: Result*): TestEvent = {
    def details(results: Result*): Seq[Event] = results map { r =>
      new Event {
        override def testName = "test"
        override def description = ""
        override def result = r
        override def error = null
      }
    }
    val event = mock(classOf[TestEvent])
    when(event.detail) thenReturn details(results: _*)
    event
  }


  "the results of executed test" should {
    "be collected correctly for passed case" in {
      val sut = getNotifyingTestsListener
      sut.doInit()
      sut.testEvent(events(Result.Success))
      sut.doComplete(sbt.Result.Passed)

      sut.testResults.size must_== 1
    }
    "be collected correctly for failed case" in {
      val sut = getNotifyingTestsListener
      sut.testEvent(events(Result.Failure, Result.Failure))
      sut.doComplete(sbt.Result.Failed)

      sut.testResults.size must_== 2
    }
    "be collected correctly for mixed results" in {
      import de.aaschmid.sbtplugin.testnotifier.{Error => SError}
      val sut = getNotifyingTestsListener
      sut.testEvent(events(Result.Success, Result.Failure, Result.Skipped, Result.Failure, Result.Success))
      sut.doComplete(null)

      sut.testResults.size must_== 5
      sut.testResults.toList.filter(_.severity == Passed).size must_== 2
      sut.testResults.toList.filter(_.severity == Skipped).size must_== 1
      sut.testResults.toList.filter(_.severity == Failed).size must_== 2
      sut.testResults.toList.filter(_.severity == SError).size must_== 0
    }
    "be empty after calling of doInit" in {
      val sut = getNotifyingTestsListener
      sut.testEvent(events(Result.Success))
      sut.doComplete(sbt.Result.Passed)
      sut.testResults.size must_== 1
      sut.doInit()
      sut.testResults.size must_== 0
    }
  }
}
