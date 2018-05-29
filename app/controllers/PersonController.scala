package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.{JsNumber, JsResult, JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class PersonController @Inject()(repo: PersonRepository,
                                  cc: MessagesControllerComponents
                                )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  /**
   * The mapping for the person form.
   */
  val personForm: Form[CreatePersonForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "age" -> number.verifying(min(0), max(140))
    )(CreatePersonForm.apply)(CreatePersonForm.unapply)
  }

  /**
   * The index action.
   */


  /**
   * The add person action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on PersonRepository.
   */

    def addPerson1= Action.async(parse.json[Person]){ request =>
      val person = request.body
      insertPerson(person)
        }

  private def insertPerson(person: Person):Future[Result] = {
    repo.create(person.name,person.age)
      .map(e =>Ok(Json.toJson(e)))
      .recoverWith {
        case e : Exception => {
          Future.successful(InternalServerError("No pudo guardarse el registro"))
        }
      }
  }

  def addPerson2 = Action { request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    // Expecting json body
    jsonBody.map { json =>
      Ok("Got: " + (json \ "name").as[String])
    }.getOrElse {
      BadRequest("Expecting application/json request body")
    }
  }

  def addPerson3 = Action(parse.json) { request: Request[JsValue] =>
    Ok("Got:" + (request.body \ "name").as[String])
  }

  def addPerson4 = Action { request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    //Got: JsDefined("laura")esta en la lista
    // Expecting json body
    jsonBody.map { json =>
      if ((json \ "name").as[String] == "laura"){
        Ok("Got: " + (json \ "name").as[String] +" esta en la lista")
      } else {
        Ok("El nombre laura no se encuentra en el json")
      }
      //Ok("Got: " + (json \ "name").as[String])
    }.getOrElse {
      BadRequest("Expecting application/json request body")
    }
  }

  def addPerson = Action { request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    //Got: JsDefined("laura")esta en la lista
    // Expecting json body
    jsonBody.map { json =>
      val ageResult: JsResult[Int] = json.validate[Int](Person.personAgeReads)
      if ((json \ "age").as[Int] >= 18){
        Ok("Got: " + (json \ "name").as[String] +" es mayor de edad: " + ageResult.get)
      } else {
        Ok((json \"name").as[String]+ " no es mayor de edad")
      }
      //Ok("Got: " + (json \ "name").as[String])
    }.getOrElse {
      BadRequest("Expecting application/json request body")
    }
  }


  /**
   * A REST endpoint that gets all the people as JSON.
    * implicit writes.
   */
  def getPersons = Action.async { implicit request =>
    repo.list().map { people =>
      Ok(Json.toJson(people))
    }
  }
}

/**
 * The create person form.
 *
 * Generally for forms, you should define separate objects to your models, since forms very often need to present data
 * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
 * that is generated once it's created.
 */
case class CreatePersonForm(name: String, age: Int)
