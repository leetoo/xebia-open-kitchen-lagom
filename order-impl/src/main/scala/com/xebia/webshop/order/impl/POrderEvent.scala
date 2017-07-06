package com.xebia.webshop.order.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger}
import com.xebia.webshop.order.impl.POrder.{POrderLine, PShippingInfo}

trait POrderEvent extends AggregateEvent[POrderEvent] {
  override def aggregateTag: AggregateEventTagger[POrderEvent] = POrderEvent.Tag
}

object POrderEvent {

  val Tag = AggregateEventTag[POrderEvent]

  case class OrderCreated(orderLines: Seq[POrderLine]) extends POrderEvent

  case class ShippingInformationAdded(info: PShippingInfo) extends POrderEvent

  case class OrderPaid(reference: String) extends POrderEvent

  case object OrderConfirmed extends POrderEvent

}
