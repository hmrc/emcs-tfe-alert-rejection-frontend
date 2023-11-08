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

object InformationMessages {
  sealed trait ViewMessages { _: i18n =>
    val heading: String = "Do you want to receive the movement?"
    val title: String = titleHelper(heading)

    val cyaConsigneeLabel = "Information about consignee details"
    val cyaConsigneeHidden = "information about consignee details"
    val cyaGoodsTypesLabel = "Information about goods types"
    val cyaGoodsTypesHidden = "information about goods types"
    val cyaGoodsQuantitiesLabel = "Information about goods quantities"
    val cyaGoodsQuantitiesHidden = "information about goods quantities"

    val addMoreConsigneeInformation = "Enter information about consignee details"
    val addMoreGoodsTypesInformation = "Enter information about goods types"
    val addMoreGoodsQuantitiesInformation = "Enter information about goods quantities"
  }
  object English extends ViewMessages with BaseEnglish {}

  object Welsh extends ViewMessages with BaseWelsh {}

}
