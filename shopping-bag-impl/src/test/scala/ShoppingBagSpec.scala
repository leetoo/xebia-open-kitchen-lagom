import akka.Done
import akka.actor.ActorSystem
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.xebia.webshop.shoppingbag.impl._
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class ShoppingBagSpec extends WordSpecLike with Matchers with BeforeAndAfterAll with TypeCheckedTripleEquals {
  val system = ActorSystem("ShoppingBagSpec", JsonSerializerRegistry.actorSystemSetupFor(ShoppingBagSerializerRegistry))

  override protected def afterAll() {
    Await.ready(system.terminate(), 20.seconds)
  }

  "A ShoppingBag" must {
    "Add an item to its contents" in {
      val driver = new PersistentEntityTestDriver(system, new ShoppingBagEntity, "Basket1")
      val addItemOutcome = driver.run(PShoppingBagCommand.AddItem("A", 4))
      addItemOutcome.events should ===(List(PShoppingBagEvent.ItemAdded("A", 4)))
      addItemOutcome.state.items should ===(Map("A" -> 4))
      addItemOutcome.replies should ===(List(Done))
      addItemOutcome.issues should ===(Nil)

      val getItemsOutcome = driver.run(PShoppingBagCommand.GetBasket)
      getItemsOutcome.issues should ===(Nil)
      getItemsOutcome.replies should ===(List(PShoppingBag(Map("A" -> 4))))

      val addAdditionalItemsOutcome = driver.run(PShoppingBagCommand.AddItem("B", 3), PShoppingBagCommand.AddItem("A", 5))
      addAdditionalItemsOutcome.events should ===(List(PShoppingBagEvent.ItemAdded("B", 3), PShoppingBagEvent.ItemAdded("A", 5)))
      addAdditionalItemsOutcome.state.items should ===(Map("A" -> 9, "B" -> 3))
      addAdditionalItemsOutcome.replies should ===(List(Done, Done))
      addAdditionalItemsOutcome.issues should ===(Nil)
    }

    "Remove an existing item from its contents" in {
      val driver = new PersistentEntityTestDriver(system, new ShoppingBagEntity, "Basket1")
      val addItemOutcome = driver.run(PShoppingBagCommand.AddItem("A", 4), PShoppingBagCommand.RemoveItem("A", 2))
      addItemOutcome.events should ===(List(PShoppingBagEvent.ItemAdded("A", 4), PShoppingBagEvent.ItemRemoved("A", 2)))
      addItemOutcome.state.items should ===(Map("A" -> 2))
      addItemOutcome.replies should ===(List(Done))
      addItemOutcome.issues should ===(Nil)

      val getItemsOutcome = driver.run(PShoppingBagCommand.GetBasket)
      getItemsOutcome.issues should ===(Nil)
      getItemsOutcome.replies should ===(List(PShoppingBag(Map("A" -> 2))))
    }

    "Remove an non-existing item from its contents" in {
      val driver = new PersistentEntityTestDriver(system, new ShoppingBagEntity, "Basket1")
      val addItemOutcome = driver.run(PShoppingBagCommand.AddItem("A", 4), PShoppingBagCommand.RemoveItem("B", 2))
      ???
    }
  }
}
