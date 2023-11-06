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

package views

import base.ViewSpecBase
import fixtures.messages.DestinationOfficeMessages
import forms.DestinationOfficeFormProvider
import models.NormalMode
import org.jsoup.Jsoup
import play.api.test.FakeRequest
import views.html.DestinationOfficeView

class DestinationOfficeViewSpec extends ViewSpecBase with ViewBehaviours {

  lazy val form = app.injector.instanceOf[DestinationOfficeFormProvider].apply()
  lazy val view = app.injector.instanceOf[DestinationOfficeView]

  object Selectors extends BaseSelectors

  "DestinationOffice view" - {

    Seq(DestinationOfficeMessages.English, DestinationOfficeMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs = messages(app, messagesForLanguage.lang)
        implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
        implicit val doc = Jsoup.parse(view(form, NormalMode).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.radioButton(1) -> messagesForLanguage.gb,
          Selectors.radioButton(2) -> messagesForLanguage.ni,
          Selectors.button -> messagesForLanguage.continue
        ))
      }
    }
  }
}
