package de.aaschmid.sbtplugin.testnotifier


/**
 * Own severity more freedom, especially for the ordering.
 * Hint: {@link sbt.Result} does not contain a value for skipped and enum {@link org.scalatools.testing.Result} has not
 * the correct ordering.
 */
sealed case class Severity(private val priority: Int) {
  def isWorseThan(that: Severity): Boolean = this.priority > that.priority
  override def toString: String = getClass.getSimpleName.replaceAll("""^(.*)\$.*$""", "$1") // get rid of subsequent $
}


object Passed extends Severity(0)
object Skipped extends Severity(1)
object Failed extends Severity(2)
object Error extends Severity(3)


object Severity {
  import org.scalatools.testing.Result

  def apply(result: Result): Severity = result match {
    case Result.Success => Passed
    case Result.Failure => Failed
    case Result.Skipped => Skipped
    case Result.Error => Error
  }

  def worst(list: List[Severity]): Severity =  // TODO list sort { Ordering.by ... } in 2.8.x
    list reduceLeft { (x, y) => if (x isWorseThan y) x else y }
}
