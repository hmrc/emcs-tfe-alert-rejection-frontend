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

package navigation

import controllers.routes
import models.SelectReason.{ConsigneeDetailsWrong, Other}
import models.{Mode, NormalMode, UserAnswers}
import pages._
import play.api.mvc.Call

import javax.inject.Inject

class Navigator @Inject()() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case SelectAlertRejectPage => (userAnswers: UserAnswers) =>
      if (userAnswers.isNorthernIrelandTrader) {
        routes.DestinationOfficeController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
      } else {
        routes.SelectReasonController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
      }

    case DestinationOfficePage => (userAnswers: UserAnswers) =>
      routes.SelectReasonController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

    case SelectReasonPage => (userAnswers: UserAnswers) =>
      userAnswers.get(SelectReasonPage) match {
        case Some(selectedOptions) if selectedOptions.contains(ConsigneeDetailsWrong) =>
          controllers.routes.ChooseConsigneeInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case Some(selectedOptions) if selectedOptions.contains(Other) =>
          controllers.routes.GiveInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case _ =>
          controllers.routes.SelectGiveInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
      }

    case ChooseConsigneeInformationPage => (userAnswers: UserAnswers) =>
      userAnswers.get(ChooseConsigneeInformationPage) match {
        case Some(true) =>
          // TODO route to consignee information page AR04 when finished
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        case _ =>
          // TODO route to CYA page when finished
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

    case SelectGiveInformationPage => (userAnswers: UserAnswers) =>
      userAnswers.get(SelectGiveInformationPage) match {
        case Some(true) =>
          controllers.routes.GiveInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case _ =>
          // TODO route to CYA page when finished
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

    case GiveInformationPage => (userAnswers: UserAnswers) =>
      // TODO route to CYA page when finished
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()


    case _ => (userAnswers: UserAnswers) =>
      routes.IndexController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private val checkRoutes: Page => UserAnswers => Call = {
    case _ => (userAnswers: UserAnswers) =>
      routes.IndexController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode => normalRoutes(page)(userAnswers)
    case _ => checkRoutes(page)(userAnswers)
  }
}
