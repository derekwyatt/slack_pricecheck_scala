Comments
========

* You have `com.pricecheck` and `com.scalapricecheck` and neither of them are in directory structures that match the package.

* Derive "main" from `scala.App` instead

* You have unused `import`s.  Use the `-Ywarn-unused-import` flag on your compiler.

* There are no comments in this thing. How would you convey intent of the design without them?

* `ITAD`
  * `ITAD.apply` is weird... why not just transform `class ITAD` into `final case class ITAD`?
  * JSON reader typeclass instances are pretty optimistic; you shouldn't be constructing `JsSuccess` directly, but assessing the success or failure from the various `\` and `as` calls.
  * Those typeclass instances are in the wrong spot; they should be in the companion object.
  * Using the global `ExecutionContext` is a pretty bad idea here. You should be tailoring it to something that can handle IO issues
  * `getPlain` really blocks?  Get a better HTTP library.
  * Rather than have `getLowestPrice` construct a dinky little `Future,` it should `map` / `flatMap` on top of the `Future` that `getPlain` should be returning
  * `prices` returning `Option[List[Price]]` is weird. If there's nothing there, then one would assume that the `List` is empty. If there's an error then one would expect a `Try[List[Price]]` instead.
  * `lowestPrice` doesn't need to be there. You should have:
  ``` scala
  object Price {
    implicit val ord: Ordering[Price] = Ordering.by(_.price_new)
  }
  ```
  * That allows you to run `listOfPrices.min`.
  * Never ever have `someOption.get`.  Either don't use `Option`s (because, if you use `.get` then you're not using `Option`s) or `map` over them.

* `Bot`
  * You shouldn't be constructing the `ActorSystem` in this class. If you need one, take it as an `implicit` argument
  * `selfId` is a var? It should probably be a `def`.
  * Why does `run` not return anything?
  * `onSuccess` and `onFailure` should be replaced with `flatMap` and `recoverWith` respectively.
  * `respondWithPrice` shouldn't return `Unit` but a `Future[Unit]` at worst.
  * `f"Found lowest price of $$${price.price_new}%1.2f at ${price.shop.name} (${price.url})"`

* Client
  * Standard mutability / side-effect issue. What happens if you run `sendMessage` before `connect`?
  ``` scala
  trait ClientBuilder {
    def connect(): Client
  }

  trait Client {
    def sendMessage(target: String, message: String): Future[Any]
    def onMessage(f: (Message) => Unit): Unit
    def self: String
  }
  ```
  * `Message` trait should use `def`s instead of `val`s.

* `SlackMessage`
  * `SlackMessage` should be a `final case class` and it should have `text` and `origin` constructor parameters. If you want to construct it from a `slack.models.Message` then you should have an `apply` factory method in the companion object.  Don't complicate the cleanliness of your model with implementation annoyances.

* `SlackClient`
  * Side-effects a call to `connect` on construction?  Bad monkey.
  * You're constructing another `ActorSystem` here. Take one implicitly on construction.
  * Oh, so many `var`s!
    * If you had used the builder pattern then all of these could be passed to the `SlackClient` when it gets constructed out of `connect`.
