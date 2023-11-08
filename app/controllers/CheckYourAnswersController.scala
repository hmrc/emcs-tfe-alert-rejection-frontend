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
import models.requests.DataRequest
import models.{NormalMode, SelectAlertReject}
import navigation.Navigator
import pages.{SelectAlertRejectPage, SelectReasonPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import viewmodels.checkAnswers.CheckAnswersHelper
import views.html.CheckYourAnswersView

import javax.inject.Inject
import scala.concurrent.Future

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            override val userAnswersService: UserAnswersService,
                                            override val navigator: Navigator,
                                            override val auth: AuthAction,
                                            override val withMovement: MovementAction,
                                            override val getData: DataRetrievalAction,
                                            override val requireData: DataRequiredAction,
                                            override val userAllowList: UserAllowListAction,
                                            val controllerComponents: MessagesControllerComponents,
                                            checkAnswersHelper: CheckAnswersHelper,
                                            view: CheckYourAnswersView
                                          ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withGuard {
        case alertOrReject =>
          renderView(alertOrReject)
      }
    }

  def onPageSubmit(ern: String, arc: String): Action[AnyContent] = {
    onPageLoad(ern, arc) // TODO on a different ticket
  }

  private def withGuard(f: SelectAlertReject => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    (request.userAnswers.get(SelectAlertRejectPage), request.userAnswers.get(SelectReasonPage)) match {
      case (None, _) =>
        Future.successful(
          Redirect(controllers.routes.SelectAlertRejectPageController.onPageLoad(request.ern, request.arc, NormalMode))
        )
      case (_, None) =>
        Future.successful(
          Redirect(controllers.routes.SelectReasonController.onPageLoad(request.ern, request.arc, NormalMode))
        )
      case (Some(alertOrReject), Some(_)) =>
        f(alertOrReject)
    }


  private def renderView(alertOrReject: SelectAlertReject)(implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      Ok(
        view(
          alertOrReject,
          checkAnswersHelper.summaryList(alertOrReject),
          controllers.routes.CheckYourAnswersController.onPageSubmit(request.ern, request.arc)
        )
      )
    )
  }

}


