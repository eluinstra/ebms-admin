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

import de.adesso.wickedcharts.chartjs.chartoptions.colors.SimpleColor;
import java.awt.Color;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.EbMSMessageStatus;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum EbMSMessageTrafficChartSerie
{
	TOTAL_STATUS("Total", SimpleColor.YELLOW, Color.YELLOW, EbMSMessageStatus.values()),
	RECEIVE_STATUS_OK("Ok", SimpleColor.GREEN, Color.GREEN, new EbMSMessageStatus[]{EbMSMessageStatus.PROCESSED, EbMSMessageStatus.FORWARDED}),
	RECEIVE_STATUS_WARN("Warn", SimpleColor.ORANGE, Color.ORANGE, new EbMSMessageStatus[]{EbMSMessageStatus.RECEIVED}),
	RECEIVE_STATUS_NOK(
			"Failed",
			SimpleColor.RED,
			Color.RED,
			new EbMSMessageStatus[]{EbMSMessageStatus.UNAUTHORIZED, EbMSMessageStatus.NOT_RECOGNIZED, EbMSMessageStatus.FAILED}),
	RECEIVE_STATUS(
			"Received",
			SimpleColor.GREY,
			Color.BLACK,
			new EbMSMessageStatus[]{EbMSMessageStatus.UNAUTHORIZED, EbMSMessageStatus.NOT_RECOGNIZED, EbMSMessageStatus.RECEIVED, EbMSMessageStatus.PROCESSED,
					EbMSMessageStatus.FORWARDED, EbMSMessageStatus.FAILED}),
	SEND_STATUS_OK("Ok", SimpleColor.GREEN, Color.GREEN, new EbMSMessageStatus[]{EbMSMessageStatus.DELIVERED}),
	SEND_STATUS_WARN("Warn", SimpleColor.ORANGE, Color.ORANGE, new EbMSMessageStatus[]{EbMSMessageStatus.CREATED}),
	SEND_STATUS_NOK("Failed", SimpleColor.RED, Color.RED, new EbMSMessageStatus[]{EbMSMessageStatus.DELIVERY_FAILED, EbMSMessageStatus.EXPIRED}),
	SEND_STATUS(
			"Sending",
			SimpleColor.BLUE,
			Color.BLUE,
			new EbMSMessageStatus[]{EbMSMessageStatus.CREATED, EbMSMessageStatus.DELIVERED, EbMSMessageStatus.DELIVERY_FAILED, EbMSMessageStatus.EXPIRED});

	String name;
	de.adesso.wickedcharts.chartjs.chartoptions.colors.Color color;
	Color colorX;
	EbMSMessageStatus[] ebMSMessageStatuses;
}