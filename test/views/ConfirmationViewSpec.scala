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
import fixtures.messages.ConfirmationMessages
import models.ConfirmationDetails
import models.SelectAlertReject.{Alert, Reject}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.SelectAlertRejectPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.ConfirmationView

class ConfirmationViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "ConfirmationView" - {

    Seq(ConfirmationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        Seq(Alert,Reject).foreach { aType =>

          s"when a $aType submission" - {

            implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            val view = app.injector.instanceOf[ConfirmationView]

            val testConfirmationDetails = ConfirmationDetails(emptyUserAnswers.set(SelectAlertRejectPage, aType))

            implicit val doc: Document = Jsoup.parse(view(testConfirmationDetails).toString())

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title,
              Selectors.h1 -> messagesForLanguage.submissionStatus(aType),
              Selectors.h2(1) -> messagesForLanguage.h2(aType),
              Selectors.link(1) -> messagesForLanguage.printThisScreen,
              Selectors.h2(2) -> messagesForLanguage.whatHappensNext,
              Selectors.p(2) -> messagesForLanguage.whatHappensNextParagraph(aType),
              Selectors.h3(1) -> messagesForLanguage.ifAlertedHeading,
              Selectors.p(3) -> messagesForLanguage.ifAlertedHeadingParagraph,
              Selectors.h3(2) -> messagesForLanguage.ifRejectedHeading,
              Selectors.p(4) -> messagesForLanguage.ifRejectedHeadingParagraph1,
              Selectors.p(5) -> messagesForLanguage.ifRejectedHeadingParagraph2,
              "#confirmationButton" -> messagesForLanguage.returnToMovement,
              "#feedbackSurvey" -> messagesForLanguage.feedback
            ))

          }

        }


      }
    }
  }
}