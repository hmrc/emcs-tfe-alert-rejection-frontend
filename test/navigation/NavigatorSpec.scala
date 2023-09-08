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
import models.SelectReason.{ConsigneeDetailsWrong, Other}
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

        "must go to the SelectReason page" in {
          navigator.nextPage(SelectAlertRejectPage, NormalMode, emptyUserAnswers) mustBe
            routes.SelectReasonController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for the SelectReasonController page" - {

        // TODO update to redirect to new page when it is created
        "must go to UnderConstruction page" - {

          "when the user has chosen `Other`" in {
            val userAnswers = emptyUserAnswers
              .set(SelectAlertRejectPage, Alert)
              .set(SelectReasonPage, Set(Other))

            navigator.nextPage(SelectReasonPage, NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        "must go to the SelectGiveInformation page" - {

          "when the user has chosen an option that doesn't contain `Other`" in {
            val userAnswers = emptyUserAnswers
              .set(SelectAlertRejectPage, Alert)
              .set(SelectReasonPage, Set(ConsigneeDetailsWrong))

            navigator.nextPage(SelectReasonPage, NormalMode, userAnswers) mustBe
              controllers.routes.SelectGiveInformationController.onPageLoad(testErn, testArc, NormalMode)
          }
        }
      }
    }

    "for the SelectGiveInformation page" - {

      // TODO update to redirect to new pages when they are created
      "must go to UnderConstruction page" - {

        "when the user has chosen Yes to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(Other))
            .set(SelectGiveInformationPage, true)

          navigator.nextPage(SelectReasonPage, NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }

        "when the user has chosen No to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(ConsigneeDetailsWrong))
            .set(SelectGiveInformationPage, false)

          navigator.nextPage(SelectReasonPage, NormalMode, userAnswers) mustBe
            controllers.routes.SelectGiveInformationController.onPageLoad(testErn, testArc, NormalMode)
        }
      }
    }
  }

  "in Check mode" - {

  }
}
