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
import mocks.services.MockUserAnswersService
import models.{ConfirmationDetails, NormalMode}
import models.SelectAlertReject.Alert
import pages.{ConfirmationPage, SelectAlertRejectPage}
import play.api.http.Status.SEE_OTHER
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, defaultAwaitTimeout, redirectLocation, route, running, status, writeableOf_AnyContentAsEmpty}
import services.UserAnswersService

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with MockUserAnswersService {

  "IndexController Controller" - {

    "when calling .onPageLoad()" - {

      "when existing UserAnswers don't exist" - {

        "must Initialise the UserAnswers and redirect to the SelectAlertRejectController" in {

          MockUserAnswersService.set(emptyUserAnswers).returns(Future.successful(emptyUserAnswers))

          val application = applicationBuilder(userAnswers = None).overrides(
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          ).build()

          running(application) {
            val request = FakeRequest(GET, routes.IndexController.onPageLoad(testErn, testArc).url)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.SelectAlertRejectPageController.onPageLoad(testErn, testArc, NormalMode).url)
          }
        }
      }

      "when existing UserAnswers exist" - {

        "must redirect to the SelectAlertRejectController" in {

          val userAnswers = emptyUserAnswers.set(SelectAlertRejectPage, Alert)

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          running(application) {

            val request = FakeRequest(GET, routes.IndexController.onPageLoad(testErn, testArc).url)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.SelectAlertRejectPageController.onPageLoad(testErn, testArc, NormalMode).url)
          }
        }
      }

      "when the ConfirmationPage UserAnswer exists" - {

        "must Initialise the UserAnswers and redirect to the SelectAlertRejectController" in {

          val userAnswersSoFar = emptyUserAnswers
            .set(ConfirmationPage, ConfirmationDetails(userAnswers = emptyUserAnswers))

          MockUserAnswersService.set(emptyUserAnswers).returns(Future.successful(emptyUserAnswers))

          val application = applicationBuilder(userAnswers = Some(userAnswersSoFar)).overrides(
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          ).build()

          running(application) {
            val request = FakeRequest(GET, routes.IndexController.onPageLoad(testErn, testArc).url)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.SelectAlertRejectPageController.onPageLoad(testErn, testArc, NormalMode).url)
          }
        }

      }

    }

  }

}
