package com.xebia.webshop.shoppingbag.impl

import com.lightbend.lagom.internal.client.CircuitBreakerMetricsProviderImpl
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import com.xebia.webshop.shoppingbag.api.{AddItemToShoppingBagRequest, ShoppingBag, ShoppingBagService}
import com.xebia.webshop.shoppingbag.impl.PShoppingBagCommand.{AddItem, GetBasket, RemoveItem}
import com.xebia.webshop.shoppingbag.impl.PShoppingBagEvent.{ItemAdded, ItemRemoved}
import play.api.LoggerConfigurator
import play.api.libs.ws.ahc.AhcWSComponents

import scala.collection.immutable.Seq

abstract class ShoppingBagApplication(ctx: LagomApplicationContext) extends LagomApplication(ctx)
  with AhcWSComponents
  with LagomKafkaComponents
  with CassandraPersistenceComponents {

  override lazy val lagomServer: LagomServer = serverFor[ShoppingBagService](wire[ShoppingBagServiceImpl])

  override lazy val jsonSerializerRegistry = ShoppingBagSerializerRegistry

  persistentEntityRegistry.register(wire[ShoppingBagEntity])
}

class ShoppingBagApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    val environment = context.playContext.environment
    LoggerConfigurator(environment.classLoader).foreach {
      _.configure(environment)
    }
    new ShoppingBagApplication(context) with LagomDevModeComponents
  }

  override def load(context: LagomApplicationContext): LagomApplication =
    new ShoppingBagApplication(context) {
      override lazy val circuitBreakerMetricsProvider = new CircuitBreakerMetricsProviderImpl(actorSystem)

      override def serviceLocator: ServiceLocator = NoServiceLocator
    }
}

object ShoppingBagSerializerRegistry extends JsonSerializerRegistry {

  import ShoppingBagEntityFormats._
  import AddItemToShoppingBagRequest.format
  import ShoppingBag.format

  override def serializers: Seq[JsonSerializer[_]] = Seq(
    /* External API */
    JsonSerializer[AddItemToShoppingBagRequest],
    JsonSerializer[ShoppingBag],

    /* Entity commands */
    JsonSerializer[AddItem],
    JsonSerializer[RemoveItem],
    JsonSerializer[GetBasket.type],

    /* Entity events*/
    JsonSerializer[ItemAdded],
    JsonSerializer[ItemRemoved],

    /* Entity state */
    JsonSerializer[PShoppingBag]
  )
}