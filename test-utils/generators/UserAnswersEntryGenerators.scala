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

import models.{SelectAlertReject, SelectReason}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.{SelectGiveInformationPage, SelectAlertRejectPage, SelectReasonPage}
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitrarySelectGiveInformationUserAnswersEntry: Arbitrary[(SelectGiveInformationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SelectGiveInformationPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySelectReasonUserAnswersEntry: Arbitrary[(SelectReasonPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SelectReasonPage.type]
        value <- arbitrary[SelectReason].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySelectAlertRejectPageUserAnswersEntry: Arbitrary[(SelectAlertRejectPage.type, JsValue)] =
    Arbitrary {
      for {
        page <- arbitrary[SelectAlertRejectPage.type]
        value <- arbitrary[SelectAlertReject].map(Json.toJson(_))
      } yield (page, value)
    }

}
