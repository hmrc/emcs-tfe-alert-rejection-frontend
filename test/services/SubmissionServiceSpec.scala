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

package services

import base.SpecBase
import config.AppConfig
import featureswitch.core.config.EnableNRS
import fixtures.{NRSBrokerFixtures, SubmissionFixtures}
import mocks.config.MockAppConfig
import mocks.connectors.MockSubmissionConnector
import mocks.services.{MockAuditingService, MockNRSBrokerService}
import models.DestinationOffice.GreatBritain
import models.SelectAlertReject.{Alert, Reject}
import models.SelectReason.{GoodTypesNotMatchOrder, QuantitiesNotMatchOrder}
import models.alertOrRejection.SubmitAlertOrRejectionModel
import models.audit.SubmissionAudit
import models.{SubmitAlertOrRejectionException, UnexpectedDownstreamResponseError}
import pages.{DestinationOfficePage, SelectAlertRejectPage, SelectReasonPage}
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class SubmissionServiceSpec extends SpecBase
  with MockSubmissionConnector
  with SubmissionFixtures
  with MockAuditingService
  with MockAppConfig
  with MockNRSBrokerService
  with NRSBrokerFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  implicit val appConfig: AppConfig = applicationBuilder().build().injector.instanceOf[AppConfig]

  lazy val testService = new SubmissionService(mockSubmissionConnector, mockNRSBrokerService, mockAuditingService, mockAppConfig)

  class Fixture(isNRSEnabled: Boolean) {
    MockAppConfig.getFeatureSwitchValue(EnableNRS).returns(isNRSEnabled)
  }

  ".submit" - {

    Seq(true, false).foreach { nrsEnabled =>

      s"when NRS enabled is '$nrsEnabled'" - {

        Seq(Alert, Reject).foreach { aType =>
          s"for a ${aType.getClass.getSimpleName.stripSuffix("$")}" - {

            Seq(
              ("Northern Ireland Trader" -> "XI123456789"),
              ("Great British Trader" -> "GB123456789")
            ).foreach {
              case (testDescription, testingErn) =>
                s"for a $testDescription" - {

                  "should submit, audit and return a success response" - {

                    "when connector receives a success from downstream" in new Fixture(nrsEnabled) {

                      val userAnswers = emptyUserAnswers.copy(ern = testingErn)
                        .set(SelectAlertRejectPage, aType)
                        .set(DestinationOfficePage, GreatBritain)
                        .set(SelectReasonPage, Seq(GoodTypesNotMatchOrder, QuantitiesNotMatchOrder))

                      val request = dataRequest(FakeRequest(), userAnswers, ern = testingErn)
                      val submission = SubmitAlertOrRejectionModel()(request, appConfig)

                      MockSubmitExplainDelayConnector
                        .submit(testingErn, testArc, submission).returns(Future.successful(Right(submitAlertOrRejectionChRISResponseModel)))
                        .noMoreThanOnce()

                      MockAuditingService
                        .audit(SubmissionAudit(testCredId, testInternalId, testingErn, testReceiptDate, submission, Right(submitAlertOrRejectionChRISResponseModel)))
                        .noMoreThanOnce()

                      if (nrsEnabled) {
                        MockNRSBrokerService.submitPayload(submission, testingErn)
                          .returns(Future.successful(Right(nrsBrokerResponseModel)))
                      } else {
                        MockNRSBrokerService.submitPayload(submitAlertOrRejectionModel, testingErn).never()
                      }

                      testService.submit(testingErn, testArc)(hc, request).futureValue mustBe submitAlertOrRejectionChRISResponseModel
                    }
                  }

                  "should submit, audit and return a failure response" - {

                    "when connector receives a failure from downstream" in new Fixture(nrsEnabled) {

                      val userAnswers = emptyUserAnswers.copy(ern = testingErn)
                        .set(SelectAlertRejectPage, aType)
                        .set(DestinationOfficePage, GreatBritain)
                        .set(SelectReasonPage, Seq(GoodTypesNotMatchOrder, QuantitiesNotMatchOrder))

                      val request = dataRequest(FakeRequest(), userAnswers, ern = testingErn)
                      val submission = SubmitAlertOrRejectionModel()(request, appConfig)

                      MockSubmitExplainDelayConnector
                        .submit(testingErn, testArc, submission).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
                        .noMoreThanOnce()

                      MockAuditingService
                        .audit(SubmissionAudit(testCredId, testInternalId, testingErn, testReceiptDate, submission, Left(UnexpectedDownstreamResponseError)))
                        .noMoreThanOnce()

                      if (nrsEnabled) {
                        MockNRSBrokerService.submitPayload(submission, testingErn)
                          .returns(Future.successful(Right(nrsBrokerResponseModel)))
                      } else {
                        MockNRSBrokerService.submitPayload(submitAlertOrRejectionModel, testingErn).never()
                      }

                      intercept[SubmitAlertOrRejectionException](await(testService.submit(testingErn, testArc)(hc, request))).getMessage mustBe
                        s"Failed to submit alert or rejection to emcs-tfe for ern: '$testingErn' & arc: '$testArc'"
                    }
                  }
                }
            }
          }
        }
      }
    }
  }
}
