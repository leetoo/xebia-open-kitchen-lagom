package com.xebia.webshop.order.impl

import com.lightbend.lagom.internal.client.CircuitBreakerMetricsProviderImpl
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire.wire
import com.xebia.webshop.order.api.{ConfirmOrderRequest, CreateOrderRequest, OrderService}
import com.xebia.webshop.order.impl.POrder.{POrderLine, PSKU, PShippingInfo}
import com.xebia.webshop.order.impl.POrderCommand.{AddPayment, AddShippingInformation, CreateOrder, GetOrder}
import com.xebia.webshop.order.impl.POrderEvent.{OrderConfirmed, OrderCreated, OrderPaid, ShippingInformationAdded}
import play.api.LoggerConfigurator
import play.api.libs.ws.ahc.AhcWSComponents

import scala.collection.immutable.Seq

abstract class OrderApplication(ctx: LagomApplicationContext) extends LagomApplication(ctx)
  with AhcWSComponents
  with CassandraPersistenceComponents
  with LagomKafkaComponents {

  override lazy val lagomServer: LagomServer = serverFor[OrderService](wire[OrderServiceImpl])

  override lazy val jsonSerializerRegistry = OrderSerializerRegistry

  persistentEntityRegistry.register(wire[OrderEntity])
}

class OrderApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    val environment = context.playContext.environment
    LoggerConfigurator(environment.classLoader).foreach {
      _.configure(environment)
    }
    new OrderApplication(context) with LagomDevModeComponents
  }

  override def load(context: LagomApplicationContext): LagomApplication =
    new OrderApplication(context) {
      override lazy val circuitBreakerMetricsProvider = new CircuitBreakerMetricsProviderImpl(actorSystem)

      override def serviceLocator: ServiceLocator = NoServiceLocator
    }
}

object OrderSerializerRegistry extends JsonSerializerRegistry {

  import OrderEntityFormats._

  override def serializers: Seq[JsonSerializer[_]] = Seq(
    /* External API */
    JsonSerializer[CreateOrderRequest],
    JsonSerializer[ConfirmOrderRequest],

    /* Entity commands */
    JsonSerializer[CreateOrder],
    JsonSerializer[AddShippingInformation],
    JsonSerializer[AddPayment],
    JsonSerializer[GetOrder.type],


    /* Entity events*/
    JsonSerializer[OrderCreated],
    JsonSerializer[ShippingInformationAdded],
    JsonSerializer[OrderPaid],
    JsonSerializer[OrderConfirmed.type],

    /* Entity state */
    JsonSerializer[PShippingInfo],
    JsonSerializer[PSKU],
    JsonSerializer[POrderLine],
    JsonSerializer[POrder]
  )
}
