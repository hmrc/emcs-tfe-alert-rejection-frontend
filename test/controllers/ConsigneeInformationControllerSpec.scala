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
import forms.ConsigneeInformationFormProvider
import mocks.services.MockUserAnswersService
import models.SelectAlertReject.Alert
import models.SelectReason.Other
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.{ChooseConsigneeInformationPage, ConsigneeInformationPage, SelectAlertRejectPage, SelectReasonPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.ConsigneeInformationView

import scala.concurrent.Future

class ConsigneeInformationControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val view = application.injector.instanceOf[ConsigneeInformationView]
  }

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ConsigneeInformationFormProvider()
  val form = formProvider()

  lazy val consigneeInformationRoute = routes.ConsigneeInformationController.onPageLoad(testErn, testArc, NormalMode).url

  "ConsigneeInformation Controller" - {

    "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers
      .set(SelectAlertRejectPage, Alert)
      .set(SelectReasonPage, Set(Other))
      .set(ChooseConsigneeInformationPage, true)
    )) {

      running(application) {
        val request = FakeRequest(GET, consigneeInformationRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ConsigneeInformationView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          onSubmit = controllers.routes.ConsigneeInformationController.onSubmit(testErn, testArc, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must return 303 and redirect to the Journey Recovery when accept or reject has not been answered" in new Fixture(Some(emptyUserAnswers
      .set(SelectReasonPage, Set(Other))
    )) {

      running(application) {
        val request = FakeRequest(GET, consigneeInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must return 303 and redirect to the Journey Recovery when Select Reason has not been answered" in new Fixture(Some(emptyUserAnswers
      .set(SelectAlertRejectPage, Alert)
    )) {

      running(application) {
        val request = FakeRequest(GET, consigneeInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers
      .set(ConsigneeInformationPage, Some("answer"))
      .set(SelectReasonPage, Set(Other))
      .set(SelectAlertRejectPage, Alert)
      .set(ChooseConsigneeInformationPage, true)
    )) {

      running(application) {
        val request = FakeRequest(GET, consigneeInformationRoute)

        val view = application.injector.instanceOf[ConsigneeInformationView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill(Some("answer")),
          controllers.routes.ConsigneeInformationController.onSubmit(testErn, testArc, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers
      .set(SelectReasonPage, Set(Other))
      .set(SelectAlertRejectPage, Alert)
      .set(ChooseConsigneeInformationPage, true)
    )) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {
        val request =
          FakeRequest(POST, consigneeInformationRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to the Journey Recovery page when Select Reason is not answered" in new Fixture(Some(emptyUserAnswers
      .set(SelectAlertRejectPage, Alert)
    )) {

      running(application) {
        val request =
          FakeRequest(POST, consigneeInformationRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to the Journey Recovery page when alert or reject is not answered" in new Fixture(Some(emptyUserAnswers
      .set(SelectReasonPage, Set(Other))
    )) {

      running(application) {
        val request =
          FakeRequest(POST, consigneeInformationRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers
      .set(SelectReasonPage, Set(Other))
      .set(SelectAlertRejectPage, Alert)
      .set(ChooseConsigneeInformationPage, true)

    )) {

      running(application) {
        val request =
          FakeRequest(POST, consigneeInformationRoute)
            .withFormUrlEncodedBody(("value", ">"))

        val boundForm = form.bind(Map("value" -> ">"))

        val view = application.injector.instanceOf[ConsigneeInformationView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          boundForm,
          controllers.routes.ConsigneeInformationController.onSubmit(testErn, testArc, NormalMode)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {
        val request = FakeRequest(GET, consigneeInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {
        val request =
          FakeRequest(POST, consigneeInformationRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
