package de.aaschmid.sbtplugin.testnotifier


/**
 * This trait contains all the possible settings for customizing the notification which is shown after all tests run.
 * You can even customize the function which is called for showing the notification and the specification of its
 * parameters such that you can use this Plugin also with different operating systems.
 */
trait TestNotifySettings {

  import java.io.File

  /** @return millies in milliseconds */
  def notificationTime: Int = 5000

  /** Determines if the notification {@code summary + "\n" + body} will be printed to console */
  def printNotification: Boolean = true

  private[this] val urgencyList = Map(
    Passed -> "low",
    Skipped -> "normal",
    Failed -> "normal",
    Error -> "critical")

  /** @return the target directory where the resource icons within the package are copied to */
  def imageTargetDir: File =
    new File(System.getProperty("java.io.tmpdir") + File.separator + "sbt-test-notify-icons")

  /**
   * @param resourcePath the absolute resource path within the packaged jar file
   * @return the image file that is copied from the resource within the jar to a directory outside the packaged jar
   */
  private[testnotifier] def getOrCreateFile(resourcePath: String): File = {
    import java.io.{FileOutputStream, InputStream, OutputStream}
    import sbt.{FileUtilities, ConsoleLogger}

    val sourceStream = getClass.getResourceAsStream(resourcePath)

    val targetDir = imageTargetDir
    if (!targetDir.exists) targetDir.mkdirs()

    val targetFile = new File(targetDir.getAbsolutePath + File.separator + (new File(resourcePath)).getName)
    if (!targetFile.exists) {
      targetFile.createNewFile()
      FileUtilities.transferAndClose(sourceStream, new FileOutputStream(targetFile), new ConsoleLogger)
    }
    targetFile.getAbsoluteFile
  }

  /** Map that contains all paths of default resource icons within the packaged jar for each possible {@link Severity} */
  private[testnotifier] val resourceIconPaths: Map[Severity,  String] = Map(
    Passed -> "/de/aaschmid/sbtplugin/testnotifier/passed.png",
    Skipped -> "/de/aaschmid/sbtplugin/testnotifier/skipped.png",
    Failed -> "/de/aaschmid/sbtplugin/testnotifier/failed.png",
    Error -> "/de/aaschmid/sbtplugin/testnotifier/error.png")

  /**
   * @return an icon paths for each possible {@link Severity}, e.g. {@link Passed}, {@link Skipped}, ...
   * @throws NoSuchElementException if no key with supplied severity does exist
   */
  def icon(s: Severity): File = getOrCreateFile(resourceIconPaths(s))

  /** @return the summary of the notification for the worst {@link Severity} */
  def summary(worst: Severity): String = "Test results"

  /** @return the body of the notification which is shown below {@link #summary(Severity)} */
  def body(duration: Long, testResults: List[TestResult]): String = {
//    val grouped = testResults groupBy { _.severity } // TODO groupBy with 2.8.x
    List("duration: " + formatDuration(duration),
      formatPassed(testResults filter { _.severity == Passed }),
      formatSkipped(testResults filter { _.severity == Skipped }),
      formatFailed(testResults filter { _.severity == Failed }),
      formatErrors(testResults filter { _.severity == Error })
    ) mkString "\n"
  }

  /**
   * Formats the duration which is given in milliseconds
   * @return the formatted duration, e.g. 45 sec(s) or 1:15 min(s)
   */
  def formatDuration(millies: Long): String = millies match {
    case m if m < 1000 => m + " msec(s)"
    case m if m < 60000 => (m / 1000) + " sec(s)"
    case m => (m / 60000) + (m % 60000 match {
      case r if (r < 1000) => ""
      case r => ":" + "%02d".format(r / 1000)
    }) + " min(s)"
  }

  /** @return the {@link String} to display for the passed tests */
  def formatPassed(tests: List[TestResult]): String =
    formatTestResults("passed", tests, false)

  /** @return the {@link String} to display for the skipped tests */
  def formatSkipped(tests: List[TestResult]): String =
    formatTestResults("skipped", tests, true)

  /** @return the {@link String} to display for the failed tests */
  def formatFailed(tests: List[TestResult]): String =
    formatTestResults("failed", tests, true)

  /** @return the {@link String} to display for the tests which have thrown an exception */
  def formatErrors(tests: List[TestResult]): String =
    formatTestResults("errors", tests, true)

  /**
   * This function formats the given test results such that {@code prefix + ":" + tests.length}. If
   * {@code showTestClasses == true && tests.length > 0} additionally the class names of the test are listed for the
   * category. Thereby each test class becomes a new line indented by a single tab.
   * @return the formatted results
   */
  def formatTestResults(prefix: String, tests: List[TestResult], showTestClasses: Boolean): String = {
    prefix + ": " + tests.length + (showTestClasses match {
      case true => (if (tests.length > 0) tests mkString ("\n\t", "\n\t", "") else "")
      case _ => ""
    })
  }

  /**
   * The Shell command which is responsible for displaying the notification. The default command 'notify-send' requires
   * 'libnotify-bin' which can be installed using "sudo apt-get install libnotify-bin"
   * @return the Shell command which is responsible for displaying the notification
   */
  def notificationCommand: String = "notify-send"

  /**
   * All parameters which are used to display the notification should be returned in their correct order in a
   * {@link List}, e.g. {@code List("-i", "/usr/share/icons/gnome/scalable/emblems/emblem-default.svg")} to show the
   * notification five seconds (using 'notify-send')
   * @return a {@link List} containing all parameters and their values for displaying the notification
   */
  def notificationParams(worst: Severity, summary: String, body: String): List[String] =
    List("-t", notificationTime.toString, "-u", urgencyList(worst), "-i", icon(worst).getAbsolutePath, summary, body)
}
