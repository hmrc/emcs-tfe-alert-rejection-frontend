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
import models.SelectAlertReject

object ConfirmationMessages {

  sealed trait ViewMessages { _: i18n =>
    val title = "Confirmation - Excise Movement and Control System - GOV.UK"

    def submissionStatus(alertOrReject: SelectAlertReject): String = alertOrReject match {
      case SelectAlertReject.Alert => "Alert submitted"
      case SelectAlertReject.Reject => "Rejection submitted"
    }

    def h2(alertOrReject: SelectAlertReject): String = alertOrReject match {
      case SelectAlertReject.Alert => "Alert information"
      case SelectAlertReject.Reject => "Rejection information"
    }

    val whatHappensNext = "What happens next"

    def whatHappensNextParagraph(alertOrReject: SelectAlertReject): String = alertOrReject match {
      case SelectAlertReject.Alert => "The movement will be updated to show you have successfully submitted an alert. This may not happen straight away."
      case SelectAlertReject.Reject => "The movement will be updated to show you have successfully submitted a rejection. This may not happen straight away."
    }

    val ifAlertedHeading = "If you have alerted the consignor to an issue"

    val ifAlertedHeadingParagraph = "The movement can continue but you may wish to contact the consignor to discuss the issue. Depending on the issue and movement status, the consignor may choose to cancel or submit a change of destination."

    val ifRejectedHeading = "If you have rejected the movement"

    val ifRejectedHeadingParagraph1 = "The consignor must now cancel or submit a change of destination for the movement."
    val ifRejectedHeadingParagraph2 = "If the movement contains energy goods, the consignor may also choose to split the movement."

    val returnToMovement = "Return to movement"

    val feedback = "What did you think of this service? (opens in new tab)"

    val printThisScreen = "Print this screen"
  }

  object English extends ViewMessages with BaseEnglish {}
}
