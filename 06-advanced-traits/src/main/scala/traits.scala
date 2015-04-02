

object Traits {

  trait DodgyTrait {
    val a: Int
    val b: Int
    val c = a + b
  }

  val x = new DodgyTrait {
    val a = 1
    val b = 2
  }

  val y = new {
    val a = 1
    val b = 2
  } with DodgyTrait

  class DodgySubclass extends { val a = 1; val b = 2 } with DodgyTrait

  trait HappyTrait {
    def a: Int
    def b: Int
    val c = a + b
  }

  val h = new HappyTrait {
    def a = 1      // will be re-evaluated every time it's called
    lazy val b = 2 // will be evaluated once
  }

  //
  // Stackable traits
  //

  trait KeyValueStore {
    def getValue(key: String): String
  }

  class Redis extends KeyValueStore {
    override def getValue(key: String) = {
      // read value from Redis...
      "hello"
    }
  }

  trait Caching extends KeyValueStore {
    val cache = new {
      def get(key: String): Option[String] = None
      def put(key:String, value: String) = {}
    }

    abstract override def getValue(key: String) = {
      cache.get(key) match {
        case Some(cachedValue) => cachedValue
        case None =>
          val valueFromKVS = super.getValue(key)
          cache.put(key, valueFromKVS)
          valueFromKVS
      }
    }
  }

  trait Logging extends KeyValueStore {
    abstract override def getValue(key: String) = {
      val value = super.getValue(key)
      println(s"Got a value from the key-value store. Key=$key, Value=$value")
      value
    }
  }

  val kvs = new Redis with Caching with Logging
  val kvs2 = new Redis with Logging with Caching
}
