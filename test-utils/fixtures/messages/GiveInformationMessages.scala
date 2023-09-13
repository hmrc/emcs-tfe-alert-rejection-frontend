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

    def title(selectAlertReject: SelectAlertReject, hasOther: Boolean): String = titleHelper(heading(selectAlertReject,hasOther))

    def heading(selectAlertReject: SelectAlertReject, hasOther: Boolean): String = {
      (selectAlertReject, hasOther) match {
        case (Alert, true) => "Give information about the alert"
        case (Reject, true) =>"Give information about the rejection"
        case (Alert, false) => "Give more information about the alert"
        case (Reject, false) => "Give more information about the rejection"
      }
    }

    def hint(hasOther: Boolean): String = {
      hasOther match {
        case true => "Give information."
        case _ => "Give information (optional)."
      }
    }

    val errorRequired: String
    val errorLength: String
    val errorCharacter: String
    val errorXss: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val errorRequired = "Enter information"
    override val errorLength = "Information must be 350 characters or less"
    override val errorCharacter = "Information must contain letters or numbers"
    override val errorXss = "Information must not include < and > and : and ;"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val errorRequired = "Enter information"
    override val errorLength = "Information must be 350 characters or less"
    override val errorCharacter = "Information must contain letters or numbers"
    override val errorXss = "Information must not include < and > and : and ;"
  }
}
