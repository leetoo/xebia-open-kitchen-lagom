package com.xebia.webshop.order.api

import com.xebia.webshop.order.api.Order.{OrderLine, ShippingInfo}

import play.api.libs.json._
import julienrf.json.derived

sealed trait OrderEvent

object OrderEvent {

  case class OrderCreated(orderId: String, orderLines: Seq[OrderLine]) extends OrderEvent

  object OrderCreated {
    implicit val format: Format[OrderCreated] = Json.format
  }

  case class OrderConfirmed(orderId: String, orderLines: Seq[OrderLine], shippingInfo: ShippingInfo) extends OrderEvent

  object OrderConfirmed {
    implicit val format: Format[OrderConfirmed] = Json.format
  }

  implicit val format: Format[OrderEvent] =
    derived.flat.oformat((__ \ "type").format[String])
}
