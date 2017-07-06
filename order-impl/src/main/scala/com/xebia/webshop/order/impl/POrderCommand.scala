package com.xebia.webshop.order.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.xebia.webshop.order.impl.POrder.{POrderLine, PShippingInfo}

trait POrderCommand

object POrderCommand {

  case class CreateOrder(orderLines: Seq[POrderLine]) extends POrderCommand with ReplyType[Done]

  case class AddShippingInformation(info: PShippingInfo) extends POrderCommand with ReplyType[Done]

  case class AddPayment(reference: String) extends POrderCommand with ReplyType[Done]

  case object GetOrder extends POrderCommand with ReplyType[POrder]

}