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
import fixtures.messages.InformationMessages
import models.CheckMode
import pages._
import play.api.Application
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

class InformationSummarySpec extends SpecBase {

  implicit lazy val app: Application = applicationBuilder().build()
  lazy val link = app.injector.instanceOf[link]

  "InformationSummary" - {

    Seq(InformationMessages.English).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val msgs: Messages = messagesApi(app).preferred(Seq(langMessages.lang))
        lazy val informationSummary = new InformationSummary(link)

        def pageToRoute(page: QuestionPage[Option[String]]): Call = page match {
          case ConsigneeInformationPage =>
            controllers.routes.ConsigneeInformationController.onPageLoad(testErn, testArc, CheckMode)
          case GoodsTypeInformationPage =>
            controllers.routes.GoodsTypeInformationController.onPageLoad(testErn, testArc, CheckMode)
          case GoodsQuantitiesInformationPage =>
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          case GiveInformationPage =>
            controllers.routes.GiveInformationController.onPageLoad(testErn, testArc, CheckMode)
          case page =>
            throw new IllegalArgumentException(s"Unexpected page of $page in pageToRoute")
        }

        def pageToCYALabel(page: QuestionPage[Option[String]]): String = page match {
          case ConsigneeInformationPage =>
            langMessages.cyaConsigneeLabel
          case GoodsTypeInformationPage =>
            langMessages.cyaGoodsTypesLabel
          case GoodsQuantitiesInformationPage =>
            langMessages.cyaGoodsQuantitiesLabel
          case page =>
            throw new IllegalArgumentException(s"Unexpected page of $page in pageToCYALabel")
        }

        def pageToCYAHidden(page: QuestionPage[Option[String]]): String = page match {
          case ConsigneeInformationPage =>
            langMessages.cyaConsigneeHidden
          case GoodsTypeInformationPage =>
            langMessages.cyaGoodsTypesHidden
          case GoodsQuantitiesInformationPage =>
            langMessages.cyaGoodsQuantitiesHidden
          case page =>
            throw new IllegalArgumentException(s"Unexpected page of $page in pageToCYAHidden")
        }

        def pageToCYAMoreInformation(page: QuestionPage[Option[String]]): String = page match {
          case ConsigneeInformationPage =>
            langMessages.addMoreConsigneeInformation
          case GoodsTypeInformationPage =>
            langMessages.addMoreGoodsTypesInformation
          case GoodsQuantitiesInformationPage =>
            langMessages.addMoreGoodsQuantitiesInformation
          case page =>
            throw new IllegalArgumentException(s"Unexpected page of $page in pageToCYAMoreInformation")
        }

        Seq(ConsigneeInformationPage, GoodsTypeInformationPage, GoodsQuantitiesInformationPage).foreach { aPage =>

            s"must render the expected SummaryRowList for ${aPage.getClass.getSimpleName.stripSuffix("$")}" - {

              "when information has been supplied" in {

                implicit val userAnswers = emptyUserAnswers.set(aPage, Some("user inputted text"))

                informationSummary.row(
                  page = aPage,
                  changeAction = pageToRoute(aPage),
                  keyOverride = None,
                  showChangeLinks = true
                ) mustBe
                  SummaryListRowViewModel(
                    key = pageToCYALabel(aPage),
                    value = ValueViewModel(Text("user inputted text")),
                    actions = Seq(
                      ActionItemViewModel(
                        langMessages.change,
                        pageToRoute(aPage).url,
                        id = aPage
                      ).withVisuallyHiddenText(pageToCYAHidden(aPage))
                    )
                  )

              }

              "when information is missing" in {

                implicit val userAnswers = emptyUserAnswers.set(aPage, None)

                informationSummary.row(
                  page = aPage,
                  changeAction = pageToRoute(aPage),
                  keyOverride = None,
                  showChangeLinks = true
                ) mustBe
                  SummaryListRowViewModel(
                    key = pageToCYALabel(aPage),
                    value = ValueViewModel(HtmlContent(link(
                      pageToRoute(aPage).url,
                      pageToCYAMoreInformation(aPage)
                    )))
                  )
              }

              "with no change links" in {

                implicit val userAnswers = emptyUserAnswers.set(aPage, Some("user inputted text"))

                informationSummary.row(
                  page = aPage,
                  changeAction = pageToRoute(aPage),
                  keyOverride = None,
                  showChangeLinks = false
                ) mustBe
                  SummaryListRowViewModel(
                    key = pageToCYALabel(aPage),
                    value = ValueViewModel(Text("user inputted text")),
                    actions = Seq()
                  )

              }
            }

        }
      }
    }

  }

}
