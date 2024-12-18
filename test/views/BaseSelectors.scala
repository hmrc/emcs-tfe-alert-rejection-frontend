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

package views

trait BaseSelectors {

  val title = "title"
  val h1: String = "h1"
  val legend: String = "legend"
  def h2(i: Int) = s"main h2:nth-of-type($i)"
  def h3(i: Int) = s"main h3:nth-of-type($i)"
  val p: Int => String = i => s"main p:nth-of-type($i)"
  val inset: Int => String = i => s"main div.govuk-inset-text:nth-of-type($i)"
  val link: Int => String = i => s"main a:nth-of-type($i)"
  def bullet(i: Int, ul: Int = 1) = s"main ul.govuk-list--bullet:nth-of-type($ul) li:nth-of-type($i)"
  def numbered(i: Int, ol: Int = 1) = s"main ol.govuk-list--number:nth-of-type($ol) li:nth-of-type($i)"
  val hint: String = "main .govuk-hint"
  val button = ".govuk-button"
  val secondaryButton = ".govuk-button--secondary"
  val saveAndExitLink = "#main-content > div > div > form > div.govuk-button-group > a"
  val label: String => String = forId => s"main label[for='$forId']"
  def radioButton(radioIndex: Int) = s".govuk-radios > div:nth-child($radioIndex) > label"
  def checkboxItem(index: Int) = s".govuk-checkboxes > div:nth-child($index) > label"
  val dateDay = s".govuk-date-input .govuk-date-input__item:nth-of-type(1)"
  val dateMonth = s".govuk-date-input .govuk-date-input__item:nth-of-type(2)"
  val dateYear = s".govuk-date-input .govuk-date-input__item:nth-of-type(3)"

  val tableHeader: Int => String = i => s"main table thead tr th:nth-of-type($i)"
  val tableRow: (Int, Int) => String = (x, y) => s"main table tbody tr:nth-of-type($x) > :nth-child($y)"
  val summaryRowKey: Int => String = i => s"main dl div:nth-of-type($i) dt"
  val summaryRowValue: Int => String = i => s"main dl div:nth-of-type($i) dd"

  val cardHeader: Int => String = i => s".govuk-summary-card:nth-of-type($i) h2"

  val inputSuffix = ".govuk-input__suffix"

  def detailsSummary(i: Int) = s"main details:nth-of-type($i) summary"

  def id(i: String) = s"#$i"
}

object BaseSelectors extends BaseSelectors

