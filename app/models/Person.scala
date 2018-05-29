package models

import play.api.libs.json._
import play.api.libs.json.Reads._
case class Person(id: Long, name: String, age: Int)

object Person {
  implicit val personFormat = Json.format[Person]
  implicit val personAgeReads = (JsPath \ "age").read[Int](min(18))
}
