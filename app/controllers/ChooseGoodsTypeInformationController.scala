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
import forms.ChooseGoodsTypeInformationFormProvider
import models.Mode
import navigation.Navigator
import pages.{ChooseGoodsTypeInformationPage, GoodsTypeInformationPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.ChooseGoodsTypeInformationView

import javax.inject.Inject
import scala.concurrent.Future

class ChooseGoodsTypeInformationController @Inject()(override val messagesApi: MessagesApi,
                                                     override val userAnswersService: UserAnswersService,
                                                     override val navigator: Navigator,
                                                     override val auth: AuthAction,
                                                     override val withMovement: MovementAction,
                                                     override val getData: DataRetrievalAction,
                                                     override val requireData: DataRequiredAction,
                                                                   formProvider: ChooseGoodsTypeInformationFormProvider,
                                                     val controllerComponents: MessagesControllerComponents,
                                                     view: ChooseGoodsTypeInformationView
                                                    ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovement(ern, arc) { implicit request =>
      Ok(view(fillForm(ChooseGoodsTypeInformationPage, formProvider()), mode))
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        {
          case true =>
            saveAndRedirect(ChooseGoodsTypeInformationPage, true, mode)
          case false =>
            val removedMoreInfo = request.userAnswers.set(GoodsTypeInformationPage, None)
            saveAndRedirect(ChooseGoodsTypeInformationPage, false, removedMoreInfo, mode)
        }
      )
    }
}
