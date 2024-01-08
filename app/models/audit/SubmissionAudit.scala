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

package models.audit

import models.ErrorResponse
import models.alertOrRejection.{AlertOrRejectionReasonModel, SubmitAlertOrRejectionModel}
import models.response.emcsTfe.SubmissionResponse
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{JsValue, Json, Writes}

case class SubmissionAudit (
                        credentialId: String,
                        internalId: String,
                        ern: String,
                        receiptDate: String,
                        submissionRequest: SubmitAlertOrRejectionModel,
                        submissionResponse: Either[ErrorResponse, SubmissionResponse]
                      ) extends AuditModel {

  override val auditType: String = "AlertOrRejectSubmission"

  override val detail: JsValue = Json.obj(fields =
    "credentialId" -> credentialId,
    "internalId" -> internalId,
    "ern" -> ern,
    "arc" -> submissionRequest.exciseMovement.arc,
    "sequenceNumber" -> submissionRequest.exciseMovement.sequenceNumber,
    "consigneeTrader" -> submissionRequest.consigneeTrader,
    "exciseMovement" -> submissionRequest.exciseMovement,
    "destinationOffice" -> submissionRequest.destinationOffice,
    "dateOfAlertOrRejection" -> submissionRequest.dateOfAlertOrRejection,
    "isRejected" -> submissionRequest.isRejected,
    "alertOrRejectionReasons" -> alertOrRejectionReasonsForAuditing(submissionRequest.alertOrRejectionReasons)
  ) ++ {
    submissionResponse match {
      case Right(success) =>
        Json.obj(fields =
          "status" -> "success",
          "receipt" -> success.receipt,
          "receiptDate" -> receiptDate
        )
      case Left(failedMessage) =>
        Json.obj(fields =
          "status" -> "failed",
          "failedMessage" -> failedMessage.message
        )
    }
  }

  private def alertOrRejectionReasonsForAuditing(alertOrRejectReasons: Option[Seq[AlertOrRejectionReasonModel]]): Seq[JsValue] = {

    val auditWrites = Writes[AlertOrRejectionReasonModel] { model =>
      Json.obj(Seq[Option[(String, JsValueWrapper)]](
        Some("reasonCode" -> model.reason),
        Some("reasonDescription" -> model.reason.getClass.getSimpleName.stripSuffix("$")),
        model.additionalInformation.map("additionalInformation" -> _)
      ).flatten: _*)
    }

    alertOrRejectReasons match {
      case Some(reasons: Seq[AlertOrRejectionReasonModel]) =>
        reasons.map{ reason =>
          Json.toJson(reason)(auditWrites)
        }

      case _ => Seq[JsValue]()
    }
  }
}
