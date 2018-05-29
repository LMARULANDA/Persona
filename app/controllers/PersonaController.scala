package controllers

import javax.inject.Inject
import models.PersonRepository
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}

import scala.concurrent.ExecutionContext

class PersonaController @Inject()(repo: PersonRepository,
                                  cc: MessagesControllerComponents
                                 )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc)  {



}
