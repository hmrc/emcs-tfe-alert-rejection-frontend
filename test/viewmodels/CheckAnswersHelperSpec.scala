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

package viewmodels

import base.SpecBase
import mocks.viewmodels._
import models.SelectAlertReject.{Alert, Reject}
import models.SelectReason.{ConsigneeDetailsWrong, GoodTypesNotMatchOrder, Other, QuantitiesNotMatchOrder}
import models.{CheckMode, SelectReason}
import pages._
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.CheckAnswersHelper
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class CheckAnswersHelperSpec
  extends SpecBase
  with MockSelectAlertRejectPageSummary
  with MockSelectReasonSummary
  with MockInformationSummary {

  lazy val checkAnswersHelper = new CheckAnswersHelper(
    mockSelectAlertRejectPageSummary,
    mockSelectReasonSummary,
    mockInformationSummary
  )()

  lazy val app = applicationBuilder().build()

  implicit lazy val msgs = messages(app)

  "CheckAnswersHelper" - {
      Seq(Alert,Reject).foreach { aType =>

        s"being rendered for a $aType" - {

          "with all fields present" - {

            implicit lazy val request = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(SelectAlertRejectPage, aType)
                .set(SelectReasonPage, SelectReason.values)
            )

            "must render the expected SummaryList" in {

              val alertOrRejection = SummaryListRow("AlertOrRejection", ValueViewModel(aType.toString))
              val reason = SummaryListRow("Reason", ValueViewModel("some details are wrong"))
              val consigneeInformation = SummaryListRow("ConsigneeInformation", ValueViewModel("ConsigneeInformation here"))
              val goodsTypesInformation = SummaryListRow("GoodsTypesInformation", ValueViewModel("GoodsTypesInformation here"))
              val goodsQuantitiesInformation = SummaryListRow("GoodsQuantitiesInformation", ValueViewModel("GoodsQuantitiesInformation here"))
              val otherInformation = SummaryListRow("OtherInformation", ValueViewModel("OtherInformation here"))

              MockSelectAlertRejectPageSummary.row(aType).returns(Some(alertOrRejection))
              MockSelectReasonSummary.row(aType).returns(Some(reason))
              MockInformationSummary.row(
                ConsigneeInformationPage,
                controllers.routes.ConsigneeInformationController.onPageLoad(testErn, testArc, CheckMode),
                None
              ).returns(consigneeInformation)
              MockInformationSummary.row(
                GoodsTypeInformationPage,
                testOnly.controllers.routes.UnderConstructionController.onPageLoad(), // TODO once page built
                None
              ).returns(goodsTypesInformation)
              MockInformationSummary.row(
                GoodsQuantitiesInformationPage,
                testOnly.controllers.routes.UnderConstructionController.onPageLoad(), // TODO once page built
                None
              ).returns(goodsQuantitiesInformation)
              MockInformationSummary.row(
                GiveInformationPage,
                controllers.routes.GiveInformationController.onPageLoad(testErn, testArc, CheckMode),
                Some(s"checkYourAnswers.giveInformation.$aType.label")
              ).returns(otherInformation)

              checkAnswersHelper.summaryList(aType) mustBe SummaryList(Seq(
                alertOrRejection,
                reason,
                consigneeInformation,
                goodsTypesInformation,
                goodsQuantitiesInformation,
                otherInformation
              ))
            }

          }

          Seq(
            (ConsigneeDetailsWrong, ConsigneeInformationPage),
            (GoodTypesNotMatchOrder, GoodsTypeInformationPage),
            (QuantitiesNotMatchOrder, GoodsQuantitiesInformationPage),
            (Other, GiveInformationPage)
          ).foreach {
            case (forReason, page) =>

            s"for just ${forReason.getClass.getSimpleName.stripSuffix("$")}" - {

              implicit lazy val request = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(SelectAlertRejectPage, aType)
                  .set(SelectReasonPage, Seq(forReason))
              )

              "must render the expected SummaryList" in {

                val alertOrRejection = SummaryListRow("AlertOrRejection", ValueViewModel(aType.toString))
                val reason = SummaryListRow("Reason", ValueViewModel("some details are wrong"))
                val information = SummaryListRow(s"${aType}Information", ValueViewModel("click here to fill in"))

                MockSelectAlertRejectPageSummary.row(aType).returns(Some(alertOrRejection))
                MockSelectReasonSummary.row(aType).returns(Some(reason))
                MockInformationSummary.row(
                  page,
                  forReason match {
                    case ConsigneeDetailsWrong =>
                      controllers.routes.ConsigneeInformationController.onPageLoad(testErn, testArc, CheckMode)
                    case GoodTypesNotMatchOrder =>
                      testOnly.controllers.routes.UnderConstructionController.onPageLoad()
                    case QuantitiesNotMatchOrder =>
                      testOnly.controllers.routes.UnderConstructionController.onPageLoad()
                    case Other =>
                      controllers.routes.GiveInformationController.onPageLoad(testErn, testArc, CheckMode)
                  },
                  forReason match {
                    case Other =>
                      Some(s"checkYourAnswers.giveInformation.$aType.label")
                    case _ =>
                      None
                  }
                ).returns(information)

                checkAnswersHelper.summaryList(aType) mustBe SummaryList(Seq(
                  alertOrRejection,
                  reason,
                  information
                ))
              }

            }
          }

        }

      }
  }

}
