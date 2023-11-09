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

package mocks.connectors

import connectors.emcsTfe.SubmissionConnector
import models.ErrorResponse
import models.alertOrRejection.SubmitAlertOrRejectionModel
import models.response.emcsTfe.SubmissionResponse
import org.scalamock.handlers.CallHandler5
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockSubmissionConnector extends MockFactory {

  lazy val mockSubmissionConnector: SubmissionConnector = mock[SubmissionConnector]

  object MockSubmitExplainDelayConnector {

    def submit(ern: String,
               arc: String,
               submission:SubmitAlertOrRejectionModel
              ): CallHandler5[String, String, SubmitAlertOrRejectionModel, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, SubmissionResponse]]] =
      (mockSubmissionConnector.submit(_: String, _: String, _: SubmitAlertOrRejectionModel)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, arc, submission, *, *)
  }
}
