/*
 * Copyright 2024 HM Revenue & Customs
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

package models.nrs

import models.alertOrRejection.SubmitAlertOrRejectionModel
import play.api.libs.json.{JsValue, Json, Writes}

case class AlertRejectNRSSubmission(ern: String,
                                    submissionRequest: SubmitAlertOrRejectionModel) {
  val json: JsValue =
    Json.toJsObject(submissionRequest).deepMerge(Json.obj("exciseRegistrationNumber" -> ern))
}

object AlertRejectNRSSubmission {
  implicit val writes: Writes[AlertRejectNRSSubmission] = Writes(_.json)
}