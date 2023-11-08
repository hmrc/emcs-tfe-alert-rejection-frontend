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
import models.SelectReason.{ConsigneeDetailsWrong, GoodTypesNotMatchOrder, Other, QuantitiesNotMatchOrder}
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

      "for the DestinationOfficePage" - {

        "must go to the SelectReasonController" - {

          "when the trader is a GB trader" in {
            navigator.nextPage(DestinationOfficePage, NormalMode, emptyUserAnswers.copy(ern = "GB1234567890")) mustBe
              routes.SelectReasonController.onPageLoad("GB1234567890", testArc, NormalMode)
          }

          "when the trader is an XI trader" in {
            navigator.nextPage(DestinationOfficePage, NormalMode, emptyUserAnswers.copy(ern = "XI1234567890")) mustBe
              routes.SelectReasonController.onPageLoad("XI1234567890", testArc, NormalMode)
          }

        }

      }

      "for the SelectReasonController page" - {

        "must go to the ChooseConsigneeInformation page" - {

          "when the user has chosen `Some or all of the consignee details are wrong`" in {
            val userAnswers = emptyUserAnswers
              .set(SelectAlertRejectPage, Alert)
              .set(SelectReasonPage, Set(ConsigneeDetailsWrong))

            navigator.nextPage(SelectReasonPage, NormalMode, userAnswers) mustBe
              controllers.routes.ChooseConsigneeInformationController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "must go to the ChooseGoodsTypesInformation page" - {

          "when the user has chosen `Goods types do not match the order`" in {
            val userAnswers = emptyUserAnswers
              .set(SelectAlertRejectPage, Alert)
              .set(SelectReasonPage, Set(GoodTypesNotMatchOrder))

            navigator.nextPage(SelectReasonPage, NormalMode, userAnswers) mustBe
              controllers.routes.ChooseGoodsTypeInformationController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "must go to the ChooseQuantitiesInformation page" - {

          "when the user has chosen `Goods quantities do not match the order`" in {
            val userAnswers = emptyUserAnswers
              .set(SelectAlertRejectPage, Alert)
              .set(SelectReasonPage, Set(QuantitiesNotMatchOrder))

            navigator.nextPage(SelectReasonPage, NormalMode, userAnswers) mustBe
              controllers.routes.ChooseGoodsQuantitiesInformationController.onPageLoad(testErn, testArc, NormalMode)
          }

          "must go to the ConsigneeInformation page" - {

            "when the user has chosen to give information about the consignee details being wrong" in {
              val userAnswers = emptyUserAnswers
                .set(SelectAlertRejectPage, Alert)
                .set(SelectReasonPage, Set(ConsigneeDetailsWrong))
                .set(ChooseConsigneeInformationPage, true)

              navigator.nextPage(ChooseConsigneeInformationPage, NormalMode, userAnswers) mustBe
                controllers.routes.ConsigneeInformationController.onPageLoad(testErn, testArc, NormalMode)
            }
          }
        }


        "must go to GiveInformation page" - {

          "when the user has chosen an option that does contain `Other`" in {
            val userAnswers = emptyUserAnswers
              .set(SelectAlertRejectPage, Alert)
              .set(SelectReasonPage, Set(Other))

            navigator.nextPage(SelectReasonPage, NormalMode, userAnswers) mustBe
              controllers.routes.GiveInformationController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

      }

      "for the ChooseConsigneeInformation page" - {

        "when the user has chosen Yes to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(ConsigneeDetailsWrong))
            .set(ChooseConsigneeInformationPage, true)

          navigator.nextPage(ChooseConsigneeInformationPage, NormalMode, userAnswers) mustBe
            controllers.routes.ConsigneeInformationController.onPageLoad(testErn, testArc, NormalMode)
        }

        "when the user has chosen No to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(ConsigneeDetailsWrong))
            .set(ChooseConsigneeInformationPage, false)

          navigator.nextPage(ChooseConsigneeInformationPage, NormalMode, userAnswers) mustBe
            controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }

        "when the user hasn't answered YES or NO to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(ConsigneeDetailsWrong))

          navigator.nextPage(ChooseConsigneeInformationPage, NormalMode, userAnswers) mustBe
            controllers.routes.ChooseConsigneeInformationController.onPageLoad(testErn, testArc, NormalMode)
        }

      }

      "for the GiveGoodsTypeInformation Page" - {

        "when the user submits more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(GoodTypesNotMatchOrder))
            .set(ChooseGoodsTypeInformationPage, true)

          navigator.nextPage(GoodsTypeInformationPage, NormalMode, userAnswers) mustBe
            controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }

        "when the user submits more information, with the `Goods types do not match the order` checkbox ticked on the select reason page" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(GoodTypesNotMatchOrder, Other))
            .set(ChooseGoodsTypeInformationPage, true)

          navigator.nextPage(GoodsTypeInformationPage, NormalMode, userAnswers) mustBe
            controllers.routes.GiveInformationController.onPageLoad(testErn, testArc, NormalMode)
        }
      }


      "for the ConsigneeInformation page" - {

        "when the user submits more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(ConsigneeDetailsWrong))
            .set(ChooseConsigneeInformationPage, true)

          navigator.nextPage(ConsigneeInformationPage, NormalMode, userAnswers) mustBe
            controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }

        "when the user submits more information, with the `Goods types do not match the order` checkbox ticked on the select reason page" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(ConsigneeDetailsWrong, GoodTypesNotMatchOrder))
            .set(ChooseConsigneeInformationPage, true)

          navigator.nextPage(ConsigneeInformationPage, NormalMode, userAnswers) mustBe
            controllers.routes.ChooseGoodsTypeInformationController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for the ChooseGoodTypesInformation page" - {
        "when the user has chosen Yes to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(GoodTypesNotMatchOrder))
            .set(ChooseGoodsTypeInformationPage, true)

          navigator.nextPage(ChooseGoodsTypeInformationPage, NormalMode, userAnswers) mustBe
            routes.GoodsTypeInformationController.onPageLoad(testErn, testArc, NormalMode)
        }

        "when the user has chosen No to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(GoodTypesNotMatchOrder))
            .set(ChooseGoodsTypeInformationPage, false)

          navigator.nextPage(ChooseGoodsTypeInformationPage, NormalMode, userAnswers) mustBe
            controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }

        "when the user hasn't answered YES or NO to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(GoodTypesNotMatchOrder))

          navigator.nextPage(ChooseGoodsTypeInformationPage, NormalMode, userAnswers) mustBe
            controllers.routes.ChooseGoodsTypeInformationController.onPageLoad(testErn, testArc, NormalMode)
        }

      }

      "for the ChooseQuantitiesInformation page" - {

        "when the user has chosen Yes to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(QuantitiesNotMatchOrder))
            .set(ChooseGoodsQuantitiesInformationPage, true)

          navigator.nextPage(ChooseGoodsQuantitiesInformationPage, NormalMode, userAnswers) mustBe
            controllers.routes.GoodsQuantitiesInformationController.onPageLoad(testErn, testArc, NormalMode)
        }

        "when the user has chosen No to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(QuantitiesNotMatchOrder))
            .set(ChooseGoodsQuantitiesInformationPage, false)

          navigator.nextPage(ChooseGoodsQuantitiesInformationPage, NormalMode, userAnswers) mustBe
            controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }

        "when the user hasn't answered YES or NO to giving more information" in {
          val userAnswers = emptyUserAnswers
            .set(SelectAlertRejectPage, Alert)
            .set(SelectReasonPage, Set(QuantitiesNotMatchOrder))

          navigator.nextPage(ChooseGoodsQuantitiesInformationPage, NormalMode, userAnswers) mustBe
            controllers.routes.ChooseGoodsQuantitiesInformationController.onPageLoad(testErn, testArc, NormalMode)
        }

      }

      "for the GoodsQuantitiesInformation page" - {

        "when the user submits more information" - {

          "navigate to the 'Other' information page (AR09)" - {

            "when the user selects 'Other' on the alert selection page" in {
              val userAnswers = emptyUserAnswers
                .set(SelectAlertRejectPage, Alert)
                .set(SelectReasonPage, Set(QuantitiesNotMatchOrder, Other))
                .set(ChooseGoodsQuantitiesInformationPage, true)
                .set(GoodsQuantitiesInformationPage, Some("answer"))

              //TODO: address when AR09 is in place (don't think this is the correct page)
              navigator.nextPage(GoodsQuantitiesInformationPage, NormalMode, userAnswers) mustBe
                controllers.routes.GiveInformationController.onPageLoad(testErn, testArc, NormalMode)
            }
          }

          "navigate to the CYA page (AR10)" - {

            "when the user hasn't selected 'Other' on the alert selection page" in {
              val userAnswers = emptyUserAnswers
                .set(SelectAlertRejectPage, Alert)
                .set(SelectReasonPage, Set(QuantitiesNotMatchOrder))
                .set(ChooseGoodsQuantitiesInformationPage, true)
                .set(GoodsQuantitiesInformationPage, Some("answer"))

              navigator.nextPage(GoodsQuantitiesInformationPage, NormalMode, userAnswers) mustBe
                testOnly.controllers.routes.UnderConstructionController.onPageLoad()
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
              controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
          }
        }
      }
    }

    "in Check mode" - {

    }
  }
}
