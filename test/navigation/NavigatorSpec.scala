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

package navigation

import base.SpecBase
import controllers.routes
import models.SelectAlertReject.Alert
import models.SelectReason.{ConsigneeDetailsWrong, GoodTypesNotMatchOrder, Other}
import models._
import pages._

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page

        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn, testArc)
      }

      "for the SelectAlertReject page" - {

        "must go to the SelectReason page" - {
          "when the trader is a GB trader" in {
            navigator.nextPage(SelectAlertRejectPage, NormalMode, emptyUserAnswers) mustBe
              routes.SelectReasonController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "must go to the DestinationOffice page" - {
          "when the trader is an XI trader" in {
            val northernIrelandERN = "XI1234567890"

            navigator.nextPage(SelectAlertRejectPage, NormalMode, emptyUserAnswers.copy(ern = northernIrelandERN)) mustBe
              routes.DestinationOfficeController.onPageLoad(northernIrelandERN, testArc, NormalMode)
          }
        }
      }

      "for the SelectReasonController page" - {

        "must go to GiveInformation page" - {

          "when the user has chosen an option that does contain `Other`" in {
            val userAnswers = emptyUserAnswers
              .set(SelectAlertRejectPage, Alert)
              .set(SelectReasonPage, Set(Other))

            navigator.nextPage(SelectReasonPage, NormalMode, userAnswers) mustBe
              controllers.routes.GiveInformationController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "must go to the SelectGiveInformation page" - {

          // TODO remove this once AR stories are completed
          "when the user has chosen an option that doesn't contain `Other` or `Some or all of the consignee details are wrong`" in {
            val userAnswers = emptyUserAnswers
              .set(SelectAlertRejectPage, Alert)
              .set(SelectReasonPage, Set(GoodTypesNotMatchOrder))

            navigator.nextPage(SelectReasonPage, NormalMode, userAnswers) mustBe
              controllers.routes.SelectGiveInformationController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "must go to the ChooseConsigneeInformation page" - {

          "when the user has chosen `Some or all of the consignee details are wrong`" in {
            val userAnswers = emptyUserAnswers
              .set(SelectAlertRejectPage, Alert)
              .set(SelectReasonPage, Set(ConsigneeDetailsWrong))

            navigator.nextPage(SelectReasonPage, NormalMode, userAnswers) mustBe
              controllers.routes.ChooseConsigneeInformationController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

      }
    }

    "for the SelectGiveInformation page" - {

      "must go to GiveInformation page" - {

        "when the user has chosen Yes to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(Other))
            .set(SelectGiveInformationPage, true)

          navigator.nextPage(SelectReasonPage, NormalMode, userAnswers) mustBe
            controllers.routes.GiveInformationController.onPageLoad(testErn, testArc, NormalMode)
        }

        "when the user has chosen No to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(ConsigneeDetailsWrong))
            .set(SelectGiveInformationPage, false)

          navigator.nextPage(SelectGiveInformationPage, NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }
    }

    "for the ChooseConsigneeInformation page" - {

        // TODO route to consignee information page AR04 when finished
        "when the user has chosen Yes to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(ConsigneeDetailsWrong))
            .set(ChooseConsigneeInformationPage, true)

          navigator.nextPage(ChooseConsigneeInformationPage, NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }

        "when the user has chosen No to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(ConsigneeDetailsWrong))
            .set(ChooseConsigneeInformationPage, false)

          navigator.nextPage(ChooseConsigneeInformationPage, NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
    }

    //TODO: GIVE INFORMATION PAGE NAV SPEC
    "for  the GiveInformation page" - {
      "must go to the CheckYourAnswers page" - {

        val userAnswers = emptyUserAnswers
          .set(SelectAlertRejectPage, Alert)
          .set(SelectReasonPage, Set(ConsigneeDetailsWrong))
          .set(SelectGiveInformationPage, false)
          .set(GiveInformationPage, Some(""))
        navigator.nextPage(SelectGiveInformationPage, NormalMode, userAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }
    }
  }

  "in Check mode" - {

  }
}
