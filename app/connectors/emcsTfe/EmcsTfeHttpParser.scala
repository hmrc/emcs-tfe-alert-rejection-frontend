/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors.emcsTfe

import connectors.BaseConnectorUtils
import models.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status.OK
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}

import java.net.URL
import scala.concurrent.{ExecutionContext, Future}

trait EmcsTfeHttpParser[A] extends BaseConnectorUtils[A] {
  def http: HttpClientV2

  implicit object EmcsTfeReads extends HttpReads[Either[ErrorResponse, A]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, A] = {
      response.status match {
        case OK => response.validateJson match {
          case Some(valid) => Right(valid)
          case None =>
            logger.warn(s"[read] Bad JSON response from emcs-tfe")
            Left(JsonValidationError)
        }
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe: $status")
          Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  def get(url: URL)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, A]] = {
    http
      .get(url)
      .execute[Either[ErrorResponse, A]]
  }

  def post[I](url: URL, body: I)(implicit hc: HeaderCarrier, ec: ExecutionContext, writes: Writes[I]): Future[Either[ErrorResponse, A]] = {
    http
      .post(url)
      .withBody(Json.toJson(body))
      .execute[Either[ErrorResponse, A]]
  }
}
