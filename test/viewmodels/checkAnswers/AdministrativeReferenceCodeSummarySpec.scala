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
import fixtures.messages.AdministrativeReferenceCodeMessages
import org.scalatest.matchers.must.Matchers
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Value
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class AdministrativeReferenceCodeSummarySpec extends SpecBase with Matchers {

  "AdministrativeReferenceCodeSummary" - {

    lazy val app = applicationBuilder().build()
    val arcSummary = new AdministrativeReferenceCodeSummary

    Seq(AdministrativeReferenceCodeMessages.English, AdministrativeReferenceCodeMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

          "must output the expected row" in {
            implicit val userAnswers = emptyUserAnswers

            arcSummary.row() mustBe Some(SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(HtmlContent(userAnswers.arc)),
              actions = Seq()
            ))
          }

      }
    }
  }

}
