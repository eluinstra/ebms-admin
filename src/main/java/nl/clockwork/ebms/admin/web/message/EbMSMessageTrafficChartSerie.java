package nl.clockwork.ebms.admin.web.message;

import java.awt.Color;

import de.adesso.wickedcharts.chartjs.chartoptions.colors.SimpleColor;
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
	TOTAL_STATUS("Total",SimpleColor.YELLOW,Color.YELLOW,EbMSMessageStatus.values()),
	RECEIVE_STATUS_OK("Ok",SimpleColor.GREEN,Color.GREEN,new EbMSMessageStatus[]{EbMSMessageStatus.PROCESSED,EbMSMessageStatus.FORWARDED}),
	RECEIVE_STATUS_WARN("Warn",SimpleColor.ORANGE,Color.ORANGE,new EbMSMessageStatus[]{EbMSMessageStatus.RECEIVED}),
	RECEIVE_STATUS_NOK("Failed",SimpleColor.RED,Color.RED,new EbMSMessageStatus[]{EbMSMessageStatus.UNAUTHORIZED,EbMSMessageStatus.NOT_RECOGNIZED,EbMSMessageStatus.FAILED}),
	RECEIVE_STATUS("Received",SimpleColor.GREY,Color.BLACK,new EbMSMessageStatus[]{EbMSMessageStatus.UNAUTHORIZED,EbMSMessageStatus.NOT_RECOGNIZED,EbMSMessageStatus.RECEIVED,EbMSMessageStatus.PROCESSED,EbMSMessageStatus.FORWARDED,EbMSMessageStatus.FAILED}),
	SEND_STATUS_OK("Ok",SimpleColor.GREEN,Color.GREEN,new EbMSMessageStatus[]{EbMSMessageStatus.DELIVERED}),
	SEND_STATUS_WARN("Warn",SimpleColor.ORANGE,Color.ORANGE,new EbMSMessageStatus[]{EbMSMessageStatus.SENDING}),
	SEND_STATUS_NOK("Failed",SimpleColor.RED,Color.RED,new EbMSMessageStatus[]{EbMSMessageStatus.DELIVERY_FAILED,EbMSMessageStatus.EXPIRED}),
	SEND_STATUS("Sending",SimpleColor.BLUE,Color.BLUE,new EbMSMessageStatus[]{EbMSMessageStatus.SENDING,EbMSMessageStatus.DELIVERED,EbMSMessageStatus.DELIVERY_FAILED,EbMSMessageStatus.EXPIRED});
	
	String name;
	de.adesso.wickedcharts.chartjs.chartoptions.colors.Color color;
	Color colorX;
	EbMSMessageStatus[] ebMSMessageStatuses;
}