package viewmodels

import base.SpecBase
import fixtures.messages.InformationMessages
import models.CheckMode
import pages.ConsigneeInformationPage
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


          s"must render the expected SummaryRowList for ConsigneeInformationPage" - {

            "when information has been supplied" in {

              implicit val request = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(ConsigneeInformationPage, Some("user inputted text"))
              )

              informationSummary.row(
                page = ConsigneeInformationPage,
                changeAction = controllers.routes.ConsigneeInformationController.onPageLoad(testErn, testArc, CheckMode),
                keyOverride = None
              ) mustBe
                SummaryListRowViewModel(
                  key = langMessages.cyaLabel,
                  value = ValueViewModel(Text("user inputted text")),
                  actions = Seq(
                    ActionItemViewModel(
                      langMessages.change,
                      controllers.routes.ConsigneeInformationController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = ConsigneeInformationPage
                    ).withVisuallyHiddenText(langMessages.cyaHidden)
                  )
                )

            }

            "when information is missing" in {

              implicit val request = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(ConsigneeInformationPage, None)
              )

              informationSummary.row(
                page = ConsigneeInformationPage,
                changeAction = controllers.routes.ConsigneeInformationController.onPageLoad(testErn, testArc, CheckMode),
                keyOverride = None
              ) mustBe
                SummaryListRowViewModel(
                  key = langMessages.cyaLabel,
                  value = ValueViewModel(HtmlContent(link(
                    controllers.routes.ConsigneeInformationController.onPageLoad(testErn, testArc, CheckMode).url,
                    langMessages.addMoreInformation
                  )))
                )

            }
          }




      }
    }
  }

}
