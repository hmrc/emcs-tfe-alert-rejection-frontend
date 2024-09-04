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

package controllers

import controllers.actions._
import forms.SelectReasonFormProvider
import models.SelectReason.{ConsigneeDetailsWrong, GoodTypesNotMatchOrder, Other, QuantitiesNotMatchOrder}
import models.requests.DataRequest
import models.{Mode, SelectReason, UserAnswers}
import navigation.Navigator
import pages._
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.SelectReasonView

import javax.inject.Inject
import scala.concurrent.Future

class SelectReasonController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        override val userAnswersService: UserAnswersService,
                                        override val navigator: Navigator,
                                        override val auth: AuthAction,
                                        override val withMovement: MovementAction,
                                        override val getData: DataRetrievalAction,
                                        override val requireData: DataRequiredAction,
                                         formProvider: SelectReasonFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: SelectReasonView
                                      ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAnswer(SelectAlertRejectPage) { alertOrRejectAnswer =>
        renderView(Ok, fillForm(SelectReasonPage, formProvider(alertOrRejectAnswer)), mode)
      }
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAnswer(SelectAlertRejectPage) { alertOrRejectAnswer =>
        formProvider(alertOrRejectAnswer).bindFromRequest().fold(
          formWithErrors =>
            renderView(BadRequest, formWithErrors, mode),
          values =>
            saveAndRedirect(SelectReasonPage, values, cleanseAnswers(values), mode)
        )
      }
    }

  private def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    withAnswer(SelectAlertRejectPage) { alertOrRejectAnswer =>
      Future.successful(status(view(form, alertOrRejectAnswer, routes.SelectReasonController.onSubmit(request.ern, request.arc, mode))))
    }

  private def cleanseAnswers(values: Set[SelectReason])(implicit request: DataRequest[_]): UserAnswers =
    cleanseUserAnswersIfValueHasChanged(
      page = SelectReasonPage,
      newAnswer = values,
      cleansingFunction = {
        val allOptionsNotChecked: Seq[SelectReason] = SelectReason.values.filterNot(values.contains)

        allOptionsNotChecked.foldLeft(request.userAnswers) {
          case (answers, ConsigneeDetailsWrong) =>
            answers
              .remove(ChooseConsigneeInformationPage)
              .remove(ConsigneeInformationPage)
          case (answers, GoodTypesNotMatchOrder) =>
            answers
              .remove(ChooseGoodsTypeInformationPage)
              .remove(GoodsTypeInformationPage)
          case (answers, QuantitiesNotMatchOrder) =>
            answers
              .remove(ChooseGoodsQuantitiesInformationPage)
              .remove(GoodsQuantitiesInformationPage)
          case (answers, Other) =>
            answers
              .remove(GiveInformationPage)
          case (answers, _) => answers
        }
      }
    )
}
