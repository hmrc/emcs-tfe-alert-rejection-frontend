package viewmodels

import base.SpecBase
import fixtures.messages.InformationMessages
import models.CheckMode
import pages.{ConsigneeInformationPage, GiveInformationPage, GoodsQuantitiesInformationPage, GoodsTypeInformationPage, QuestionPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.checkAnswers.InformationSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

class InformationSummarySpec extends SpecBase {

  "InformationSummary" - {

    Seq(InformationMessages.English, InformationMessages.Welsh).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))
        lazy val link = app.injector.instanceOf[link]
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
        }

        def pageToCYALabel(page: QuestionPage[Option[String]]): String = page match {
          case ConsigneeInformationPage =>
            langMessages.cyaConsigneeLabel
          case GoodsTypeInformationPage =>
            langMessages.cyaGoodsTypesLabel
          case GoodsQuantitiesInformationPage =>
            langMessages.cyaGoodsQuantitiesLabel
        }

        def pageToCYAHidden(page: QuestionPage[Option[String]]): String = page match {
          case ConsigneeInformationPage =>
            langMessages.cyaConsigneeHidden
          case GoodsTypeInformationPage =>
            langMessages.cyaGoodsTypesHidden
          case GoodsQuantitiesInformationPage =>
            langMessages.cyaGoodsQuantitiesHidden
        }

        def pageToCYAMoreInformation(page: QuestionPage[Option[String]]): String = page match {
          case ConsigneeInformationPage =>
            langMessages.addMoreConsigneeInformation
          case GoodsTypeInformationPage =>
            langMessages.addMoreGoodsTypesInformation
          case GoodsQuantitiesInformationPage =>
            langMessages.addMoreGoodsQuantitiesInformation
        }

        Seq(ConsigneeInformationPage, GoodsTypeInformationPage, GoodsQuantitiesInformationPage).foreach { aPage =>

            s"must render the expected SummaryRowList for ${aPage.getClass.getSimpleName.stripSuffix("$")}" - {

              "when information has been supplied" in {

                implicit val request = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(aPage, Some("user inputted text"))
                )

                informationSummary.row(
                  page = aPage,
                  changeAction = pageToRoute(aPage),
                  keyOverride = None
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
                implicit val request = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(aPage, None)
                )

                informationSummary.row(
                  page = aPage,
                  changeAction = pageToRoute(aPage),
                  keyOverride = None
                ) mustBe
                  SummaryListRowViewModel(
                    key = pageToCYALabel(aPage),
                    value = ValueViewModel(HtmlContent(link(
                      pageToRoute(aPage).url,
                      pageToCYAMoreInformation(aPage)
                    )))
                  )
              }
            }

        }
      }
    }

  }

}
