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

package fixtures

import models.alertOrRejection.{AlertOrRejectionReasonModel, SubmitAlertOrRejectionModel}
import models.common.ExciseMovementModel
import models.response.emcsTfe.{AddressModel, SubmissionResponse, TraderModel}
import play.api.libs.json.{JsObject, JsValue, Json}

import java.time.LocalDate

trait SubmissionFixtures extends BaseFixtures {

  val submitAlertOrRejectionModel: SubmitAlertOrRejectionModel = SubmitAlertOrRejectionModel(
    consigneeTrader = Some(
      TraderModel(
        traderExciseNumber = Some(testErn),
        traderName = Some("Consignee trader name"),
        address = Some(AddressModel(
          streetNumber = Some("7"),
          street = Some("Consignee street"),
          postcode = Some("AA1 1AA"),
          city = Some("Consignee city")
        )),
        eoriNumber = None
      )
    ),
    exciseMovement = ExciseMovementModel(testArc, 1),
    destinationOffice = "destinationOffice",
    dateOfAlertOrRejection = LocalDate.now,
    isRejected = true,
    alertOrRejectionReasons = Some(Seq[AlertOrRejectionReasonModel]().empty)
  )

  val submitAlertOrRejectionChRISResponseModel: SubmissionResponse = SubmissionResponse(
    receipt = testConfirmationReference,
    downstreamService = "ChRIS"
  )

  val submitAlertOrRejectionEISResponseModel: SubmissionResponse = SubmissionResponse(
    receipt = testConfirmationReference,
    downstreamService = "EIS"
  )

  val submitAlertOrRejectionChRISResponseJson: JsObject = Json.obj(
    "receipt" -> testConfirmationReference,
    "receiptDate" -> testReceiptDate
  )

  val submitAlertOrRejectionEISResponseJson: JsValue = Json.parse(
    s"""{
       | "status": "OK",
       | "message": "$testConfirmationReference",
       | "emcsCorrelationId": "3e8dae97-b586-4cef-8511-68ac12da9028"
       |}""".stripMargin)
}
