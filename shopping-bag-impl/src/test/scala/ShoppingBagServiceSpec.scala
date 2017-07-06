import akka.NotUsed
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import com.xebia.webshop.shoppingbag.api.{AddItemToShoppingBagRequest, ShoppingBagService, ShoppingBag}
import com.xebia.webshop.shoppingbag.impl.ShoppingBagApplication
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

import scala.concurrent.Future

class ShoppingBagServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {
  lazy val service = ServiceTest.startServer(ServiceTest.defaultSetup.withCassandra(true)) { ctx =>
    new ShoppingBagApplication(ctx) with LocalServiceLocator {}
  }

  override protected def beforeAll() = service

  override protected def afterAll() = service.stop()

  val client = service.serviceClient.implement[ShoppingBagService]

  "The Shopping Bag service" should {
    "put a item in the shopping bag and retrieve the bag" in {
      client.addItemToBag("BagA").invoke(AddItemToShoppingBagRequest("SKUA", 4)).map { response =>
        response should === (ShoppingBag(Map("SKUA" -> 4)))
      }
    }

    "put multiple items in the shopping bag and retrieve the bag correctly" in {
      val bagId = "BAGB"
      for {
        _ <- client.addItemToBag(bagId).invoke(AddItemToShoppingBagRequest("A", 1))
        _ <- client.addItemToBag(bagId).invoke(AddItemToShoppingBagRequest("A", 2))
        _ <- client.addItemToBag(bagId).invoke(AddItemToShoppingBagRequest("B", 8))
        _ <- client.addItemToBag(bagId).invoke(AddItemToShoppingBagRequest("C", 3))
        currentState <- client.retrieveShoppingBag(bagId).invoke()
      } yield {
        currentState should === (ShoppingBag(Map("A" -> 3, "B" -> 8, "C" -> 3)))
      }
    }

    "Send back an empty shopping bag when no items have been added yet" in {
      client.retrieveShoppingBag("BAGC").invoke().map { x =>
        x should ===(ShoppingBag(Map.empty))
      }
    }
  }
}
