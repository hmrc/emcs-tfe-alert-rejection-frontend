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
import models.SelectReason.{ConsigneeDetailsWrong, GoodTypesNotMatchOrder, Other, QuantitiesNotMatchOrder}
import models.{Mode, NormalMode, SelectReason, UserAnswers}
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
      redirectToNextWrongPage()(userAnswers)

    case ChooseConsigneeInformationPage => (userAnswers: UserAnswers) =>
        userAnswers.get(ChooseConsigneeInformationPage) match {
          case Some(true) =>
            controllers.routes.ConsigneeInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(false) => redirectToNextWrongPage(Some(ConsigneeDetailsWrong))(userAnswers)
          case _ => routes.ChooseConsigneeInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        }

    case ConsigneeInformationPage => (userAnswers: UserAnswers) =>
      redirectToNextWrongPage(Some(ConsigneeDetailsWrong))(userAnswers)

    case ChooseGoodsTypeInformationPage => (userAnswers: UserAnswers) =>
      userAnswers.get(ChooseGoodsTypeInformationPage) match {
        case Some(true) =>
          routes.GoodsTypeInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

        case Some(false) => redirectToNextWrongPage(Some(GoodTypesNotMatchOrder))(userAnswers)
        case _ => routes.ChooseGoodsTypeInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
      }

    case ChooseGoodsQuantitiesInformationPage => (userAnswers: UserAnswers) =>
      userAnswers.get(ChooseGoodsQuantitiesInformationPage) match {
        case Some(true) =>
          // TODO route to goods quantities information page AR08 when finished
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()

        case Some(false) => redirectToNextWrongPage(Some(QuantitiesNotMatchOrder))(userAnswers)
        case _ => routes.ChooseGoodsQuantitiesInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
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

    case GoodsTypeInformationPage => (userAnswers: UserAnswers) =>
      redirectToNextWrongPage(Some(GoodTypesNotMatchOrder))(userAnswers)


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

  private[navigation] def redirectToNextWrongPage(lastOptionAnswered: Option[SelectReason] = None)(implicit userAnswers: UserAnswers): Call =
    userAnswers.get(SelectReasonPage) match {
      case Some(selectedOptions) =>
        nextWrongOptionToAnswer(selectedOptions, lastOptionAnswered) match {
          case Some(ConsigneeDetailsWrong) =>
            controllers.routes.ChooseConsigneeInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(GoodTypesNotMatchOrder) =>
            controllers.routes.ChooseGoodsTypeInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(QuantitiesNotMatchOrder) =>
            controllers.routes.ChooseGoodsQuantitiesInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(Other) =>
            controllers.routes.GiveInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
          case None =>
            // TODO route to CYA page when finished
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      case _ =>
        routes.SelectReasonController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
    }

  private[navigation] def nextWrongOptionToAnswer(selectedOptions: Set[SelectReason],
                                                              lastOption: Option[SelectReason] = None): Option[SelectReason] = {
    val orderedSetOfOptions = SelectReason.values.filter(selectedOptions.contains)
    lastOption match {
      case Some(value) if orderedSetOfOptions.lastOption.contains(value) =>
        None
      case Some(value) =>
        Some(orderedSetOfOptions(orderedSetOfOptions.indexOf(value) + 1))
      case None =>
        orderedSetOfOptions.headOption
    }
  }
}
