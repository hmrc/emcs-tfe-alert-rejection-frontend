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
import forms.ChooseGoodsQuantitiesInformationFormProvider
import models.Mode
import navigation.Navigator
import pages.ChooseGoodsQuantitiesInformationPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.ChooseGoodsQuantitiesInformationView

import javax.inject.Inject
import scala.concurrent.Future

class ChooseGoodsQuantitiesInformationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val withMovement: MovementAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val userAllowList: UserAllowListAction,
                                       formProvider: ChooseGoodsQuantitiesInformationFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ChooseGoodsQuantitiesInformationView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovement(ern, arc) { implicit request =>
      Ok(view(fillForm(ChooseGoodsQuantitiesInformationPage, formProvider()), mode))
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value =>
          saveAndRedirect(ChooseGoodsQuantitiesInformationPage, value, mode)
      )
    }
}
