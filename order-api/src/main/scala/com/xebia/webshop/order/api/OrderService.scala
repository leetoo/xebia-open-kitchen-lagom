package com.xebia.webshop.order.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport._
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait OrderService extends Service {
  def createOrder(orderId: String): ServiceCall[CreateOrderRequest, Order]

  def confirmOrder(orderId: String): ServiceCall[ConfirmOrderRequest, NotUsed]

  def getOrder(orderId: String): ServiceCall[NotUsed, Order]

  def orderEvents: Topic[OrderEvent]

  override def descriptor: Descriptor = {
    import Method._
    import Service._

    named("order").withCalls {
      restCall(POST, "/order", createOrder _)
      restCall(GET, "/order/:orderId", getOrder _)
    }.withTopics(
      topic("order-OrderEvent", orderEvents)
    )
  }
}
