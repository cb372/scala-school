import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object scalaschool {

  class Impl(val c: Context) {
    import c.universe._

    /**
     * World's smallest macro - just returns the expression it's given
     */
    def doNothing[A: c.WeakTypeTag](a: c.Expr[A]) = {
      println("yo I'm in ur compilerz")
      a
    }

    /**
     * Macros can also deal with trees instead of expressions
     */
    def takesATree(a: c.Tree) = {
      a
    }

    def throwsCompileError = {
      c.abort(c.enclosingPosition, "Uh oh!")
    }

  }

  object Macros {
    def doNothing[A](a: A): A = macro Impl.doNothing[A]
    def takesATree[A](a: A): A = macro Impl.takesATree
    def failToCompile = macro Impl.throwsCompileError
  }

}
