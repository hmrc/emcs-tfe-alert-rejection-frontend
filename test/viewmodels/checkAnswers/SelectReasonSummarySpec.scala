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
import fixtures.messages.SelectReasonMessages
import models.NormalMode
import models.SelectAlertReject.{Alert, Reject}
import models.SelectReason.{ConsigneeDetailsWrong, GoodTypesNotMatchOrder, Other, QuantitiesNotMatchOrder}
import pages._
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.list

class SelectReasonSummarySpec extends SpecBase {

  "SelectReasonSummary" - {

    Seq(SelectReasonMessages.English, SelectReasonMessages.Welsh).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))
        lazy val list = app.injector.instanceOf[list]
        lazy val selectReasonSummary = new SelectReasonSummary(list)

        Seq(Alert, Reject).foreach{
          aType =>

            s"for a $aType" - {

              Seq(ConsigneeDetailsWrong, GoodTypesNotMatchOrder, QuantitiesNotMatchOrder, Other).foreach {
                reason =>

                 s"must render the expected SummaryRowList for a ${reason.getClass.getSimpleName.stripSuffix("$")}" - {

                   "when information has been supplied" - {

                     "with change links enabled" in {

                       implicit val userAnswers = emptyUserAnswers.set(SelectReasonPage, Seq(reason))

                       selectReasonSummary.row(aType, showChangeLinks = true) mustBe
                         Some(
                           SummaryListRowViewModel(
                             key = langMessages.cyaLabel(aType),
                             value = ValueViewModel(HtmlContent(
                               list(Seq(langMessages.cyaValue(reason)))
                             )),
                             actions = Seq(
                               ActionItemViewModel(
                                 langMessages.change,
                                 controllers.routes.SelectReasonController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode).url,
                                 id = SelectReasonPage
                               ).withVisuallyHiddenText("SelectReason")
                             )
                           )
                         )
                     }

                     "with change links disabled" in {

                       implicit val userAnswers = emptyUserAnswers.set(SelectReasonPage, Seq(reason))

                       selectReasonSummary.row(aType, showChangeLinks = false) mustBe
                         Some(
                           SummaryListRowViewModel(
                             key = langMessages.cyaLabel(aType),
                             value = ValueViewModel(HtmlContent(
                               list(Seq(langMessages.cyaValue(reason)))
                             )),
                             actions = Seq()
                           )
                         )
                     }

                   }

                 }


              }

        }


        }



      }
    }

  }
}
