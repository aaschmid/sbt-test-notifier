package de.aaschmid.sbtplugin.testnotifier

import org.specs.Specification
import org.specs.util.DataTables


object TestNotifySettingsTest extends Specification with DataTables with TestNotifySettings /*with Mockito*/ {
  import java.io.File
  import de.aaschmid.sbtplugin.testnotifier.{Error => SError}

  "Test resource should copied correctly" in {
    val target = getOrCreateFile("/de/aaschmid/sbtplugin/testnotifier/resource.txt")
    println(target)
    target must exist
    target.delete() must beTrue
  }

  "For all severities must exist one icon available as resource" in {
            "severity"            |>
    Passed.asInstanceOf[Severity] !
             Skipped              |
              Failed              |
              SError              | { (severity) =>
      val resource = getClass.getResource(resourceIconPaths(severity))
      resource must notBeNull
      (new File(resource.getPath)) must exist
    }
  }

  "Duration should be formated correctly" in {
     "duration" |      "result"   |>
            0   !     "0 msec(s)" |
          100   !   "100 msec(s)" |
         1000   !     "1 sec(s)"  |
        10000   !    "10 sec(s)"  |
        60000   !     "1 min(s)"  |
        75000   !  "1:15 min(s)"  |
      3601000   ! "60:01 min(s)"  |
      3600000   !    "60 min(s)"  | { (millies, result) =>
       formatDuration(millies) must_== result
     }
  }
}