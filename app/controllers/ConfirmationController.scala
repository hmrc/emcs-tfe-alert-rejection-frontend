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
import handlers.ErrorHandler
import models.ConfirmationDetails
import navigation.Navigator
import pages.{ConfirmationPage, SelectAlertRejectPage, SelectReasonPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.ConfirmationView

import javax.inject.Inject

class ConfirmationController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        override val userAnswersService: UserAnswersService,
                                        override val navigator: Navigator,
                                        override val auth: AuthAction,
                                        override val withMovement: MovementAction,
                                        override val getData: DataRetrievalAction,
                                        override val requireData: DataRequiredAction,
                                        override val userAllowList: UserAllowListAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: ConfirmationView,
                                        errorHandler: ErrorHandler
                                      ) extends BaseNavigationController with AuthActionHelper with I18nSupport {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =

    authorisedDataRequestWithCachedMovement(ern, arc) { implicit request =>
      request.userAnswers.get(ConfirmationPage) match {
        case Some(confirmationDetails: ConfirmationDetails) =>
          confirmationDetails.userAnswers.get(SelectAlertRejectPage).foreach {
            selectType =>
              logger.info(s"[onPageLoad] Alert/rejection type: [$selectType]")
          }
          confirmationDetails.userAnswers.get(SelectReasonPage).foreach {
            reasons =>
              reasons.map {
                reason =>
                  logger.info(s"[onPageLoad] Alert/rejection reason: [${messagesApi.preferred(request).apply(s"selectReason.$reason")}]")
              }
          }
          Ok(view(confirmationDetails))
        case None =>
          logger.warn("[onPageLoad] Could not retrieve submission receipt reference from User session")
          BadRequest(errorHandler.badRequestTemplate)
      }
    }
}
