package services

import javax.inject.Inject
import models.{Person, PersonRepository}
import play.api.libs.json.Json
import play.api.mvc.Result

import scala.concurrent.Future

/*

class PersonService @Inject (personRepository: PersonRepository) {

   def insertPerson(person: Person):Future[Result] = {
    personRepository.create(person.name,person.age)
      .map(e =>Ok(Json.toJson(e)))
      .recoverWith {
        case e : Exception => {
          Future.successful(InternalServerError("No pudo guardarse el registro"))
        }
      }
  }
}*/
