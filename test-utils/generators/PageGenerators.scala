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

package generators

import org.scalacheck.Arbitrary
import pages.{DestinationOfficePage, GiveInformationPage, SelectAlertRejectPage, SelectGiveInformationPage, SelectReasonPage, ChooseConsigneeInformationPage}

trait PageGenerators {

  implicit lazy val arbitraryDestinationOfficePage: Arbitrary[DestinationOfficePage.type] =
    Arbitrary(DestinationOfficePage)

  implicit lazy val arbitrarySelectGiveInformationPage: Arbitrary[SelectGiveInformationPage.type] =
    Arbitrary(SelectGiveInformationPage)

  implicit lazy val arbitraryGiveInformationPage: Arbitrary[GiveInformationPage.type] =
    Arbitrary(GiveInformationPage)

  implicit lazy val arbitrarySelectReasonPage: Arbitrary[SelectReasonPage.type] =
    Arbitrary(SelectReasonPage)

  implicit lazy val arbitrarySelectAlertRejectPage: Arbitrary[SelectAlertRejectPage.type] =
    Arbitrary(SelectAlertRejectPage)

  implicit lazy val chooseConsigneeInformationPage: Arbitrary[ChooseConsigneeInformationPage.type] =
    Arbitrary(ChooseConsigneeInformationPage)

}
