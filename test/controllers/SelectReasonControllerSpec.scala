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
import forms.SelectReasonFormProvider
import mocks.services.MockUserAnswersService
import models.SelectAlertReject.Alert
import models.SelectReason.{ConsigneeDetailsWrong, Other}
import models.{NormalMode, SelectReason}
import navigation.{FakeNavigator, Navigator}
import pages._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.SelectReasonView

import scala.concurrent.Future

class SelectReasonControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  lazy val selectReasonRoute = routes.SelectReasonController.onPageLoad(testErn, testArc, NormalMode).url

  val formProvider = new SelectReasonFormProvider()
  val form = formProvider(Alert)

  lazy val selectReasonSubmitAction = routes.SelectReasonController.onSubmit(testErn, testArc, NormalMode)

  "SelectReason Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(SelectAlertRejectPage, Alert)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, selectReasonRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SelectReasonView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, Alert, selectReasonSubmitAction)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(SelectAlertRejectPage, Alert)
        .set(SelectReasonPage, SelectReason.values.toSet)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, selectReasonRoute)

        val view = application.injector.instanceOf[SelectReasonView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(SelectReason.values.toSet), Alert, selectReasonSubmitAction)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {


      MockUserAnswersService.set().returns(
        Future.successful(emptyUserAnswers
          .set(SelectAlertRejectPage, Alert)
          .set(SelectReasonPage, SelectReason.values.toSet)
        )
      )

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers
          .set(SelectAlertRejectPage, Alert)
          .set(SelectReasonPage, SelectReason.values.toSet)
        ))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, selectReasonRoute)
            .withFormUrlEncodedBody(("value[0]", SelectReason.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(SelectAlertRejectPage, Alert)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, selectReasonRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[SelectReasonView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, Alert, selectReasonSubmitAction)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, selectReasonRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, selectReasonRoute)
            .withFormUrlEncodedBody(("value[0]", SelectReason.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must cleanse answers when un-selecting options that are already selected" in {
      val userAnswersSoFar = emptyUserAnswers
        .set(SelectAlertRejectPage, Alert)
        .set(SelectReasonPage, SelectReason.values.toSet)
        .set(ChooseConsigneeInformationPage, true)
        .set(ConsigneeInformationPage, Some("a"))
        .set(ChooseGoodsTypeInformationPage, true)
        .set(GoodsTypeInformationPage, Some("b"))
        .set(ChooseGoodsQuantitiesInformationPage, true)
        .set(GoodsQuantitiesInformationPage, Some("c"))
        .set(GiveInformationPage, Some("d"))

      val expectedAnswersToSave = emptyUserAnswers
        .set(SelectAlertRejectPage, Alert)
        .set(SelectReasonPage, Seq(ConsigneeDetailsWrong, Other))
        .set(ChooseConsigneeInformationPage, true)
        .set(ConsigneeInformationPage, Some("a"))
        .set(GiveInformationPage, Some("d"))

      MockUserAnswersService.set(expectedAnswersToSave).returns(Future.successful(expectedAnswersToSave))

      val application =
        applicationBuilder(userAnswers = Some(userAnswersSoFar))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, selectReasonRoute)
            .withFormUrlEncodedBody(
              ("value[0]", ConsigneeDetailsWrong.toString),
              ("value[1]", Other.toString)
            )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }
  }

}
