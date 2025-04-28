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

package viewmodels.checkAnswers

import base.SpecBase
import fixtures.messages.GoodsQuantitiesInformationMessages
import models.CheckMode
import models.requests.DataRequest
import org.scalatest.matchers.must.Matchers
import pages.GoodsQuantitiesInformationPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Value
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

class GoodsQuantitiesInformationSummarySpec extends SpecBase with Matchers {

  "GoodsQuantitiesInformationSummary" - {

    lazy val app = applicationBuilder().build()
    implicit val link: link = app.injector.instanceOf[views.html.components.link]
    val summary = new GoodsQuantitiesInformationSummary(link)

    Seq(GoodsQuantitiesInformationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "when there's no answer" - {

          "must output the expected data" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            summary.row() mustBe SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(
                HtmlContent(link(
                  controllers.routes.GoodsQuantitiesInformationController.onPageLoad(testErn, testArc, CheckMode).url,
                  messagesForLanguage.valueWhenAnswerNotPresent))
              )
            )
          }
        }

        "when there's an empty answer" - {

          "must output the expected data" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
              dataRequest(FakeRequest(), emptyUserAnswers.set(GoodsQuantitiesInformationPage, Some("")))

            summary.row() mustBe SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(
                HtmlContent(link(
                  controllers.routes.GoodsQuantitiesInformationController.onPageLoad(testErn, testArc, CheckMode).url,
                  messagesForLanguage.valueWhenAnswerNotPresent))
              )
            )
          }
        }

        "when there's an answer" - {

          "must output the expected row" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
              dataRequest(FakeRequest(), emptyUserAnswers.set(GoodsQuantitiesInformationPage, Some("value")))

            summary.row() mustBe SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(Text("value")),
              actions = Seq(
                ActionItemViewModel(
                  content = messagesForLanguage.change,
                  controllers.routes.GoodsQuantitiesInformationController.onPageLoad(testErn, testArc, CheckMode).url,
                  id = GoodsQuantitiesInformationPage
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
              )
            )
          }
        }
      }
    }
  }

}
