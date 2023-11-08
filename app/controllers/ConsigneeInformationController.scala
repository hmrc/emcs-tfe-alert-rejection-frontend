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
import forms.ConsigneeInformationFormProvider
import models.Mode
import navigation.Navigator
import pages.{ChooseConsigneeInformationPage, ConsigneeInformationPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import utils.JsonOptionFormatter.optionFormat
import views.html.ConsigneeInformationView

import javax.inject.Inject
import scala.concurrent.Future

class ConsigneeInformationController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                override val userAnswersService: UserAnswersService,
                                                override val navigator: Navigator,
                                                override val auth: AuthAction,
                                                override val withMovement: MovementAction,
                                                override val getData: DataRetrievalAction,
                                                override val requireData: DataRequiredAction,
                                                override val userAllowList: UserAllowListAction,
                                                formProvider: ConsigneeInformationFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: ConsigneeInformationView
                                              ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
          withAnswer(ChooseConsigneeInformationPage) { _ =>
            Future(Ok(view(
              fillForm(ConsigneeInformationPage, formProvider()),
              routes.ConsigneeInformationController.onSubmit(ern, arc, mode)
            )))
      }
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
        withAnswer(ChooseConsigneeInformationPage) { _ =>
          formProvider().bindFromRequest().fold(
            formWithErrors =>
              Future.successful(BadRequest(view(
                formWithErrors,
                routes.ConsigneeInformationController.onSubmit(ern, arc, mode)
              ))),
            value =>
              saveAndRedirect(
                ConsigneeInformationPage,
                value,
                request.userAnswers.set(ChooseConsigneeInformationPage, true),
                mode
              )
          )
        }
    }
}
