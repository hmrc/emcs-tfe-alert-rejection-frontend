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

package viewmodels

import base.SpecBase
import fixtures.messages.SelectAlertRejectMessages
import models.CheckMode
import models.SelectAlertReject.{Alert, Reject}
import pages._
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.checkAnswers.SelectAlertRejectPageSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class SelectAlertRejectSummarySpec extends SpecBase {

  "SelectAlertRejectSummary" - {

    Seq(SelectAlertRejectMessages.English, SelectAlertRejectMessages.Welsh).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))
        lazy val selectAlertRejectSummary = new SelectAlertRejectPageSummary()

        s"must render the expected SummaryRowList for an `alert`" - {

          "when information has been supplied" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers.set(SelectAlertRejectPage, Alert))

            selectAlertRejectSummary.row(Alert) mustBe
              Some(
                SummaryListRowViewModel(
                  key = langMessages.cyaLabel,
                  value = ValueViewModel(HtmlContent(langMessages.cyaAlertValue)),
                  actions = Seq(
                    ActionItemViewModel(
                      langMessages.change,
                      controllers.routes.SelectAlertRejectPageController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, CheckMode).url,
                      id = SelectAlertRejectPage
                    ).withVisuallyHiddenText("SelectAlertRejectPage")
                  )
                )
              )
          }

        }

        s"must render the expected SummaryRowList for a `rejection`" - {

          "when information has been supplied" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers.set(SelectAlertRejectPage, Reject))

            selectAlertRejectSummary.row(Reject) mustBe
              Some(
                SummaryListRowViewModel(
                  key = langMessages.cyaLabel,
                  value = ValueViewModel(HtmlContent(langMessages.cyaRejectValue)),
                  actions = Seq(
                    ActionItemViewModel(
                      langMessages.change,
                      controllers.routes.SelectAlertRejectPageController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, CheckMode).url,
                      id = SelectAlertRejectPage
                    ).withVisuallyHiddenText("SelectAlertRejectPage")
                  )
                )
              )
          }

        }

      }
    }

  }
}
