/*
 * Copyright (C) 2009-2013 Typesafe Inc. <http://www.typesafe.com>
 */
package play.filters.csrf

import play.api.libs.ws.WS.WSRequestHolder
import scala.concurrent.Future
import play.api.libs.ws.{WS, Response}
import play.api.mvc._

/**
 * Specs for the Scala per action CSRF actions
 */
object ScalaCSRFActionSpec extends CSRFCommonSpecs {

  def csrfCheckRequest[T](makeRequest: (WSRequestHolder) => Future[Response])(handleResponse: Response => T) = {
    withServer {
      case _ => CSRFCheck(Action(Results.Ok))
    } {
      handleResponse(await(makeRequest(WS.url("http://localhost:" + testServerPort))))
    }
  }

  def csrfAddToken[T](makeRequest: (WSRequestHolder) => Future[Response])(handleResponse: Response => T) = {
    withServer {
      case _ => CSRFAddToken(Action { implicit req =>
        CSRF.getToken(req).map { token =>
          Results.Ok(token.value)
        } getOrElse Results.NotFound
      })
    } {
      handleResponse(await(makeRequest(WS.url("http://localhost:" + testServerPort))))
    }
  }

}
