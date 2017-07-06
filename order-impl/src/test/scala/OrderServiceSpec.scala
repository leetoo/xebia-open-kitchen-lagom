import akka.stream.testkit.scaladsl.TestSink
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import com.xebia.webshop.order.api.{OrderEvent, OrderService}
import com.xebia.webshop.order.impl.OrderApplication
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

class OrderServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {
  lazy val service = ServiceTest.startServer(ServiceTest.defaultSetup.withCassandra(true)) { ctx =>
    new OrderApplication(ctx) with LocalServiceLocator {}
  }

  override protected def beforeAll() = service

  override protected def afterAll() = service.stop()

  val client = service.serviceClient.implement[OrderService]

  "The Order Bag service" should {
    "create an order when a set of items and amounts is given" in {
      ???
    }

    "return an order on basis of ID" in {
      ???
    }

    "distribute an order event when an order is created and when it is confirmed" in {
      val source = client.orderEvents.subscribe.atMostOnceSource
      source.runWith(TestSink.probe[OrderEvent])
        .request(1)
        .expectNext should ===(OrderEvent.OrderCreated)
    }
  }
}