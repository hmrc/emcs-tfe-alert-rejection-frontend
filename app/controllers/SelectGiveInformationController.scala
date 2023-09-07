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
import forms.SelectGiveInformationFormProvider
import models.{Mode, NormalMode, SelectAlertReject}
import models.requests.DataRequest
import navigation.Navigator
import pages.{SelectAlertRejectPage, SelectGiveInformationPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.SelectGiveInformationView

import javax.inject.Inject
import scala.concurrent.Future

class SelectGiveInformationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val withMovement: MovementAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val userAllowList: UserAllowListAction,
                                       formProvider: SelectGiveInformationFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: SelectGiveInformationView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAnswer(SelectAlertRejectPage) { alertOrReject =>
        Future(renderView(Ok, alertOrReject, fillForm(SelectGiveInformationPage, formProvider(alertOrReject)), mode))
      }
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAnswer(SelectAlertRejectPage) { alertOrReject =>
        formProvider(alertOrReject).bindFromRequest().fold(
          formWithErrors =>
            Future(renderView(BadRequest, alertOrReject, formWithErrors, mode)),
          value =>
            saveAndRedirect(SelectGiveInformationPage, value, mode)
        )
      }
    }

  private def renderView(status: Status, alertOrReject: SelectAlertReject, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Result =
    status(view(
      alertOrReject = alertOrReject,
      form = form,
      onSubmitCall = routes.SelectGiveInformationController.onSubmit(request.ern, request.arc, mode)
    ))
}
