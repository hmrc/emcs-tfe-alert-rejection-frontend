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

package mocks.services

import models.UserAnswers
import models.requests.DataRequest
import models.response.emcsTfe.{GetMovementResponse, SubmissionResponse}
import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import services.SubmissionService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockSubmissionService extends MockFactory {

  lazy val mockSubmissionService: SubmissionService = mock[SubmissionService]

  object MockSubmissionService {

    def submit(ern: String, arc: String, movement: GetMovementResponse, userAnswers: UserAnswers)
    : CallHandler4[String, String, HeaderCarrier, DataRequest[_], Future[SubmissionResponse]] =
      (mockSubmissionService.submit(_: String, _: String)(_: HeaderCarrier, _: DataRequest[_]))
        .expects(where {
          (_ern: String, _arc: String, _: HeaderCarrier, request: DataRequest[_]) => {
            _ern == ern && _arc == arc && request.movementDetails == movement && request.userAnswers == userAnswers
          }
        })

  }

}
