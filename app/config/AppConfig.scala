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

package config

import featureswitch.core.config.FeatureSwitching
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig, configuration: Configuration) extends FeatureSwitching {

  override lazy val config: AppConfig = this

  lazy val host: String = configuration.get[String]("host")

  lazy val appName: String = configuration.get[String]("appName")

  lazy val selfUrl: String = servicesConfig.baseUrl("emcs-tfe-alert-rejection-frontend")

  lazy val timeout: Int = configuration.get[Int]("timeout-dialog.timeout")

  lazy val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  lazy val signOutUrl: String = configuration.get[String]("urls.signOut")

  lazy val deskproName: String = configuration.get[String]("deskproName")

  lazy val loginGuidance: String = configuration.get[String]("urls.loginGuidance")

  private lazy val feedbackFrontendHost: String = configuration.get[String]("feedback-frontend.host")

  lazy val feedbackFrontendSurveyUrl: String = s"$feedbackFrontendHost/feedback/$deskproName"

  lazy val loginUrl: String = configuration.get[String]("urls.login")

  lazy val registerGuidance: String = configuration.get[String]("urls.registerGuidance")

  lazy val signUpBetaFormUrl: String = configuration.get[String]("urls.signupBetaForm")

  lazy val fallbackProceduresUrl: String = configuration.get[String]("urls.fallbackProcedures")

  lazy val contactEMCSHelpdeskUrl: String = configuration.get[String]("urls.contactEmcsHelpdesk")

  lazy val emcsGeneralEnquiriesUrl: String = configuration.get[String]("urls.emcsGeneralEnquiries")

  def loginContinueUrl(ern: String, arc: String): String = configuration.get[String]("urls.loginContinue") + s"/trader/$ern/movement/$arc"

  def emcsTfeService: String = servicesConfig.baseUrl("emcs-tfe")

  def emcsTfeBaseUrl: String = s"$emcsTfeService/emcs-tfe"
  def emcsTfeFrontendBaseUrl: String = servicesConfig.baseUrl("emcs-tfe-frontend")

  private def nrsBrokerService: String = servicesConfig.baseUrl("nrs-broker")
  def nrsBrokerBaseUrl(): String = s"$nrsBrokerService/emcs-tfe-nrs-message-broker"

  def getFeatureSwitchValue(feature: String): Boolean = configuration.get[Boolean](feature)

  def emcsTfeHomeUrl(ern: Option[String]): String =
    configuration.get[String]("urls.emcsTfeHome")

  def emcsMovementDetailsUrl(ern: String, arc: String): String =
    configuration.get[String]("urls.emcsTfeMovementDetails").replace(":ern", ern).replace(":arc", arc)

  def emcsMovementsUrl(ern: String): String =
    configuration.get[String]("urls.emcsTfeMovements").replace(":ern", ern)

  def destinationOfficeSuffix: String = configuration.get[String]("constants.destinationOfficeSuffix")

  def traderKnownFactsBaseUrl: String =
    emcsTfeService + "/emcs-tfe/trader-known-facts"

}


