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

object ChooseGoodsQuantitiesInformationMessages {
  sealed trait ViewMessages { _: i18n =>

    def title(): String = "Do you want to give information about the goods quantities not matching the order?"

    def heading(): String = "Do you want to give information about the goods quantities not matching the order?"
  }

  object English extends ViewMessages with BaseEnglish {}

  object Welsh extends ViewMessages with BaseWelsh {}
}
