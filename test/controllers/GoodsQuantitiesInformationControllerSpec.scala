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
import forms.GiveInformationFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.{ChooseGoodsQuantitiesInformationPage, GoodsQuantitiesInformationPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.GoodsQuantitiesInformationView

import scala.concurrent.Future

class GoodsQuantitiesInformationControllerSpec extends SpecBase with MockUserAnswersService {

  val formProvider = new GiveInformationFormProvider()
  val form = formProvider(isMandatory = false)

  lazy val goodsQuantitiesInformationRoute = routes.GoodsQuantitiesInformationController.onPageLoad(testErn, testArc, NormalMode).url

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val view = application.injector.instanceOf[GoodsQuantitiesInformationView]
  }

  "GoodsQuantitiesInformation Controller" - {

    "must return OK and the correct view for a GET" in new Fixture(
      Some(emptyUserAnswers.set(ChooseGoodsQuantitiesInformationPage, true))
    ) {

      running(application) {
        val request = FakeRequest(GET, goodsQuantitiesInformationRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[GoodsQuantitiesInformationView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers
        .set(GoodsQuantitiesInformationPage, Some("answer"))
        .set(ChooseGoodsQuantitiesInformationPage, true))
    ) {

      running(application) {
        val request = FakeRequest(GET, goodsQuantitiesInformationRoute)

        val view = application.injector.instanceOf[GoodsQuantitiesInformationView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(Some("answer")), NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(
      Some(emptyUserAnswers.set(ChooseGoodsQuantitiesInformationPage, true))
    ) {

      MockUserAnswersService.set().returns(Future.successful(
        emptyUserAnswers
          .set(ChooseGoodsQuantitiesInformationPage, true)
          .set(GoodsQuantitiesInformationPage, Some("answer"))
      ))

      running(application) {
        val request =
          FakeRequest(POST, goodsQuantitiesInformationRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(
      Some(emptyUserAnswers.set(ChooseGoodsQuantitiesInformationPage, true))
    ) {

      running(application) {
        val request =
          FakeRequest(POST, goodsQuantitiesInformationRoute)
            .withFormUrlEncodedBody(("value", ">"))

        val boundForm = form.bind(Map("value" -> ">"))

        val view = application.injector.instanceOf[GoodsQuantitiesInformationView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no AR07 data is found" in new Fixture {

      running(application) {
        val request = FakeRequest(GET, goodsQuantitiesInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no AR07 data is found" in new Fixture {

      running(application) {
        val request =
          FakeRequest(POST, goodsQuantitiesInformationRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {
        val request = FakeRequest(GET, goodsQuantitiesInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {
        val request =
          FakeRequest(POST, goodsQuantitiesInformationRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
