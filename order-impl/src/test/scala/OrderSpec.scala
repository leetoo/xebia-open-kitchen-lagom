import akka.Done
import akka.actor.ActorSystem
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.xebia.webshop.order.impl.POrder.{POrderLine, PSKU}
import com.xebia.webshop.order.impl._
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class OrderSpec extends WordSpecLike with Matchers with BeforeAndAfterAll with TypeCheckedTripleEquals {
  val system = ActorSystem("OrderEntitySpec", JsonSerializerRegistry.actorSystemSetupFor(OrderSerializerRegistry))

  override protected def afterAll() {
    Await.ready(system.terminate(), 20.seconds)
  }

  "A Order" must {
    "must react correctly on incoming commands" in {
      val driver = new PersistentEntityTestDriver(system, new OrderEntity, "OrderA")
      val createOrderOutcome = driver.run(POrderCommand.CreateOrder(Seq(POrderLine(PSKU("A"), 5), POrderLine(PSKU("B"), 2))))
      createOrderOutcome.events should ===(List(POrderEvent.OrderCreated(Seq(POrderLine(PSKU("A"), 5), POrderLine(PSKU("B"), 2)))))
      createOrderOutcome.state.orderLines should ===(Seq(POrderLine(PSKU("A"), 5), POrderLine(PSKU("B"), 2)))
      createOrderOutcome.replies should ===(List(Done))
      createOrderOutcome.issues should ===(Nil)

      val addShippingInfoOutcome = driver.run(POrderCommand.AddShippingInformation(POrder.PShippingInfo("Wibautstraat 200", "1091GS", "Amsterdam", "NL")))
      addShippingInfoOutcome.events should ===(List(POrderEvent.ShippingInformationAdded(POrder.PShippingInfo("Wibautstraat 200", "1091GS", "Amsterdam", "NL"))))
      addShippingInfoOutcome.state.shippingInfo should ===(Some(POrder.PShippingInfo("Wibautstraat 200", "1091GS", "Amsterdam", "NL")))
      addShippingInfoOutcome.replies should ===(List(Done))
      addShippingInfoOutcome.issues should ===(Nil)

      val addPaymentOutcome = driver.run(POrderCommand.AddPayment("JDH3746"))
      addPaymentOutcome.events should ===(List(POrderEvent.OrderPaid("JDH3746"), POrderEvent.OrderConfirmed))
      addPaymentOutcome.state.paymentReference should ===(Some("JDH3746"))
      addPaymentOutcome.replies should ===(List(Done))
      addPaymentOutcome.issues should ===(Nil)
    }
  }
}

