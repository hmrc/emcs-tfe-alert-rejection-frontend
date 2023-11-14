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
import models.requests.DataRequest
import models.{ConfirmationDetails, MissingMandatoryPage, NormalMode, SelectAlertReject, UserAnswers}
import navigation.Navigator
import pages.{CheckYourAnswersPage, ConfirmationPage, SelectAlertRejectPage, SelectReasonPage}
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{SubmissionService, UserAnswersService}
import uk.gov.hmrc.http.HeaderCarrier
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
                                            val submissionService: SubmissionService,
                                            val controllerComponents: MessagesControllerComponents,
                                            checkAnswersHelper: CheckAnswersHelper,
                                            view: CheckYourAnswersView,
                                            errorHandler: ErrorHandler
                                          ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withGuard {
        case alertOrReject =>
          renderView(alertOrReject)
      }
    }

  def onPageSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>

      withGuard {
        case _ =>
          submissionService.submit(ern, arc).flatMap { response =>

            logger.debug(s"[onSubmit] response received from downstream service ${response.downstreamService}: ${response.receipt}")

            deleteDraftAndSetConfirmationFlow(request.ern, request.arc).map { _ =>
              Redirect(navigator.nextPage(CheckYourAnswersPage, NormalMode, request.userAnswers))
            }

          } recover {
            case _: MissingMandatoryPage =>
              BadRequest(errorHandler.badRequestTemplate)
            case _ =>
              InternalServerError(errorHandler.internalServerErrorTemplate)
          }

      }
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

  private def deleteDraftAndSetConfirmationFlow(
                                                ern: String,
                                                arc: String
                                               )(implicit hc: HeaderCarrier, request: DataRequest[_]): Future[UserAnswers] = {
    userAnswersService.set(
      UserAnswers(
        ern,
        arc,
        data = Json.obj(
          ConfirmationPage.toString -> ConfirmationDetails(request.userAnswers)
        )
      )
    )
  }

}


