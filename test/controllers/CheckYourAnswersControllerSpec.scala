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

import base.SpecBase
import handlers.ErrorHandler
import mocks.services.{MockSubmissionService, MockUserAnswersService}
import mocks.viewmodels.MockCheckAnswersHelper
import models.SelectAlertReject.{Alert, Reject}
import models.response.emcsTfe.SubmissionResponse
import models.{ConfirmationDetails, MissingMandatoryPage, NormalMode, SelectReason, SubmitAlertOrRejectionException, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages._
import play.api.inject
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{SubmissionService, UserAnswersService}
import viewmodels.checkAnswers.CheckAnswersHelper
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase
  with MockCheckAnswersHelper
  with MockUserAnswersService
  with MockSubmissionService
  with SummaryListFluency {

  class Fixture(val userAnswers: Option[UserAnswers]) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          inject.bind[CheckAnswersHelper].toInstance(mockCheckAnswersHelper),
          inject.bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
          inject.bind[UserAnswersService].toInstance(mockUserAnswersService),
          inject.bind[SubmissionService].toInstance(mockSubmissionService)
        )
        .build()

    lazy val errorHandler = application.injector.instanceOf[ErrorHandler]
    val view = application.injector.instanceOf[CheckYourAnswersView]
  }

  def onwardRoute = Call("GET", "/foo")

  "CheckYourAnswers Controller" - {

    ".onPageLoad" - {

      Seq(Alert, Reject).foreach { aType =>
        s"for a $aType" - {

          def request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(testErn, testArc).url)

          val userAnswersSoFar = emptyUserAnswers
            .set(SelectAlertRejectPage, aType)
            .set(SelectReasonPage, Seq())

          val list = SummaryListViewModel(Seq.empty)

          "must return OK and the correct view for a GET" in new Fixture(Some(userAnswersSoFar)) {
            running(application) {
              MockCheckAnswersHelper.summaryList(aType).returns(list)

              val result = route(application, request).value

              status(result) mustBe OK
              contentAsString(result) mustBe view(
                aType,
                list,
                controllers.routes.CheckYourAnswersController.onPageSubmit(testErn, testArc)
              )(dataRequest(request, userAnswersSoFar), messages(application)).toString()
            }
          }

          "must return a redirect when there is no answer for SelectAlertRejectPage" in new Fixture(Some(emptyUserAnswers)) {
            val result = route(application, request).value

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe Some(routes.SelectAlertRejectPageController.onPageLoad(testErn, testArc, NormalMode).url)
          }

          "must return a redirect when there is no answer for SelectReasonPage" in new Fixture(Some(emptyUserAnswers.set(SelectAlertRejectPage, aType))) {
            val result = route(application, request).value

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe Some(routes.SelectReasonController.onPageLoad(testErn, testArc, NormalMode).url)
          }

          "must return a redirect to the NotPermittedPageController when ConfirmationPage details are present" in
            new Fixture(Some(emptyUserAnswers.set(ConfirmationPage, ConfirmationDetails(UserAnswers(testErn, testArc))))) {
              val result = route(application, request).value

              status(result) mustBe SEE_OTHER
              redirectLocation(result) mustBe Some(routes.NotPermittedPageController.onPageLoad(testErn, testArc).url)
            }
        }
      }
    }

    ".onPageSubmit" - {

      Seq(Alert, Reject).foreach { aType =>
        s"for a $aType" - {

          lazy val postRequest = FakeRequest(POST, routes.CheckYourAnswersController.onPageSubmit(testErn, testArc).url)

          val userAnswersSoFar = emptyUserAnswers
            .set(SelectAlertRejectPage, aType)
            .set(SelectReasonPage, SelectReason.values)
            .set(ConsigneeInformationPage, Some("ConsigneeInformationPage free text"))
            .set(GoodsTypeInformationPage, Some("ConsigneeInformationPage free text"))
            .set(GoodsQuantitiesInformationPage, Some("GoodsQuantitiesInformationPage free text"))
            .set(GiveInformationPage, Some("GiveInformationPage free text"))

          "when the submission is successful" - {

            "must save the ConfirmationDetails and redirect to the onward route" in new Fixture(Some(userAnswersSoFar)) {
              running(application) {

                val successResponse = SubmissionResponse(receipt = testConfirmationReference, receiptDate = testReceiptDate)

                MockSubmissionService.submit(testErn, testArc, getMovementResponseModel, userAnswers.get)
                  .returns(Future.successful(successResponse))

                val updatedAnswers = userAnswers.get.set(ConfirmationPage, ConfirmationDetails(userAnswers = userAnswersSoFar))

                MockUserAnswersService.set().returns(Future.successful(updatedAnswers))

                val result = route(application, postRequest).value

                status(result) mustEqual SEE_OTHER
                redirectLocation(result) mustBe Some(onwardRoute.url)
              }
            }

          }

          "when the submission fails" - {

            "must render an internal server error" in new Fixture(Some(userAnswersSoFar)) {
              running(application) {

                MockSubmissionService.submit(testErn, testArc, getMovementResponseModel, userAnswers.get)
                  .returns(Future.failed(SubmitAlertOrRejectionException("some exception occurred")))

                val result = route(application, postRequest).value

                status(result) mustBe INTERNAL_SERVER_ERROR
                contentAsString(result) mustBe errorHandler.internalServerErrorTemplate(postRequest).toString()
              }
            }

            "when mandatory data is reported as missing" - {

              "must return BadRequest" in new Fixture(Some(userAnswersSoFar)) {
                running(application) {

                  MockSubmissionService.submit(testErn, testArc, getMovementResponseModel, userAnswersSoFar)
                    .returns(Future.failed(MissingMandatoryPage("bang")))

                  val result = route(application, postRequest).value

                  status(result) mustBe BAD_REQUEST
                  contentAsString(result) mustBe errorHandler.badRequestTemplate(postRequest).toString()
                }
              }
            }
          }

        }

      }
    }

  }
}
