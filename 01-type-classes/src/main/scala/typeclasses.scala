object typeclasses extends App {

  trait Compat[A] { 
    /** Checks whether `a1` and `a2` are compatible with each other */
    def isCompatible(a1: A, a2: A): Boolean
  }

  def findCompatiblePairs[A](pairs: Seq[(A, A)])(implicit evidence: Compat[A]): Seq[(A, A)] = {
    pairs.filter { case (a1, a2) => evidence.isCompatible(a1, a2) }
  }

  def findCompatiblePairsWithContextBound[A : Compat](pairs: Seq[(A, A)]): Seq[(A, A)] = {
    pairs.filter { case (a1, a2) => implicitly[Compat[A]].isCompatible(a1, a2) }
  }

  sealed trait Widget
  case object RoundPeg extends Widget
  case object RoundHole extends Widget
  case object SquarePeg extends Widget
  case object SquareHole extends Widget

  val pairs: Seq[(Widget, Widget)] = Seq(RoundPeg -> RoundHole, SquarePeg -> RoundHole)

  implicit val widgetCompat = new Compat[Widget] {
    def isCompatible(w1: Widget, w2: Widget) = (w1, w2) match {
      case (RoundPeg, RoundHole) => true
      case (RoundHole, RoundPeg) => true
      case (SquarePeg, SquareHole) => true
      case (SquareHole, SquarePeg) => true
      case _ => false
    }
  }

  println(findCompatiblePairs(pairs))
  println(findCompatiblePairsWithContextBound(pairs))

  implicit def optionCompat[A : Compat] = new Compat[Option[A]] {
    def isCompatible(opt1: Option[A], opt2: Option[A]): Boolean = (opt1, opt2) match {
      case (Some(a1), Some(a2)) => implicitly[Compat[A]].isCompatible(a1, a2)
      case _ => false
    }
  }

  val optPairs: Seq[(Option[Widget], Option[Widget])] = Seq(Some(SquarePeg) -> Some(SquareHole), Some(RoundPeg) -> None)
  println(findCompatiblePairs(optPairs))
}
