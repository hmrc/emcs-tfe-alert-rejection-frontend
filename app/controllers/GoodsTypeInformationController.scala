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
import forms.GiveInformationFormProvider
import models.Mode
import navigation.Navigator
import pages.{ChooseGoodsTypeInformationPage, GoodsTypeInformationPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import utils.JsonOptionFormatter.optionFormat
import utils.Logging
import views.html.GoodsTypeInformationView

import javax.inject.Inject
import scala.concurrent.Future

class GoodsTypeInformationController @Inject()(override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val navigator: Navigator,
                                                   override val auth: AuthAction,
                                                   override val userAllowList: UserAllowListAction,
                                                   override val withMovement: MovementAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   formProvider: GiveInformationFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: GoodsTypeInformationView
                                                  ) extends BaseNavigationController with AuthActionHelper with Logging {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) {
      implicit request =>
        withAnswer(ChooseGoodsTypeInformationPage) {
          _ =>
            Future.successful(
              Ok(view(
                fillForm(GoodsTypeInformationPage, formProvider(isMandatory = false)),
                routes.GoodsTypeInformationController.onSubmit(ern, arc, mode)
              ))
            )
        }
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) {
      implicit request =>
        withAnswer(ChooseGoodsTypeInformationPage) {
          _ =>
            formProvider(isMandatory = false).bindFromRequest().fold(
              formWithErrors =>
                Future.successful(
                  BadRequest(view(formWithErrors, routes.GoodsTypeInformationController.onSubmit(ern, arc, mode)))
                ),
              value => saveAndRedirect(GoodsTypeInformationPage, value, mode)
            )
        }
    }
}
