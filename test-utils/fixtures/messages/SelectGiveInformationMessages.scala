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
import models.SelectAlertReject
import models.SelectAlertReject.{Alert, Reject}

object SelectGiveInformationMessages {
  sealed trait ViewMessages { _: i18n =>

    def title(selectAlertReject: SelectAlertReject) : String = {
      selectAlertReject match {
        case Alert => titleHelper("Do you want to give more information about the alert?")
        case Reject => titleHelper("Do you want to give more information about the rejection?")
      }
    }

    def heading(selectAlertReject: SelectAlertReject) : String = {
      selectAlertReject match {
        case Alert => "Do you want to give more information about the alert?"
        case Reject => "Do you want to give more information about the rejection?"
      }
    }

    def errorMessage(selectAlertReject: SelectAlertReject): String = {
      selectAlertReject match {
        case Alert => "Select yes if you want to give more information about the alert"
        case Reject => "Select yes if you want to give more information about the rejection"
      }
    }
  }
  object English extends ViewMessages with BaseEnglish {}

}
