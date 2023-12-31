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
import forms.ChooseConsigneeInformationFormProvider
import mocks.services.MockUserAnswersService
import models.SelectAlertReject.Alert
import models.SelectReason.ConsigneeDetailsWrong
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.{ChooseConsigneeInformationPage, ConsigneeInformationPage, SelectAlertRejectPage, SelectReasonPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.ChooseConsigneeInformationView

import scala.concurrent.Future

class ChooseConsigneeInformationControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val view = application.injector.instanceOf[ChooseConsigneeInformationView]
  }

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ChooseConsigneeInformationFormProvider()
  val form = formProvider()

  lazy val chooseConsigneeInformationRoute  = routes.ChooseConsigneeInformationController.onPageLoad(testErn, testArc, NormalMode).url

  "ChooseConsigneeInformation Controller" - {

    "must return OK and the correct view for a GET when the SelectAlertReject has been answered" in new Fixture(Some(emptyUserAnswers
      .set(SelectAlertRejectPage, Alert))
    ) {

      running(application) {

        val request = FakeRequest(GET, chooseConsigneeInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          onSubmitCall = controllers.routes.ChooseConsigneeInformationController.onSubmit(testErn, testArc, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must Redirect to JourneyCorrectionController for a GET when the SelectAlertReject has NOT been answered" in new Fixture() {

      running(application) {

        val request = FakeRequest(GET, chooseConsigneeInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers
      .set(SelectAlertRejectPage, Alert)
      .set(ChooseConsigneeInformationPage, true)
    )) {

      running(application) {

        val request = FakeRequest(GET, chooseConsigneeInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(true),
          onSubmitCall = controllers.routes.ChooseConsigneeInformationController.onSubmit(testErn, testArc, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the the next page when valid data has been submitted" in new Fixture(Some(emptyUserAnswers
      .set(SelectAlertRejectPage, Alert)
    )) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {

        val request =
          FakeRequest(POST, chooseConsigneeInformationRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must cleanse the consignee information free text" -{

      val userAnswersSoFar = emptyUserAnswers
        .set(SelectAlertRejectPage, Alert)
        .set(SelectReasonPage, Set(ConsigneeDetailsWrong))
        .set(ChooseConsigneeInformationPage, true)
        .set(ConsigneeInformationPage, Some("user entered free text"))

      "when changing the answer to `No`" in new Fixture(Some(userAnswersSoFar)) {
        val expectedAnswersToSave = userAnswersSoFar
          .set(ChooseConsigneeInformationPage, false)
          .set(ConsigneeInformationPage, None)

        running(application) {

          MockUserAnswersService.set(expectedAnswersToSave).returns(Future.successful(expectedAnswersToSave))

          val request =
            FakeRequest(POST, chooseConsigneeInformationRoute)
              .withFormUrlEncodedBody(("value", "false"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

    }

    "must redirect to the JourneyRecoveryController when valid data is submitted but no SelectAlertReject answer" in new Fixture(Some(emptyUserAnswers)) {

      running(application) {

        val request =
          FakeRequest(POST, chooseConsigneeInformationRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers
      .set(SelectAlertRejectPage, Alert)
    )) {

      running(application) {
        val request =
          FakeRequest(POST, chooseConsigneeInformationRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          onSubmitCall = controllers.routes.ChooseConsigneeInformationController.onSubmit(testErn, testArc, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {

        val request = FakeRequest(GET, chooseConsigneeInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {
        val request =
          FakeRequest(POST, chooseConsigneeInformationRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
