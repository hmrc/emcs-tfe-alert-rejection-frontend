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

package forms

import fixtures.messages.ChooseConsigneeInformationMessages
import forms.behaviours.BooleanFieldBehaviours
import models.SelectAlertReject.{Alert, Reject}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}

class ChooseConsigneeInformationFormProviderSpec extends BooleanFieldBehaviours with GuiceOneAppPerSuite {

  Seq(Alert, Reject) foreach { selectedReasonType =>

    s"when the SelectAlertReject answer is $selectedReasonType" - {

      val requiredKey = s"chooseConsigneeInformation.error.required"
      val invalidKey = "error.boolean"

      val form = new ChooseConsigneeInformationFormProvider()()

      s".value" - {

        val fieldName = "value"

        behave like booleanField(
          form,
          fieldName,
          invalidError = FormError(fieldName, invalidKey)
        )

        behave like mandatoryField(
          form,
          fieldName,
          requiredError = FormError(fieldName, requiredKey)
        )
      }

      "the error messages" - {

        Seq(ChooseConsigneeInformationMessages.English) foreach { messagesForLanguage =>

          implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

          s"when output for language code '${messagesForLanguage.lang.code}'" - {

            "should have the correct content" in {
              messages(s"chooseConsigneeInformation.error.required") mustBe messagesForLanguage.errorMessage()
            }
          }
        }
      }
    }
  }
}
