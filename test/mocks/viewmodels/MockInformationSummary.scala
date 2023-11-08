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

package mocks.viewmodels

import models.requests.DataRequest
import org.scalamock.handlers.CallHandler5
import org.scalamock.scalatest.MockFactory
import pages.QuestionPage
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import viewmodels.checkAnswers.InformationSummary

trait MockInformationSummary extends MockFactory {

  lazy val mockInformationSummary: InformationSummary = mock[InformationSummary]

  object MockInformationSummary {

    def row(page: QuestionPage[Option[String]],
            changeAction: Call,
            keyOverride: Option[String]): CallHandler5[QuestionPage[Option[String]], Call, Option[String], DataRequest[_], Messages, SummaryListRow] =
      (mockInformationSummary.row(_: QuestionPage[Option[String]], _: Call, _: Option[String])(_: DataRequest[_], _: Messages))
        .expects(page, changeAction, keyOverride, *, *)
  }

}
