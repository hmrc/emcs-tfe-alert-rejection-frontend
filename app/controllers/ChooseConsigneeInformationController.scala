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
import forms.ChooseConsigneeInformationFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.{ChooseConsigneeInformationPage, ConsigneeInformationPage, SelectAlertRejectPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.ChooseConsigneeInformationView

import javax.inject.Inject
import scala.concurrent.Future

class ChooseConsigneeInformationController @Inject()(
                                                      override val messagesApi: MessagesApi,
                                                      override val userAnswersService: UserAnswersService,
                                                      override val navigator: Navigator,
                                                      override val auth: AuthAction,
                                                      override val withMovement: MovementAction,
                                                      override val getData: DataRetrievalAction,
                                                      override val requireData: DataRequiredAction,
                                                                     formProvider: ChooseConsigneeInformationFormProvider,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: ChooseConsigneeInformationView
                                                    ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] = {
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAnswer(SelectAlertRejectPage) { _ =>
        Future(renderView(Ok, fillForm(ChooseConsigneeInformationPage, formProvider()), mode))
      }
    }
  }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] = {
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAnswer(SelectAlertRejectPage) { _ =>
        formProvider().bindFromRequest().fold(
          formWithErrors =>
            Future(renderView(BadRequest, formWithErrors, mode)),
          {
            case true =>
              saveAndRedirect(ChooseConsigneeInformationPage, true, mode)
            case false =>
              val removedMoreInfo = request.userAnswers.set(ConsigneeInformationPage, None)
              saveAndRedirect(ChooseConsigneeInformationPage, false, removedMoreInfo, mode)
          }
        )
      }
    }
  }

  private def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Result =
    status(view(
      form = form,
      onSubmitCall = routes.ChooseConsigneeInformationController.onSubmit(request.ern, request.arc, mode)
    ))

}
