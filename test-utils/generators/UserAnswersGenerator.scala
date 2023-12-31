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

import fixtures.BaseFixtures
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues with BaseFixtures {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(GoodsQuantitiesInformationPage.type, JsValue)] ::
    arbitrary[(ChooseGoodsQuantitiesInformationPage.type, JsValue)] ::
    arbitrary[(ChooseGoodsTypeInformationPage.type, JsValue)] ::
    arbitrary[(DestinationOfficePage.type, JsValue)] ::
    arbitrary[(GiveInformationPage.type, JsValue)] ::
    arbitrary[(SelectGiveInformationPage.type, JsValue)] ::
    arbitrary[(ChooseConsigneeInformationPage.type, JsValue )] ::
    arbitrary[(ConsigneeInformationPage.type, JsValue)] ::
    arbitrary[(SelectReasonPage.type, JsValue)] ::
    arbitrary[(SelectAlertRejectPage.type, JsValue)] ::
    Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        data <- generators match {
          case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
          case _   => Gen.mapOf(oneOf(generators))
        }
      } yield UserAnswers (
        testErn,
        testArc,
        data = data.foldLeft(Json.obj()) {
          case (obj, (path, value)) =>
            obj.setObject(path.path, value).get
        }
      )
    }
  }
}
