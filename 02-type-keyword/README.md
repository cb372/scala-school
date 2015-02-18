# The `type` keyword

## Type aliases

In Scala you can define aliases for types, e.g.

```
type UserId = Int
type UsersById = Map[UserId, User]
type Aggregate = (Foo, Bar, Baz, Hoge) => Result
```

Aliasing a complex and commonly used type can make your code more readable, e.g.

```
def buildAndAggregateStuff(aggregate: Aggregate): Result
```

rather than

```
def buildAndAggregateStuff(aggregate: (Foo, Bar, Baz, Hoge) => Result): Result
```

Even if the types aren't complex, I find it sometimes makes code more readable if you sprinkle in some domain-specific aliases:

```
type UserId = Int
type ArticleId = Int

def hasUserViewedArticle(user: UserId, article: ArticleId): Boolean
```

### Not type-safe

It should be noted that this type aliasing doesn't give you any type-safety. `UserId` and `ArticleId` are both simply aliases for Int, so you can use either of them anywhere that you need an Int. For example:

```
val user: UserId = 123
val article: ArticleId = 456
hasUserViewedArticle(article, user) // Note: parameters are wrong way round
```

the above code will compile and run, even though we accidentally switched the user and article parameters, because they were both Ints.

If you want a more safe way to do this, you have a few options:

* Always use named parameters. In this case the compiler won't give you any help, but at least somebody reviewing your code will probably spot the typo.

    ```
    hasUserViewedArticle(user = user, article = article)
    ```

* Use case classes to model your domain. A downside of this is that you will need to add custom serialization code if you don't want to end up with weird-looking JSON.

    ```
    case class UserId(value: Int)
    case class ArticleId(value: Int)
    hasUserViewedArticle(UserId(123), ArticleId(456))
    ```

* Use tagged types (available with Scalaz or Shapeless). This gives you type-safety without the case class baggage.

    ``` 
    trait UserId
    trait ArticleId
    
    import shapeless._, tag._
    val user = tag[UserId](123)
    val article = tag[ArticleId](456)

    def hasUserViewedArticle(user: Int @@ UserId, article: Int @@ ArticleId): Boolean = {
      true
    }

    hasUserViewedArticle(user, article) // OK
    hasUserViewedArticle(article, user) // Compile error
    ``` 

## Abstract member types

TODO animal problem example

## Path dependent types

TODO

## Hands on

TODO

## Further reading

* [StackOverflow answer](http://stackoverflow.com/a/1154727/110856) about abstract member types vs generics
* Blog post: [Typelevel hackery tricks in Scala](http://www.folone.info/blog/Typelevel-Hackery/)
