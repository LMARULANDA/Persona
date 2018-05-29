package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.{JsNumber, JsResult, JsValue, Json}
import play.api.mvc._


import scala.concurrent.{ExecutionContext, Future}

class PersonaController @Inject()(repo: PersonRepository,
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
    * This is asynchronous, since we're invoking the asynchronous methods on PersonRepository.
    * recibir una persona  para crearla sin bloqueos(JsonHttp)
    */

  def addPerson = Action.async(parse.json[Person]) { request =>
    val person: Person = request.body
    insertPerson(person)
  }

  private def insertPerson(person: Person): Future[Result] = {
    repo.create(person.name, person.age)
      .map(e => Ok(Json.toJson(e)))
      .recoverWith {
        case e: Exception => {
          Future.successful(InternalServerError("No pudo guardarse el registro"))
        }
      }
  }



  /**
    * A REST endpoint that gets all the people as JSON.
    * implicit writes.
    * devolver lista de personas sin bloqueo
    */
  def getPersons = Action.async {  request =>
    val personas: Future[Seq[Person]] = repo.list()
    repo.list().map { people =>
      Ok(Json.toJson(people))
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

}