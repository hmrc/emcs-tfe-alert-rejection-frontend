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

object GiveInformationMessages {

  sealed trait ViewMessages { _: i18n =>

    def title(selectAlertReject: SelectAlertReject): String = titleHelper(heading(selectAlertReject))

    def heading(selectAlertReject: SelectAlertReject): String = {
      selectAlertReject match {
        case Alert => "Give information about the other reason(s) for the alert"
        case Reject => "Give information about the other reason(s) for the rejection"
      }
    }

    val errorRequiredAlert: String
    val errorRequiredReject: String
    val errorRequired: String
    val errorLength: String
    val errorCharacter: String
    val errorXss: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val errorRequiredAlert = "Enter information about the alert"
    override val errorRequiredReject = "Enter information about the rejection"
    override val errorRequired = "Enter information"
    override val errorLength = "Information must be 350 characters or less"
    override val errorCharacter = "Information must include letters and numbers"
    override val errorXss = "Information must not include < and > and : and ;"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val errorRequiredAlert = "Enter information about the alert"
    override val errorRequiredReject = "Enter information about the rejection"
    override val errorRequired = "Enter information"
    override val errorLength = "Information must be 350 characters or less"
    override val errorCharacter = "Information must include letters and numbers"
    override val errorXss = "Information must not include < and > and : and ;"
  }
}
