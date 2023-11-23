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

import base.SpecBase
import config.AppConfig
import models.DestinationType.TemporaryRegisteredConsignee
import models.SelectAlertReject.{Alert, Reject}
import models.common.ExciseMovementModel
import models.response.emcsTfe.{GetMovementResponse, TraderModel}
import models.{DestinationOffice, DestinationType, MissingMandatoryPage, SelectReason}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import pages._
import play.api.test.FakeRequest

import java.time.LocalDate

class SubmitAlertOrRejectionModelSpec extends SpecBase {

  lazy val mockAppConfig = mock[AppConfig]

  "SubmitAlertOrRejectionModel" - {

    "calling .destinationOfficePrefix(_: DataRequest[_])" - {

      val GB_ID = "GB123123123"
      val XI_ID = "XI123123123"

      val DESTINATION_OFFICE_PREFIX_GB = DestinationOffice.GreatBritain.toString
      val DESTINATION_OFFICE_PREFIX_XI = DestinationOffice.NorthernIreland.toString

      s"must return $DESTINATION_OFFICE_PREFIX_GB" - {
        s"when logged in user ERN starts with $DESTINATION_OFFICE_PREFIX_GB" in {
          val userAnswers = emptyUserAnswers.copy(ern = GB_ID)
          val dr = dataRequest(FakeRequest(), userAnswers)

          SubmitAlertOrRejectionModel.destinationOfficePrefix(dr) mustBe DESTINATION_OFFICE_PREFIX_GB
        }
        s"when logged in user ERN starts with $DESTINATION_OFFICE_PREFIX_XI and they select DestinationOfficePage to ${DestinationOffice.GreatBritain}" in {
          val userAnswers = emptyUserAnswers.copy(ern = XI_ID).set(DestinationOfficePage, DestinationOffice.GreatBritain)
          val dr = dataRequest(FakeRequest(), userAnswers)

          SubmitAlertOrRejectionModel.destinationOfficePrefix(dr) mustBe DESTINATION_OFFICE_PREFIX_GB
        }
        s"when logged in user ERN doesn't start with $DESTINATION_OFFICE_PREFIX_GB or $DESTINATION_OFFICE_PREFIX_XI (default case)" in {
          val userAnswers = emptyUserAnswers.copy(ern = testErn)
          val dr = dataRequest(FakeRequest(), userAnswers)

          SubmitAlertOrRejectionModel.destinationOfficePrefix(dr) mustBe DESTINATION_OFFICE_PREFIX_GB
        }
      }
      s"must return $DESTINATION_OFFICE_PREFIX_XI" - {
        s"when logged in user ERN starts with $DESTINATION_OFFICE_PREFIX_XI and they select DestinationOfficePage to ${DestinationOffice.NorthernIreland}" in {
          val userAnswers = emptyUserAnswers.copy(ern = XI_ID).set(DestinationOfficePage, DestinationOffice.NorthernIreland)
          val dr = dataRequest(FakeRequest(), userAnswers)

          SubmitAlertOrRejectionModel.destinationOfficePrefix(dr) mustBe DESTINATION_OFFICE_PREFIX_XI
        }
      }
      "must throw a MissingMandatoryPage" - {
        s"when logged in user ERN starts with $DESTINATION_OFFICE_PREFIX_XI and they have not answered the DestinationOfficePage question" in {
          val userAnswers = emptyUserAnswers.copy(ern = XI_ID)
          val dr = dataRequest(FakeRequest(), userAnswers)

          intercept[MissingMandatoryPage](SubmitAlertOrRejectionModel.destinationOfficePrefix(dr))
        }
      }
    }


    "calling .isRejectedSubmission(_: DataRequest[_])" - {
      "must throw a MissingMandatoryPage" - {
        s"when logged in user has not answered the SelectAlertRejectPage question" in {
          val userAnswers = emptyUserAnswers
          val dr = dataRequest(FakeRequest(), userAnswers)

          intercept[MissingMandatoryPage](SubmitAlertOrRejectionModel.isRejectedSubmission(dr))
        }
      }
      "must return true when this is a rejection" in {
        val userAnswers = emptyUserAnswers.set(SelectAlertRejectPage, Reject)
        val dr = dataRequest(FakeRequest(), userAnswers)

        SubmitAlertOrRejectionModel.isRejectedSubmission(dr) mustBe true
      }
      "must return false when this is a alert" in {
        val userAnswers = emptyUserAnswers.set(SelectAlertRejectPage, Alert)
        val dr = dataRequest(FakeRequest(), userAnswers)

        SubmitAlertOrRejectionModel.isRejectedSubmission(dr) mustBe false
      }
    }


    "calling .consigneeTraderDetails(_: GetMovementResponse)" - {
      implicit val userAnswers = emptyUserAnswers

      "when the destination type = TemporaryRegisteredConsignee" - {
        "must replace the consignee's ERN of the movement with the logged in consignee's ERN" in {
          implicit val dr = dataRequest(FakeRequest(), emptyUserAnswers)

          val aMovement: GetMovementResponse = getMovementResponseModel.copy(
            destinationType = TemporaryRegisteredConsignee,
            consigneeTrader = Some(
              TraderModel(
                traderExciseNumber = Some("TCA1234567890"),
                traderName = None,
                address = None,
                eoriNumber = None
              )
            )
          )

          SubmitAlertOrRejectionModel.consigneeTraderDetails(aMovement) mustBe Some(TraderModel(Some(testErn), None, None, None))
        }
      }

      DestinationType.values.filterNot(_ == TemporaryRegisteredConsignee).foreach { destinationType =>
        s"when the destinationType = ${destinationType.getClass.getSimpleName.stripSuffix("$")}" - {
          "must NOT replace the consignee's ERN of the movement with the logged in consignee's ERN" in {
            implicit val dr = dataRequest(FakeRequest(), emptyUserAnswers)

            val aMovement: GetMovementResponse = getMovementResponseModel.copy(
              destinationType = destinationType,
              consigneeTrader = Some(
                TraderModel(
                  traderExciseNumber = Some("TCA1234567890"),
                  traderName = None,
                  address = None,
                  eoriNumber = None
                )
              )
            )

            SubmitAlertOrRejectionModel.consigneeTraderDetails(aMovement) mustBe aMovement.consigneeTrader
          }
        }

      }
    }

    "calling .apply()(_: DataRequest[_], _: AppConfig)" - {

      "must construct from UserAnswers correctly" in {

        when(mockAppConfig.destinationOfficeSuffix).thenReturn("004098")

        val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, SelectReason.values)
            .set(ChooseConsigneeInformationPage, true)
            .set(ConsigneeInformationPage, Some("a"))
            .set(ChooseGoodsTypeInformationPage, true)
            .set(GoodsTypeInformationPage, Some("b"))
            .set(ChooseGoodsQuantitiesInformationPage, true)
            .set(GoodsQuantitiesInformationPage, Some("c"))
            .set(GiveInformationPage, Some("d"))

        implicit val dr = dataRequest(FakeRequest(), userAnswers)

       SubmitAlertOrRejectionModel()(dr, mockAppConfig) mustBe SubmitAlertOrRejectionModel(
          consigneeTrader = getMovementResponseModel.consigneeTrader,
          exciseMovement = ExciseMovementModel(testArc, 1),
          destinationOffice = "GB004098",
          dateOfAlertOrRejection = LocalDate.now,
          isRejected = false,
          alertOrRejectionReasons = Some(
            List(
              AlertOrRejectionReasonModel(SelectReason.ConsigneeDetailsWrong, Some("a")),
              AlertOrRejectionReasonModel(SelectReason.GoodTypesNotMatchOrder, Some("b")),
              AlertOrRejectionReasonModel(SelectReason.QuantitiesNotMatchOrder, Some("c")),
              AlertOrRejectionReasonModel(SelectReason.Other, Some("d"))
            )
          )
        )

      }
    }

  }

}
