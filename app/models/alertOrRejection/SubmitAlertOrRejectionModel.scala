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

package models.alertOrRejection

import config.AppConfig
import models.{DestinationOffice, UserAnswers}
import models.DestinationType.TemporaryRegisteredConsignee
import models.SelectAlertReject.{Alert, Reject}
import models.SelectReason.{ConsigneeDetailsWrong, GoodTypesNotMatchOrder, Other, QuantitiesNotMatchOrder}
import models.common.ExciseMovementModel
import models.requests.DataRequest
import models.response.emcsTfe.{GetMovementResponse, TraderModel}
import pages._
import play.api.libs.json.{Format, Json}
import utils.{JsonOptionFormatter, ModelConstructorHelpers}

import java.time.LocalDate

case class SubmitAlertOrRejectionModel(consigneeTrader: Option[TraderModel],
                                       exciseMovement: ExciseMovementModel,
                                       destinationOffice: String,
                                       dateOfAlertOrRejection: LocalDate,
                                       isRejected: Boolean,
                                       alertOrRejectionReasons: Option[Seq[AlertOrRejectionReasonModel]])
object SubmitAlertOrRejectionModel extends JsonOptionFormatter with ModelConstructorHelpers {

  implicit val fmt: Format[SubmitAlertOrRejectionModel] = Json.format

  private[models] def destinationOfficePrefix(implicit dataRequest: DataRequest[_]): String = {
    if (dataRequest.userAnswers.isNorthernIrelandTrader) {
      mandatoryPage(DestinationOfficePage).toString
    } else {
      DestinationOffice.GreatBritain.toString
    }
  }

  private[models] def isRejectedSubmission(implicit dataRequest: DataRequest[_]): Boolean = {
    mandatoryPage(SelectAlertRejectPage) match {
      case Reject => true
      case Alert => false
    }
  }

  private[models] def consigneeTraderDetails(movementDetails: GetMovementResponse)(implicit dataRequest: DataRequest[_]): Option[TraderModel] = {
    (movementDetails.destinationType, movementDetails.consigneeTrader) match {
      case (TemporaryRegisteredConsignee, Some(consignee: TraderModel)) =>
        Some(consignee.copy(traderExciseNumber = Some(dataRequest.ern)))
      case (_, consignee) =>
        consignee
    }
  }

  def apply()(implicit dataRequest: DataRequest[_], appConfig: AppConfig): SubmitAlertOrRejectionModel = {

    SubmitAlertOrRejectionModel(
      consigneeTrader = consigneeTraderDetails(dataRequest.movementDetails),
      exciseMovement = ExciseMovementModel(
        arc = dataRequest.movementDetails.arc,
        sequenceNumber = dataRequest.movementDetails.sequenceNumber
      ),
      destinationOffice = destinationOfficePrefix + appConfig.destinationOfficeSuffix,
      dateOfAlertOrRejection = LocalDate.now,
      isRejected = isRejectedSubmission,
      alertOrRejectionReasons =
        Some(
          mandatoryPage(SelectReasonPage).map { reason =>
            AlertOrRejectionReasonModel(
              reason,
              reason match {
                case ConsigneeDetailsWrong => dataRequest.userAnswers.get(ConsigneeInformationPage).flatten
                case GoodTypesNotMatchOrder => dataRequest.userAnswers.get(GoodsTypeInformationPage).flatten
                case QuantitiesNotMatchOrder => dataRequest.userAnswers.get(GoodsQuantitiesInformationPage).flatten
                case Other => dataRequest.userAnswers.get(GiveInformationPage).flatten
              }
            )
          }.toSeq
        )
    )
  }
}
