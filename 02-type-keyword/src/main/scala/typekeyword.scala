object typekeyword extends App {

  type Aggregate = (Int, Int) => Int
  def aggregate(a: Int, b: Int): Int = a + b
  def buildAndAggregateStuff(aggregate: Aggregate) = {
    aggregate(1, 2)
  }
  println(buildAndAggregateStuff(aggregate))

  type UserId = Int
  type ArticleId = Int

  def hasUserViewedArticle(user: UserId, article: ArticleId): Boolean = {
    true
  }

  val user: UserId = 123
  val article: ArticleId = 345

  //println(hasUserViewedArticle(user, article))
  //println(hasUserViewedArticle(article, user))

  trait UserIdT
  trait ArticleIdT
  
  import shapeless._, tag._
  val taggedUser = tag[UserIdT](123)
  val taggedArticle = tag[ArticleIdT](456)

  def hasViewed(user: Int @@ UserIdT, article: Int @@ ArticleIdT): Boolean = {
    true
  }
  // println(hasViewed(taggedUser, taggedArticle)) // compiles
  // println(hasViewed(taggedArticle, taggedArticle)) // doesn't compile

}
