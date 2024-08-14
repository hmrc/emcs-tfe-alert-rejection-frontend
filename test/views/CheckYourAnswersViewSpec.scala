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
import fixtures.messages.CheckYourAnswersMessages
import models.SelectAlertReject.Alert
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.govuk.all.SummaryListViewModel
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  lazy val view = app.injector.instanceOf[CheckYourAnswersView]

  "CheckYourAnswersView" - {

    Seq(CheckYourAnswersMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        implicit val doc: Document = Jsoup.parse(view(Alert, SummaryListViewModel(Seq()), testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.button -> messagesForLanguage.submitAlert
        ))

        "submit button should prevent double click" in {
          doc.select(Selectors.button).attr("data-prevent-double-click") mustBe "true"
        }
      }
    }
  }
}
