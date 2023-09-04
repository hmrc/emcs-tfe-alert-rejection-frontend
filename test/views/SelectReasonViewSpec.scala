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
import fixtures.messages.SelectReasonMessages
import forms.SelectReasonFormProvider
import models.SelectAlertReject
import models.SelectAlertReject.{Alert, Reject}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.SelectReasonView


class SelectReasonViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors


  lazy val view = app.injector.instanceOf[SelectReasonView]

  "SelectReasonView" - {

    Seq(SelectReasonMessages.English, SelectReasonMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        "when SelectAlertReject is `Alert`" - {

          val reason: SelectAlertReject = Alert
          val form = app.injector.instanceOf[SelectReasonFormProvider].apply(reason)

          implicit val doc: Document = Jsoup.parse(view(form, reason, testOnwardRoute).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(reason),
            Selectors.h1 -> messagesForLanguage.h1(reason),
            Selectors.checkboxItem(1) -> messagesForLanguage.checkBoxOption1,
            Selectors.checkboxItem(2) -> messagesForLanguage.checkBoxOption2,
            Selectors.checkboxItem(3) -> messagesForLanguage.checkBoxOption3,
            Selectors.checkboxItem(4) -> messagesForLanguage.checkBoxOption4,
            Selectors.button -> messagesForLanguage.continue
          ))
        }


        "when SelectAlertReject is `Reject`" - {

          val reason: SelectAlertReject = Reject
          lazy val form = app.injector.instanceOf[SelectReasonFormProvider].apply(reason)

          implicit val doc: Document = Jsoup.parse(view(form, reason, testOnwardRoute).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(reason),
            Selectors.h1 -> messagesForLanguage.h1(reason),
            Selectors.checkboxItem(1) -> messagesForLanguage.checkBoxOption1,
            Selectors.checkboxItem(2) -> messagesForLanguage.checkBoxOption2,
            Selectors.checkboxItem(3) -> messagesForLanguage.checkBoxOption3,
            Selectors.checkboxItem(4) -> messagesForLanguage.checkBoxOption4,
            Selectors.button -> messagesForLanguage.continue
          ))
        }

      }
    }
  }
}
