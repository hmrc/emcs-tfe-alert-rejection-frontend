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

import models.UserAnswers
import pages.QuestionPage
import play.api.i18n.Messages
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.JsonOptionFormatter
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

import javax.inject.Inject

class InformationSummary @Inject()(link: link) extends JsonOptionFormatter {

  def row(
           page: QuestionPage[Option[String]],
           changeAction: Call,
           keyOverride: Option[String] = None,
           showChangeLinks: Boolean
         )(implicit userAnswers: UserAnswers, messages: Messages): SummaryListRow = {

    val key = keyOverride.getOrElse(s"checkYourAnswers.$page.label")

    userAnswers.get(page) match {
      case Some(Some(answer)) if answer != "" =>
        SummaryListRowViewModel(
          key = key,
          value = ValueViewModel(Text(answer)),
          actions = if (!showChangeLinks) Seq() else Seq(
            ActionItemViewModel(
              "site.change",
              changeAction.url,
              id = page
            ).withVisuallyHiddenText(messages(s"checkYourAnswers.$page.change.hidden"))
          )
        )
      case _ =>
        SummaryListRowViewModel(
          key = key,
          value = if (!showChangeLinks) ValueViewModel(HtmlContent(HtmlFormat.empty)) else ValueViewModel(HtmlContent(link(
            link = changeAction.url,
            messageKey = s"checkYourAnswers.$page.addMoreInformation"
          )))
        )
    }
  }
}
