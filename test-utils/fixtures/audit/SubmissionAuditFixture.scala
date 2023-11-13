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

package fixtures.audit

import fixtures.BaseFixtures
import models.{SelectReason, UnexpectedDownstreamResponseError}
import models.alertOrRejection.{AlertOrRejectionReasonModel, SubmitAlertOrRejectionModel}
import models.audit.SubmissionAudit
import models.common.ExciseMovementModel
import models.response.emcsTfe.{AddressModel, SubmissionResponse, TraderModel}
import play.api.libs.json.Json

import java.time.LocalDate

trait SubmissionAuditFixture extends BaseFixtures {

  val submissionAuditModelSuccessful = SubmissionAudit(
    credentialId = testCredId,
    internalId = testInternalId,
    ern = testErn,
    submissionRequest = SubmitAlertOrRejectionModel(
      consigneeTrader = Some(
        TraderModel(
          traderExciseNumber = Some(testErn),
          traderName = Some("Trader Name"),
          address = Some(
            AddressModel(
              streetNumber = Some("1"),
              street = Some("Somewhere Street"),
              postcode = Some("AA1 1AA"),
              city = Some("city name")
            )
          ),
          eoriNumber = None
        )
      ),
      exciseMovement = ExciseMovementModel(
        arc = testArc,
        sequenceNumber = 1
      ),
      destinationOffice = "GB123456",
      dateOfAlertOrRejection = LocalDate.of(2023, 1, 1),
      isRejected = true,
      alertOrRejectionReasons = Some(
        Seq(
          AlertOrRejectionReasonModel(
            reason = SelectReason.ConsigneeDetailsWrong,
            additionalInformation = Some("Consignee Details Wrong")
          ),
          AlertOrRejectionReasonModel(
            reason = SelectReason.GoodTypesNotMatchOrder,
            additionalInformation = Some("Good Types Not Match Order")
          ),
          AlertOrRejectionReasonModel(
            reason = SelectReason.QuantitiesNotMatchOrder,
            additionalInformation = Some("Quantities Not Match Order")
          ),
          AlertOrRejectionReasonModel(
            reason = SelectReason.Other,
            additionalInformation = Some("Other")
          ),

        )
      )
    ),
    submissionResponse = Right(SubmissionResponse("receipt", "receipt date"))
  )


  val submissionAuditModelSuccessfulAsJson = Json.obj(
      "credentialId" -> testCredId,
      "internalId" -> testInternalId,
      "ern" -> testErn,
      "arc" -> testArc,
      "sequenceNumber" -> 1,
      "consigneeTrader" -> Json.obj(
        "traderExciseNumber" -> testErn,
        "traderName" -> "Trader Name",
        "address" -> Json.obj(
         "streetNumber" -> "1",
          "street" -> "Somewhere Street",
          "postcode" -> "AA1 1AA",
          "city" -> "city name"
        )
      ),
      "exciseMovement" -> Json.obj(
        "arc" -> "arc",
        "sequenceNumber" -> 1
      ),
      "destinationOffice" -> "GB123456",
      "dateOfAlertOrRejection" -> "2023-01-01",
      "isRejected" -> true,
      "alertOrRejectionReasons" -> Json.arr(
          Json.obj(
            "reasonCode" -> "1",
            "reasonDescription" -> "ConsigneeDetailsWrong",
            "additionalInformation" -> "Consignee Details Wrong"
          ),
          Json.obj(
            "reasonCode" -> "2",
            "reasonDescription" -> "GoodTypesNotMatchOrder",
            "additionalInformation" -> "Good Types Not Match Order"
          ),
          Json.obj(
            "reasonCode" -> "3",
            "reasonDescription" -> "QuantitiesNotMatchOrder",
            "additionalInformation" -> "Quantities Not Match Order"
          ),
          Json.obj(
            "reasonCode" -> "0",
            "reasonDescription" -> "Other",
            "additionalInformation" -> "Other"
          )
        ),
      "status" -> "success",
      "receipt" -> "receipt",
      "receiptDate" -> "receipt date"
  )

  val submissionAuditModelFailed = submissionAuditModelSuccessful.copy(
    submissionResponse = Left(UnexpectedDownstreamResponseError)
  )

  val submissionAuditModelFailedAsJson = Json.obj(
    "credentialId" -> testCredId,
    "internalId" -> testInternalId,
    "ern" -> testErn,
    "arc" -> testArc,
    "sequenceNumber" -> 1,
    "consigneeTrader" -> Json.obj(
      "traderExciseNumber" -> testErn,
      "traderName" -> "Trader Name",
      "address" -> Json.obj(
        "streetNumber" -> "1",
        "street" -> "Somewhere Street",
        "postcode" -> "AA1 1AA",
        "city" -> "city name"
      )
    ),
    "exciseMovement" -> Json.obj(
      "arc" -> "arc",
      "sequenceNumber" -> 1
    ),
    "destinationOffice" -> "GB123456",
    "dateOfAlertOrRejection" -> "2023-01-01",
    "isRejected" -> true,
    "alertOrRejectionReasons" -> Json.arr(
      Json.obj(
        "reasonCode" -> "1",
        "reasonDescription" -> "ConsigneeDetailsWrong",
        "additionalInformation" -> "Consignee Details Wrong"
      ),
      Json.obj(
        "reasonCode" -> "2",
        "reasonDescription" -> "GoodTypesNotMatchOrder",
        "additionalInformation" -> "Good Types Not Match Order"
      ),
      Json.obj(
        "reasonCode" -> "3",
        "reasonDescription" -> "QuantitiesNotMatchOrder",
        "additionalInformation" -> "Quantities Not Match Order"
      ),
      Json.obj(
        "reasonCode" -> "0",
        "reasonDescription" -> "Other",
        "additionalInformation" -> "Other"
      )
    ),
    "status" -> "failed",
    "failedMessage" -> "Unexpected downstream response status"
  )

}
