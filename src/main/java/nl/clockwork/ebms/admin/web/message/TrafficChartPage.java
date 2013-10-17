/**
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

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.admin.Constants.TimeUnit;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.BasePage;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.joda.time.DateTime;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.HorizontalAlignment;
import com.googlecode.wickedcharts.highcharts.options.Legend;
import com.googlecode.wickedcharts.highcharts.options.LegendLayout;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.VerticalAlignment;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

public class TrafficChartPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	private EbMSDAO ebMSDAO;

	public TrafficChartPage()
	{
		DateTime now = new DateTime();
		now = now.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0);
		DateTime to = now.toDateTime();
		DateTime from = now.minus(TimeUnit.HOURS.defaultPeriod());
		
		TrafficChartFormModel model = new TrafficChartFormModel(TimeUnit.HOURS,from.toDate(),to.toDate(),nl.clockwork.ebms.admin.Constants.allStatus);
		
		List<Date> dates = new ArrayList<Date>();
		while (from.isBefore(to))
		{
			dates.add(from.toDate());
			from = from.plus(model.timeUnit.period());
		}

		List<String> dateString = new ArrayList<String>();
		for (Date date : dates)
			dateString.add(new SimpleDateFormat(model.timeUnit.dateFormat()).format(date));

		List<Number> allMessages = getMessages(dates,model,nl.clockwork.ebms.admin.Constants.allStatus);
		List<Number> receivedMessages = getMessages(dates,model,nl.clockwork.ebms.admin.Constants.receiveStatus);
		List<Number> sentMessages = getMessages(dates,model,nl.clockwork.ebms.admin.Constants.sendStatus);
		
		Options options = new Options();
    options.setChartOptions(new ChartOptions().setType(SeriesType.LINE));
    options.setTitle(new Title("Message Traffic"));
    options.setxAxis(new Axis().setCategories(dateString));
    options.setyAxis(new Axis().setTitle(new Title("Messages")));
    options.setLegend(new Legend().setLayout(LegendLayout.VERTICAL).setAlign(HorizontalAlignment.RIGHT).setVerticalAlign(VerticalAlignment.TOP).setX(0).setY(1000).setBorderWidth(0));
    options.addSeries(new SimpleSeries().setName("Total").setColor(Color.BLACK).setData(allMessages));
    options.addSeries(new SimpleSeries().setName("Received").setColor(Color.BLUE).setData(receivedMessages));
    options.addSeries(new SimpleSeries().setName("Sent").setColor(Color.YELLOW).setData(sentMessages));
    add(new Chart("chart", options));
  }
	
	private List<Number> getMessages(List<Date> dates, TrafficChartFormModel model, EbMSMessageStatus...status)
	{
		List<Number> result = new ArrayList<Number>();
		HashMap<Date,Number> messageTraffic = ebMSDAO.selectMessageTraffic(model.from,model.to,model.timeUnit,status);
		for (Date date : dates)
			if (messageTraffic.containsKey(date))
				result.add(messageTraffic.get(date));
			else
				result.add(0);
		return result;
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("traffic",this);
	}
	
	public class TrafficChartFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private TimeUnit timeUnit;
		private Date from;
		private Date to;
		private EbMSMessageStatus[] status;
		
		public TrafficChartFormModel(TimeUnit timeUnit, Date from, Date to, EbMSMessageStatus...status)
		{
			this.timeUnit = timeUnit;
			this.from = from;
			this.to = to;
			this.status = status;
		}
		public TimeUnit getTimeUnit()
		{
			return timeUnit;
		}
		public Date getFrom()
		{
			return from;
		}
		public Date getTo()
		{
			return to;
		}
		public EbMSMessageStatus[] getStatus()
		{
			return status;
		}
	}

}
