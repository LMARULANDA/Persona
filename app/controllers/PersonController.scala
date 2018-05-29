package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.{JsNumber, JsResult, JsValue, Json}
import play.api.mvc._
import slick.dbio.Effect
import slick.driver.JdbcProfile

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

  /*pendiente por corregir insertPersonB.
  def addPersonB = Action(parse.json) { request  =>
    val person: JsValue = request.body
    val persona: Person = Person(0,(person \ "name").as[String],(person \ "age").as[Int])
    insertPersonB(persona)

  }

  private def insertPersonB(person: Person):Result = {
    val result: Result = repo.createB(person)

  }*/


  /*
  def addPerson2 = Action.async(parse.json) { request =>
    val person: JsValue = request.body
    val persona = Person(0, (person \ "name").as[String], (person \ "age").as[Int])
    insertPerson(persona)
  }*/


  //pruebas de Concepto


  def addPerson5 = Action { request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    // Expecting json body
    jsonBody.map { json =>
      Ok("Got: " + (json \ "name").as[String])
    }.getOrElse {
    BadRequest("Expecting application/json request body")
  }
  }

  def addPerson4 = Action(parse.json) { request: Request[JsValue] =>
    Ok("Got:" + (request.body \ "name").as[String])
  }


  def addPerson6 = Action { request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    //Got: JsDefined("laura")esta en la lista
    // Expecting json body
    jsonBody.map { json =>
      if ((json \ "name").as[String] == "laura") {
        Ok("Got: " + (json \ "name").as[String] + " esta en la lista")
      } else {
        Ok("El nombre laura no se encuentra en el json")
      }
      //Ok("Got: " + (json \ "name").as[String])'
    }.getOrElse {
      BadRequest("Expecting application/json request body")
    }
  }

  def addPerson9 = Action { request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    //Got: JsDefined("laura")esta en la lista
    // Expecting json body
    jsonBody.map { json =>
      val ageResult: JsResult[Int] = json.validate[Int](Person.personAgeReads)
      ageResult.map( x => {
        if ( x >= 18 ) {
          Ok("Got: " + (json \ "name").as[String] + " es mayor de edad: " + x)
        } else {
          Ok((json \ "name").as[String] + " no es mayor de edad")
        }
      }).getOrElse(BadRequest("La edad no es numerica o no se cumplen las validaciones"))

      //Ok("Got: " + (json \ "name").as[String])
    }.getOrElse {
      BadRequest("Expecting application/json request body")
    }
  }

  def addPerson8 = Action { request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    Ok(jsonBody.get)

  }

  def archivo = Ok.sendResource("algunacosa/aaaa.pdf")

  /**
    * A REST endpoint that gets all the people as JSON.
    * implicit writes.
    * devolver lista de personas sin bloqueo
    */
  def getPersons1 = Action.async {  request =>
    val personas: Future[Seq[Person]] = repo.list()
    repo.list().map { people =>
      Ok(Json.toJson(people))
    }
  }

  /**
    * Metodo que retorna error,no puede escribir una instancia de Future a HTTP response.
    *
    * @return
    */
  /*def getPersons = Action { implicit request =>
    val personas = repo.list()
    Ok(personas)
  }*/
  def getPersons2 = Action.async { request =>
    val personas: Future[Seq[Person]] = repo.list()
    personas.map { p =>
      Ok(Json.toJson(p))

    }
  }

  //preguntar a roger

  /* def getPersons3 = Action { request =>
    val personas: JdbcProfile.this.StreamingProfileAction[Seq[Person], Person, Effect.Read]:  = repo.listB()

  }

}*/

  def getPersons3: Action[AnyContent] = Action.async { request =>
    val persona: Future[Seq[Person]] = repo.personMayorDeEdad()
    persona.map { p =>
      Ok(Json.toJson(p))
    }
  }

  def getPersons: Action[AnyContent] = Action.async { request =>
    val persona: Future[Seq[Person]] = repo.personOrderBy()
    persona.map { p =>
      Ok(Json.toJson(p))
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