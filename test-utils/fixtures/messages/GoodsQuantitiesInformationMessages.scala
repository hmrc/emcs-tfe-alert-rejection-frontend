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

object GoodsQuantitiesInformationMessages {

  sealed trait ViewMessages { _: i18n =>

    lazy val title: String = titleHelper(heading)

    lazy val heading: String = "Give information about the goods quantities not matching the order (optional)"

    val errorRequired: String
    val errorLength: String
    val errorCharacter: String
    val errorXss: String

    val cyaLabel: String
    val valueWhenAnswerNotPresent: String
    val cyaChangeHidden: String
    val change: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val errorRequired = "Enter information"
    override val errorLength = "Information must be 350 characters or less"
    override val errorCharacter = "Information must include letters and numbers"
    override val errorXss = "Information must not include < and > and : and ;"
    override val cyaLabel: String = "Information about goods quantities"
    override val valueWhenAnswerNotPresent: String = "Enter information about goods quantities"
    override val cyaChangeHidden: String = "information about goods quantities"
    override val change: String = "Change"
  }
}
