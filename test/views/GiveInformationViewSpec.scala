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
import fixtures.messages.GiveInformationMessages
import forms.GiveInformationFormProvider
import models.SelectAlertReject.{Alert, Reject}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.GiveInformationView


class GiveInformationViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  lazy val form = app.injector.instanceOf[GiveInformationFormProvider].apply(true)
  lazy val view = app.injector.instanceOf[GiveInformationView]

  "SelectAlertRejectView" - {

    Seq(GiveInformationMessages.English, GiveInformationMessages.Welsh).foreach { messagesForLanguage =>
      Seq(Alert, Reject).foreach { alertOrReject =>
        Seq(true, false).foreach { hasOther =>
          s"when being rendered in lang code of '${messagesForLanguage.lang.code}'with '${alertOrReject.toString}' other option set to '${hasOther}'" - {
            implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            implicit val doc: Document = Jsoup.parse(view(form, alertOrReject, hasOther, testOnwardRoute).toString())


            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title(alertOrReject, hasOther),
              Selectors.h1 -> messagesForLanguage.heading(alertOrReject, hasOther),
              Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
              Selectors.hint -> messagesForLanguage.hint(hasOther),
              Selectors.button -> messagesForLanguage.continue
            ))
          }
        }
      }
    }
  }
}
