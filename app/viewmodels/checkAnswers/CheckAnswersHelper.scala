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

package viewmodels.checkAnswers

import models.SelectReason.{ConsigneeDetailsWrong, GoodTypesNotMatchOrder, Other, QuantitiesNotMatchOrder}
import models.requests.DataRequest
import models.{CheckMode, SelectAlertReject}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}

import javax.inject.Inject

class CheckAnswersHelper @Inject()(
                                    selectAlertRejectPageSummary: SelectAlertRejectPageSummary,
                                    selectReasonSummary: SelectReasonSummary,
                                    moreInformationSummary: InformationSummary
                                  )() {

  def summaryList(alertOrReject: SelectAlertReject)(implicit request: DataRequest[_], messages: Messages): SummaryList = {

    val commonRows = Seq(
      selectAlertRejectPageSummary.row(alertOrReject),
      selectReasonSummary.row(alertOrReject)
    ).flatten

    val whatsWrongInformationRows = Seq(
      consigneeInformation,
      goodsTypesInformation,
      goodsQuantitiesInformation,
      otherInformation(alertOrReject)
    ).flatten

    SummaryList(
      rows = commonRows ++ whatsWrongInformationRows
    )
  }

  private def consigneeInformation()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    if (request.userAnswers.get(SelectReasonPage).exists(_.contains(ConsigneeDetailsWrong))) {
      Some(moreInformationSummary.row(
        page = ConsigneeInformationPage,
        changeAction = controllers.routes.ConsigneeInformationController.onPageLoad(request.ern, request.arc, CheckMode)
      ))
    } else {
      None
    }

  private def goodsTypesInformation()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    if (request.userAnswers.get(SelectReasonPage).exists(_.contains(GoodTypesNotMatchOrder))) {
      Some(moreInformationSummary.row(
        page = GoodsTypeInformationPage,
        changeAction = testOnly.controllers.routes.UnderConstructionController.onPageLoad() // TODO once page built
      ))
    } else {
      None
    }

  private def goodsQuantitiesInformation()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    if (request.userAnswers.get(SelectReasonPage).exists(_.contains(QuantitiesNotMatchOrder))) {
      Some(moreInformationSummary.row(
        page = GoodsQuantitiesInformationPage,
        changeAction = testOnly.controllers.routes.UnderConstructionController.onPageLoad() // TODO once page built
      ))
    } else {
      None
    }

  private def otherInformation(alertOrReject: SelectAlertReject)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    if (request.userAnswers.get(SelectReasonPage).exists(_.contains(Other))) {
      Some(moreInformationSummary.row(
        page = GiveInformationPage,
        changeAction = controllers.routes.GiveInformationController.onPageLoad(request.ern, request.arc, CheckMode),
        keyOverride = Some(s"checkYourAnswers.$GiveInformationPage.$alertOrReject.label")
      ))
    } else {
      None
    }

}
