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
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockCheckAnswersHelper
import models.SelectAlertReject.{Alert, Reject}
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.{SelectAlertRejectPage, SelectReasonPage}
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import viewmodels.checkAnswers.CheckAnswersHelper
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

class CheckYourAnswersControllerSpec extends SpecBase
  with MockCheckAnswersHelper
  with MockUserAnswersService
  with SummaryListFluency {

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          inject.bind[CheckAnswersHelper].toInstance(mockCheckAnswersHelper),
          inject.bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
          inject.bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    lazy val errorHandler = application.injector.instanceOf[ErrorHandler]
    val view = application.injector.instanceOf[CheckYourAnswersView]
  }

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
        }
      }
    }
  }
}
