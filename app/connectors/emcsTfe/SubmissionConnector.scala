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

import config.AppConfig
import models.alertOrRejection.SubmitAlertOrRejectionModel
import models.response.emcsTfe.SubmissionResponse
import models.{ErrorResponse, UnexpectedDownstreamResponseError}
import play.api.libs.json.Reads
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionConnector @Inject()(val http: HttpClient,
                                    config: AppConfig) extends EmcsTfeHttpParser[SubmissionResponse] {

  override implicit val reads: Reads[SubmissionResponse] = SubmissionResponse.reads

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def submit(exciseRegistrationNumber: String, arc: String, submissionModel: SubmitAlertOrRejectionModel)
            (implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, SubmissionResponse]] = {
    post(s"$baseUrl/alert-or-rejection/$exciseRegistrationNumber/$arc", submissionModel)

  }.recover {
    ex =>
      logger.warn(s"[submit] Unexpected response from emcs-tfe : ${ex.getMessage}")
      Left(UnexpectedDownstreamResponseError)
  }

}
