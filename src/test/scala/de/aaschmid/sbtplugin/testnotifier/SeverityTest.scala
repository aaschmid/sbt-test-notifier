package de.aaschmid.sbtplugin.testnotifier

import org.specs.Specification
import org.specs.util.DataTables


object SeverityTest extends Specification with DataTables {
  import de.aaschmid.sbtplugin.testnotifier.{Error => SError}

  def provide = addToSusVerb("provide")


  "Sealed case class Severity" should provide {
    "isWorseThan" in {
        "s0"  |   "s1"  | "result" |>
       Passed.asInstanceOf[Severity] !
                Passed.asInstanceOf[Severity] !
                            false  |
       Passed ! Skipped !   false  |
       Passed !  Failed !   false  |
       Passed !  SError !   false  |
      Skipped !  Passed !    true  |
      Skipped !  Failed !   false  |
       Failed !  Passed !    true  |
      Skipped !  SError !   false  |
       SError !  Failed !    true  | { (s0, s1, result) =>
        (s0 isWorseThan s1) must_== result
      }
    }
    "toString which does not contain any of [$.]" in {
      Passed.toString must beMatching ("^[^$.]+$")
    }
  }

  "Companion object Severity" should provide {
    "correct worst" in {
      "list" | "result" |>
      List[Severity](Passed)                ! Passed.asInstanceOf[Severity] |
      List(Passed, Passed)                  ! Passed                        |
      List(Passed, Skipped, Passed)         ! Skipped                       |
      List(Failed, Passed, Skipped)         ! Failed                        |
      List(Passed, Skipped, Failed, SError) ! SError                        | { (list, result) =>
        Severity.worst(list) must_== result
      }
    }
    "exception on passed empty List" in {
      Severity.worst(Nil) must throwA[UnsupportedOperationException]
    }
  }
}
