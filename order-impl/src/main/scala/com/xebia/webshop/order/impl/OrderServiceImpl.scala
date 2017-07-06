package com.xebia.webshop.order.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.xebia.webshop.order._
import com.xebia.webshop.order.api.{ConfirmOrderRequest, CreateOrderRequest, Order, OrderService}
import sun.plugin.dom.exception.InvalidStateException

import scala.concurrent.{ExecutionContext, Future}

class OrderServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends OrderService {

  override def createOrder(orderId: String): ServiceCall[CreateOrderRequest, Order] = ???

  override def confirmOrder(orderId: String): ServiceCall[ConfirmOrderRequest, NotUsed] = ???

  override def getOrder(orderId: String): ServiceCall[NotUsed, Order] = ???

  override def orderEvents = TopicProducer.singleStreamWithOffset { offset =>
    persistentEntityRegistry.eventStream(POrderEvent.Tag, offset).filter(e =>
      /* Filter some of the internal events to isolate some of the entities internal logic */
      e.event.isInstanceOf[POrderEvent.OrderCreated] || e.event == POrderEvent.OrderConfirmed
    ).mapAsync(1) { event =>
      event.event match {
        case POrderEvent.OrderCreated(orderLines) =>
          val message = api.OrderEvent.OrderCreated(event.entityId, orderLines.map(x => api.Order.OrderLine(api.Order.SKU(x.sku.id), x.amount)))
          Future.successful((message, event.offset))
        case POrderEvent.OrderConfirmed =>
          persistentEntityRegistry.refFor[OrderEntity](event.entityId).ask(POrderCommand.GetOrder).map { x =>
            val shippingInfo = x.shippingInfo.getOrElse(throw new InvalidStateException("Shipping info is expected to be set when the order is confirmed"))
            val message = api.OrderEvent.OrderConfirmed(
              orderId = event.entityId,
              orderLines = x.orderLines.map(x => api.Order.OrderLine(api.Order.SKU(x.sku.id), x.amount)),
              shippingInfo = api.Order.ShippingInfo(shippingInfo.address, shippingInfo.zipcode, shippingInfo.city, shippingInfo.country)
            )
            (message, event.offset)
          }
      }
    }
  }
}