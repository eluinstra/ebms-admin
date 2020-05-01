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
	ALL("All Messages",new EbMSMessageTrafficChartSerie[]{EbMSMessageTrafficChartSerie.TOTAL_STATUS,EbMSMessageTrafficChartSerie.RECEIVE_STATUS,EbMSMessageTrafficChartSerie.SEND_STATUS}),
	RECEIVED("Received Messages",new EbMSMessageTrafficChartSerie[]{EbMSMessageTrafficChartSerie.RECEIVE_STATUS_NOK,EbMSMessageTrafficChartSerie.RECEIVE_STATUS_WARN,EbMSMessageTrafficChartSerie.RECEIVE_STATUS_OK,EbMSMessageTrafficChartSerie.RECEIVE_STATUS}),
	SENT("Sending Messages",new EbMSMessageTrafficChartSerie[]{EbMSMessageTrafficChartSerie.SEND_STATUS_NOK,EbMSMessageTrafficChartSerie.SEND_STATUS_WARN,EbMSMessageTrafficChartSerie.SEND_STATUS_OK,EbMSMessageTrafficChartSerie.SEND_STATUS});
	
	String title;
	EbMSMessageTrafficChartSerie[] ebMSMessageTrafficChartSeries;
}