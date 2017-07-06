package com.xebia.webshop.shoppingbag.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType

sealed trait PShoppingBagCommand

object PShoppingBagCommand {

  case class AddItem(sku: String, amount: Int) extends PShoppingBagCommand with ReplyType[Done]

  case class RemoveItem(sku: String, amount: Int) extends PShoppingBagCommand with ReplyType[Done]

  case object GetBasket extends PShoppingBagCommand with ReplyType[PShoppingBag]

}