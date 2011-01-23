sbt-test-notifier
=================

This is a customizable plugin for [simple-build-tool](http://code.google.com/p/simple-build-tool/) 
to show a visual notification containing the test results. 


Requirements
------------

* Sbt version 0.7.x (not compatible with 0.5.x series).
* A library to display a notification, e.g. 
([libnotify-bin](https://launchpad.net/ubuntu/maverick/+package/libnotify-bin) 
for Ubuntu with Gnome)


Usage
-----

**ATTENTION:** This will not work currently but I am working on it. Instead you have to 
download the source and build it buy your own. You can use `publish-local` to publish it 
to your local repository and use it afterwards. ;)


To use this plugin you have change `<your project directory>/project/plugins/Plugins.scala`:

    import sbt._
    class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
      lazy val sbtIdeaRepository = "sbt-test-notifier-repo" at "???"
      lazy val sbtIdea = "de.aaschmid.sbtplugin" % "sbt-test-notifier" % "0.1"

      // ...
    }

Afterwards you have to `import de.aaschmid.sbtplugin.testnotifier.TestNotify` and add it 
to the project class in `<your project directory>/project/Project.scala`, e.g.:

    import sbt._
    class Project(info: ProjectInfo) extends DefaultProject(info) with TestNotify {
      // ...
    }


Customizations
--------------

There are a lot possible customizations that are described in this section. 


### Notification

The displaying of notifications is done by executing a shell command using a 
`ProcessBuilder` and its method `!`. This is highly dependent on the operating 
system such that you can configure it according to your operating system. 
Currently it just works for Ubuntu with Gnome by using 
[notify-send](http://manpages.ubuntu.com/manpages/maverick/man1/notify-send.1.html) 
from the [libnotify-bin](https://launchpad.net/ubuntu/maverick/+package/libnotify-bin) 
package. On Debian/Ubuntu this can be installed using 
[apt-get](http://manpages.ubuntu.com/manpages/maverick/en/man8/apt-get.8.html).

    sudo apt-get install libnotify-bin

If anyone does the customizations for a specific operating system with a certain 
library, I would be glad to get your code to add it to the plugin. One has to 
override 

    def notificationCommand: String

and

    def notificationParams(worst: Severity, summary: String, body: String): List[String]

Furthermore the time how long a notification is shown can be set by 
`def notificationTime: Int` using in milliseconds. But be aware that this will not work 
if you use [notify-send](http://manpages.ubuntu.com/manpages/maverick/man1/notify-send.1.html)
from [libnotify-bin](https://launchpad.net/ubuntu/maverick/+package/libnotify-bin) which is 
the default right now.


### Message

The message header/summary can be adjusted by overriding, e.g.:

    def summary(worst: Severity): String = "Test " + worst

The message body can be changed completely within 
`def body(duration: Long, testResults: List[TestResult]): String` or by 
changing the result of one of this methods: 

* `def formatDuration(millies: Long): String`
* `def formatPassed(tests: List[TestResult]): String`
* `def formatSkipped(tests: List[TestResult]): String`
* `def formatFailed(tests: List[TestResult]): String`
* `def formatErrors(tests: List[TestResult]): String`

or the more generally 
`def formatTestResults(prefix: String, tests: List[TestResult], showTestClasses: Boolean): String`.

### Icons

The default images are copied from the `jar` package to the projects `target/` 
directory on demand. The directory can e.g. set to the `/tmp/` by

    override def imageTargetDir: File =
      new File(System.getProperty("java.io.tmpdir"))

If you want to use your own icons you can just set them for every severity in method

    def icon(s: Severity): File


### Console

If you do not want to get the notification message also be printed on the console 
you can set `def printNotification: Boolean` to `false`.


Contributors
------------

* Erik Weikl
* [Andreas Schmid](https://github.com/aaschmid)

