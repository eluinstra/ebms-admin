/*
 * Copyright 2013 Clockwork
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.clockwork.ebms.admin.web.message;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum EbMSMessageTrafficChartOption
{
	ALL(
			"All Messages",
			new EbMSMessageTrafficChartSerie[]{EbMSMessageTrafficChartSerie.TOTAL_STATUS,EbMSMessageTrafficChartSerie.RECEIVE_STATUS,
					EbMSMessageTrafficChartSerie.SEND_STATUS}),
	RECEIVED(
			"Received Messages",
			new EbMSMessageTrafficChartSerie[]{EbMSMessageTrafficChartSerie.RECEIVE_STATUS_NOK,EbMSMessageTrafficChartSerie.RECEIVE_STATUS_WARN,
					EbMSMessageTrafficChartSerie.RECEIVE_STATUS_OK,EbMSMessageTrafficChartSerie.RECEIVE_STATUS}),
	CREATED(
			"Created Messages",
			new EbMSMessageTrafficChartSerie[]{EbMSMessageTrafficChartSerie.SEND_STATUS_NOK,EbMSMessageTrafficChartSerie.SEND_STATUS_WARN,
					EbMSMessageTrafficChartSerie.SEND_STATUS_OK,EbMSMessageTrafficChartSerie.SEND_STATUS});

	String title;
	EbMSMessageTrafficChartSerie[] ebMSMessageTrafficChartSeries;
}