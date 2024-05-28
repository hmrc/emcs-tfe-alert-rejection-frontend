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

package fixtures.messages

import fixtures.i18n
import fixtures.messages.BaseEnglish.titleHelper
import models.SelectAlertReject.{Alert, Reject}
import models.SelectReason.{ConsigneeDetailsWrong, GoodTypesNotMatchOrder, Other, QuantitiesNotMatchOrder}
import models.{SelectAlertReject, SelectReason}
import play.twirl.api.{Html, HtmlFormat}

object SelectReasonMessages {
  sealed trait ViewMessages { _: i18n =>
    def title(selectAlertReject: SelectAlertReject) : String = {
      selectAlertReject match {
        case Alert => titleHelper("Why are you alerting the consignor to an issue?")
        case Reject => titleHelper("Why are you rejecting the movement?")
      }
    }

    def h1(selectAlertReject: SelectAlertReject) : String = {
      selectAlertReject match {
        case Alert => "Why are you alerting the consignor to an issue?"
        case Reject => "Why are you rejecting the movement?"
      }
    }

    val checkBoxOption1: String = "Some or all of the consignee details are wrong"
    val checkBoxOption2: String = "Goods types do not match the order"
    val checkBoxOption3: String = "Goods quantities do not match the order"
    val checkBoxOption4: String = "Other"

    def cyaLabel(selectAlertReject: SelectAlertReject) =
      selectAlertReject match {
        case Alert => "Reason(s) for alert"
        case Reject => "Reason(s) for rejection"
    }

    def cyaValue(selectReason: SelectReason): Html =
      Html(HtmlFormat.escape(
        selectReason match {
          case ConsigneeDetailsWrong =>
            "Some or all of the consignee details are wrong"
          case GoodTypesNotMatchOrder =>
            "Goods types do not match the order"
          case QuantitiesNotMatchOrder =>
            "Goods quantities do not match the order"
          case Other =>
            "Other"
        }
      ).toString())


  }
  object English extends ViewMessages with BaseEnglish {}
}
