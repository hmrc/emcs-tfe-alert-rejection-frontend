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
import forms.DestinationOfficeFormProvider
import mocks.services.MockUserAnswersService
import models.{DestinationOffice, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.DestinationOfficePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.DestinationOfficeView

import scala.concurrent.Future

class DestinationOfficeControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers.copy(ern = "XI123"))) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()
    lazy val view = application.injector.instanceOf[DestinationOfficeView]
  }

  def onwardRoute = Call("GET", "/foo")

  lazy val destinationOfficeRoute = routes.DestinationOfficeController.onPageLoad("XI123", testArc, NormalMode).url

  val formProvider = new DestinationOfficeFormProvider()
  val form = formProvider()

  "DestinationOffice Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {
        val request = FakeRequest(GET, destinationOfficeRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request, userAnswers.get, ern = "XI123"), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
      emptyUserAnswers.copy(ern = "XI123").set(DestinationOfficePage, DestinationOffice.values.head)
    )) {
      running(application) {
        val request = FakeRequest(GET, destinationOfficeRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(form.fill(DestinationOffice.values.head), NormalMode)(dataRequest(request, ern = "XI123"), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" - {

      "and delete the rest of the answers when the input answer isn't the same as the current answer" in new Fixture(Some(
        emptyUserAnswers.copy(ern = "XI123").set(DestinationOfficePage, DestinationOffice.values.last
        ))) {
        running(application) {

          val updatedAnswers = emptyUserAnswers.copy(ern = "XI123").set(DestinationOfficePage, DestinationOffice.values.head)
          MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

          val request =
            FakeRequest(POST, destinationOfficeRoute)
              .withFormUrlEncodedBody(("value", DestinationOffice.values.head.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "and not delete the rest of the answers when the input answer is the same as the current answer" in new Fixture(Some(
        emptyUserAnswers.copy(ern = "XI123").set(DestinationOfficePage, DestinationOffice.values.head)
      )) {
        running(application) {

          MockUserAnswersService.set().never()

          val request =
            FakeRequest(POST, destinationOfficeRoute)
              .withFormUrlEncodedBody(("value", DestinationOffice.values.head.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      running(application) {
        val request =
          FakeRequest(POST, destinationOfficeRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request, ern = "XI123"), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {
        val request = FakeRequest(GET, destinationOfficeRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad("XI123", testArc).url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {
        val request =
          FakeRequest(POST, destinationOfficeRoute)
            .withFormUrlEncodedBody(("value", DestinationOffice.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad("XI123", testArc).url
      }
    }

    "must redirect to the next page in the journey" - {
      "on GET if user has a GB ERN" in new Fixture(Some(emptyUserAnswers)) {
        running(application) {
          val request = FakeRequest(GET, destinationOfficeRoute)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }
      "on POST if user has a GB ERN" in new Fixture(Some(emptyUserAnswers)) {
        running(application) {
          val request = FakeRequest(POST, destinationOfficeRoute)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }
    }
  }
}
