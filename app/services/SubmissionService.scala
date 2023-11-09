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

package services

import config.AppConfig
import connectors.emcsTfe.SubmissionConnector
import models.alertOrRejection.SubmitAlertOrRejectionModel
import models.audit.SubmissionAudit
import models.requests.DataRequest
import models.response.emcsTfe.SubmissionResponse
import models.{ErrorResponse, SubmitAlertOrRejectionException}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionService @Inject()(submitExplainDelayConnector: SubmissionConnector,
                                  auditingService: AuditingService
                                  )
                                 (implicit ec: ExecutionContext, appConfig: AppConfig) extends Logging {

  def submit(ern: String, arc: String)(implicit hc: HeaderCarrier, dataRequest: DataRequest[_]): Future[SubmissionResponse] = {

    val submissionRequest = SubmitAlertOrRejectionModel()

    submitExplainDelayConnector.submit(ern, arc, submissionRequest).map {
      case Right(submissionResponse) =>
        logger.info("[submit] Successful alert or rejection submission")

        writeAudit(submissionRequest, Right(submissionResponse))

        submissionResponse
      case Left(errorResponse) =>
        logger.warn("[submit] Unsuccessful alert or rejection submission")

        writeAudit(submissionRequest, Left(errorResponse))

        throw SubmitAlertOrRejectionException(s"Failed to submit alert or rejection to emcs-tfe for ern: '$ern' & arc: '$arc'")
    }
  }

  private def writeAudit(
                          submissionRequest: SubmitAlertOrRejectionModel,
                          submissionResponse: Either[ErrorResponse, SubmissionResponse]
                        )(implicit hc: HeaderCarrier, dataRequest: DataRequest[_]): Unit = {

    auditingService.audit(
      SubmissionAudit(
        credentialId = dataRequest.request.request.credId,
        internalId = dataRequest.internalId,
        ern = dataRequest.ern,
        submissionRequest = submissionRequest,
        submissionResponse = submissionResponse
      )
    )
  }

}
