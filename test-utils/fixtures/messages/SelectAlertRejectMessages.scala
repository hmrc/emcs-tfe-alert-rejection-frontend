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

object SelectAlertRejectMessages {
  sealed trait ViewMessages { _: i18n =>
    val heading: String = "Do you want to receive the movement?"
    val title: String = titleHelper(heading)
    val h1 = "Alert or reject a movement"
    val p1 = "Use an alert or rejection to tell HMRC and the consignor about a problem with a movement. You must do this as soon as you become aware of the problem."
    val p2 = "You can only submit an alert or rejection while the movement is in transit. If it's been delivered, you can refuse goods or tell the consignor about an issue when submitting a report of receipt."
    val radioOption1: String = "Yes, but I want to alert the consignor to an issue"
    val radioOption2: String = "No, I want to reject the movement"
    val requiredError: String = "Select what’s been delayed"
  }
  object English extends ViewMessages with BaseEnglish {}

  object Welsh extends ViewMessages with BaseWelsh {}

}
