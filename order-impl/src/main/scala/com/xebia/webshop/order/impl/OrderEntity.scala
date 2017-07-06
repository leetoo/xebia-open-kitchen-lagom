package com.xebia.webshop.order.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import com.xebia.webshop.order.impl.POrder.{POrderLine, PSKU, PShippingInfo}
import com.xebia.webshop.order.impl.POrderCommand.GetOrder
import play.api.libs.json.{Format, Json}

object OrderEntityFormats {
  implicit val pShippingInfoFormat: Format[PShippingInfo] = Json.format
  implicit val pSKUFormat: Format[PSKU] = Json.format
  implicit val pOrderLine: Format[POrderLine] = Json.format
  implicit val pOrderFormat: Format[POrder] = Json.format

  implicit val createOrderFormat: Format[POrderCommand.CreateOrder] = Json.format
  implicit val addShippingInformation: Format[POrderCommand.AddShippingInformation] = Json.format
  implicit val addPayment: Format[POrderCommand.AddPayment] = Json.format
  implicit val getOrderFormat = JsonSerializer.emptySingletonFormat(GetOrder)

  implicit val orderCreatedFormat: Format[POrderEvent.OrderCreated] = Json.format
  implicit val shippingInfoAddedFormat: Format[POrderEvent.ShippingInformationAdded] = Json.format
  implicit val orderPaidFormat: Format[POrderEvent.OrderPaid] = Json.format
  implicit val orderConfirmedFormat = JsonSerializer.emptySingletonFormat(POrderEvent.OrderConfirmed)
}

case class POrder(id: String, orderLines: Seq[POrderLine], shippingInfo: Option[PShippingInfo], paymentReference: Option[String], confirmed: Boolean)

object POrder {

  case class PShippingInfo(address: String, zipcode: String, city: String, country: String)

  case class PSKU(id: String)

  case class POrderLine(sku: PSKU, amount: Int)

}

class OrderEntity extends PersistentEntity {
  override type Command = POrderCommand
  override type Event = POrderEvent
  override type State = POrder

  override def initialState = POrder(this.entityId, Seq.empty, None, None, false)

  override def behavior: Behavior = Actions()
    .onCommand[POrderCommand.CreateOrder, Done] {
    case (POrderCommand.CreateOrder(items), ctx, state) => {
      ctx.thenPersist(POrderEvent.OrderCreated(items)) {
        evt => ctx.reply(Done)
      }
    }
  }.onCommand[POrderCommand.AddShippingInformation, Done] {
    case (POrderCommand.AddShippingInformation(info), ctx, state) => {
      ctx.thenPersist(POrderEvent.ShippingInformationAdded(info)) {
        evt => ctx.reply(Done)
      }
    }
  }.onCommand[POrderCommand.AddPayment, Done] {
    case (POrderCommand.AddPayment(reference), ctx, state) => {
      ctx.thenPersistAll(POrderEvent.OrderPaid(reference), POrderEvent.OrderConfirmed) {
        () => ctx.reply(Done)
      }
    }
  }.onReadOnlyCommand[GetOrder.type, POrder] {
    case (GetOrder, ctx, state) => ctx.reply(state)
  }.onEvent {
    case (POrderEvent.OrderCreated(items), state) => state.copy(orderLines = items)
    case (POrderEvent.ShippingInformationAdded(info), state) => state.copy(shippingInfo = Some(info))
    case (POrderEvent.OrderPaid(reference), state) => state.copy(paymentReference = Some(reference))
    case (POrderEvent.OrderConfirmed, state) => state.copy(confirmed = true)
  }
}
