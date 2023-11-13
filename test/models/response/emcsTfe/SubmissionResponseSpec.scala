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

package models.response.emcsTfe

import base.SpecBase
import fixtures.SubmissionFixtures
import play.api.libs.json.{JsPath, JsSuccess, Json}

class SubmissionResponseSpec extends SpecBase with SubmissionFixtures {
  "reads" - {
    "should read ChRIS JSON" in {
      Json.fromJson[SubmissionResponse](submitAlertOrRejectionChRISResponseJson) mustBe JsSuccess(submitAlertOrRejectionChRISResponseModel, JsPath \ "receipt")
    }
    "should read EIS JSON" in {
      Json.fromJson[SubmissionResponse](submitAlertOrRejectionEISResponseJson) mustBe JsSuccess(submitAlertOrRejectionEISResponseModel, JsPath \ "message")
    }
  }
}
