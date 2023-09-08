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

import fixtures.messages.GiveInformationMessages
import forms.behaviours.StringFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}

class GiveInformationFormProviderSpec extends StringFieldBehaviours with GuiceOneAppPerSuite {

  val requiredKey = "giveInformation.error.required"
  val lengthKey = "giveInformation.error.length"
  val xssKey = "giveInformation.error.xss"
  val characterKey = "giveInformation.error.character"
  val maxLength = 350
  val fieldName = "value"




  ".value" - {

    "when value is mandatory" - {

      val form = new GiveInformationFormProvider()(true)

      "must error if no value is supplied" in {

        val actual = form.bind(Map(fieldName -> ""))
        actual.errors mustBe Seq(FormError(fieldName, requiredKey, Seq()))
      }

      "must error if no value is all emptry chars supplied" in {

        val actual = form.bind(Map(fieldName -> "     "))
        actual.errors mustBe Seq(FormError(fieldName, requiredKey, Seq()))
      }

      "must bind successfully when value is supplied" in {

        val actual = form.bind(Map(fieldName -> "foo"))
        actual.errors mustBe Seq()
        actual.get mustBe Some("foo")
      }
    }

    "when value is NOT mandatory" - {

      val form = new GiveInformationFormProvider().apply(isMandatory = false)

      "must bind successfully if no value is supplied" in {

        val actual = form.bind(Map(fieldName -> ""))
        actual.errors mustBe Seq()
        actual.get mustBe None
      }

      "must bind successfully when value is supplied" in {

        val actual = form.bind(Map(fieldName -> "foo"))
        actual.errors mustBe Seq()
        actual.get mustBe Some("foo")
      }
    }
    "regardless of mandatory of not mandatory" - {

      val form = new GiveInformationFormProvider().apply(isMandatory = false)

      "must error when value exceeds max length" in {

        val actual = form.bind(Map(fieldName -> ("A" * maxLength + 1)))
        actual.errors mustBe Seq(FormError(fieldName, lengthKey, Seq(maxLength)))
      }

      "must bind successfully when value equals max length" in {

        val actual = form.bind(Map(fieldName -> ("A" * maxLength)))
        actual.errors mustBe Seq()
        actual.get mustBe Some("A" * maxLength)
      }

      "must error when value contains invalid characters" in {

        val actual = form.bind(Map(fieldName -> "<javascript>"))
        actual.errors mustBe Seq(FormError(fieldName, xssKey, Seq(XSS_REGEX)))
      }

      "must error when value contains only non-alphanumerics" in {

        val actual = form.bind(Map(fieldName -> "."))
        actual.errors mustBe Seq(FormError(fieldName, characterKey, Seq(ALPHANUMERIC_REGEX)))
      }
    }
  }

  "Error Messages" - {

    Seq(GiveInformationMessages.English, GiveInformationMessages.Welsh) foreach { messagesForLanguage =>

      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message when no option is selected" in {
          messages(requiredKey) mustBe messagesForLanguage.errorRequired
        }

        "have the correct error message when too long" in {
          messages(lengthKey) mustBe messagesForLanguage.errorLength
        }

        "have the correct error message when no alphanumerics" in {
          messages(characterKey) mustBe messagesForLanguage.errorCharacter
        }

        "have the correct error message when contains potential XSS attack" in {
          messages(xssKey) mustBe messagesForLanguage.errorXss
        }
      }
    }
  }

}
