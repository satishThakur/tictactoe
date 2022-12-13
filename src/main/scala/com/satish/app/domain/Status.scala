package com.satish.app.domain

sealed trait Status

object Status:
  case object Ongoing extends Status
  case object Draw extends Status
  case class Completed(winner: Player) extends Status
