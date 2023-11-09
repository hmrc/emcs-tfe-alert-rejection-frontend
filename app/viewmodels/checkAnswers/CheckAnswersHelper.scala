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
import models.{CheckMode, ConfirmationDetails, SelectAlertReject, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}

import javax.inject.Inject

class CheckAnswersHelper @Inject()(
                                    administrativeReferenceCodeSummary: AdministrativeReferenceCodeSummary,
                                    selectAlertRejectPageSummary: SelectAlertRejectPageSummary,
                                    selectReasonSummary: SelectReasonSummary,
                                    moreInformationSummary: InformationSummary
                                  )() {

  def summaryList(alertOrReject: SelectAlertReject)(implicit request: DataRequest[_], messages: Messages): SummaryList = {

    implicit val userAnswers: UserAnswers = request.userAnswers

    SummaryList(
      rows = Seq(
        selectAlertRejectPageSummary.row(alertOrReject, showChangeLinks = true),
        selectReasonSummary.row(alertOrReject, showChangeLinks = true),
        consigneeInformation(showChangeLinks = true),
        goodsTypesInformation(showChangeLinks = true),
        goodsQuantitiesInformation(showChangeLinks = true),
        otherInformation(alertOrReject, showChangeLinks = true)
      ).flatten
    )
  }

  def summaryList(confirmationDetails: ConfirmationDetails)(implicit request: DataRequest[_], messages: Messages): SummaryList = {

    implicit val userAnswers: UserAnswers = confirmationDetails.userAnswers

    val alertOrReject = userAnswers.get(SelectAlertRejectPage).get

    SummaryList(
      rows = Seq(
        administrativeReferenceCodeSummary.row(),
        selectAlertRejectPageSummary.row(alertOrReject, showChangeLinks = false),
        selectReasonSummary.row(alertOrReject, showChangeLinks = false),
        consigneeInformation(showChangeLinks = false),
        goodsTypesInformation(showChangeLinks = false),
        goodsQuantitiesInformation(showChangeLinks = false),
        otherInformation(alertOrReject, showChangeLinks = false)
      ).flatten
    )

  }

  private def consigneeInformation(showChangeLinks: Boolean)(implicit userAnswers: UserAnswers, messages: Messages): Option[SummaryListRow] =
    if (userAnswers.get(SelectReasonPage).exists(_.contains(ConsigneeDetailsWrong))) {
      Some(moreInformationSummary.row(
        page = ConsigneeInformationPage,
        changeAction = controllers.routes.ConsigneeInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, CheckMode),
        showChangeLinks = showChangeLinks
      ))
    } else {
      None
    }

  private def goodsTypesInformation(showChangeLinks: Boolean)(implicit userAnswers: UserAnswers, messages: Messages): Option[SummaryListRow] =
    if (userAnswers.get(SelectReasonPage).exists(_.contains(GoodTypesNotMatchOrder))) {
      Some(moreInformationSummary.row(
        page = GoodsTypeInformationPage,
        changeAction = controllers.routes.GoodsTypeInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, CheckMode),
        showChangeLinks = showChangeLinks
      ))
    } else {
      None
    }

  private def goodsQuantitiesInformation(showChangeLinks: Boolean)(implicit userAnswers: UserAnswers, messages: Messages): Option[SummaryListRow] =
    if (userAnswers.get(SelectReasonPage).exists(_.contains(QuantitiesNotMatchOrder))) {
      Some(moreInformationSummary.row(
        page = GoodsQuantitiesInformationPage,
        changeAction = controllers.routes.GoodsQuantitiesInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, CheckMode),
        showChangeLinks = showChangeLinks
      ))
    } else {
      None
    }

  private def otherInformation(alertOrReject: SelectAlertReject, showChangeLinks: Boolean)
                              (implicit userAnswers: UserAnswers, messages: Messages): Option[SummaryListRow] =
    if (userAnswers.get(SelectReasonPage).exists(_.contains(Other))) {
      Some(moreInformationSummary.row(
        page = GiveInformationPage,
        changeAction = controllers.routes.GiveInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, CheckMode),
        keyOverride = Some(s"checkYourAnswers.$GiveInformationPage.$alertOrReject.label"),
        showChangeLinks = showChangeLinks
      ))
    } else {
      None
    }

}
