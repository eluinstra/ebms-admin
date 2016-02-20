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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nl.clockwork.ebms.Constants.EbMSMessageStatus;
import nl.clockwork.ebms.admin.Constants.EbMSMessageTrafficChartOption;
import nl.clockwork.ebms.admin.Constants.EbMSMessageTrafficChartSerie;
import nl.clockwork.ebms.admin.Constants.TimeUnit;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
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
	private Chart chart;

	public TrafficChartPage()
	{
		this(getTrafficChartFormModel(TimeUnit.DAY,EbMSMessageTrafficChartOption.ALL));
	}
	
	public TrafficChartPage(TrafficChartFormModel model)
	{
		add(new BootstrapFeedbackPanel("feedback"));
    add(new TrafficChartForm("form",model));
  }
	
	private Options createOptions(TrafficChartFormModel model)
	{
		DateTime from = new DateTime(model.from.getTime());
		DateTime to = from.plus(model.timeUnit.getPeriod());

		List<Date> dates = new ArrayList<Date>();
		while (from.isBefore(to))
		{
			dates.add(from.toDate());
			from = from.plus(model.timeUnit.getTimeUnit());
		}

		List<String> dateString = new ArrayList<String>();
		for (Date date : dates)
			dateString.add(new SimpleDateFormat(model.timeUnit.getTimeUnitDateFormat()).format(date));

		Options result = new Options();
		result.setChartOptions(new ChartOptions().setType(SeriesType.LINE));
		result.setTitle(new Title(model.getEbMSMessageTrafficChartOption().getTitle() + " " + new SimpleDateFormat(model.timeUnit.getDateFormat()).format(model.getFrom())));
		result.setxAxis(new Axis().setCategories(dateString));
		result.setyAxis(new Axis().setTitle(new Title("Messages")));
		result.setLegend(new Legend().setLayout(LegendLayout.VERTICAL).setAlign(HorizontalAlignment.RIGHT).setVerticalAlign(VerticalAlignment.TOP).setX(0).setY(1000).setBorderWidth(0));

		for (EbMSMessageTrafficChartSerie status : model.getEbMSMessageTrafficChartOption().getEbMSMessageTrafficChartSeries())
		{
			List<Number> receivedMessages = getMessages(dates,model,status.getEbMSMessageStatuses());
			result.addSeries(new SimpleSeries().setName(status.getName()).setColor(status.getColor()).setData(receivedMessages));
		}
		
		return result;
	}

	private static TrafficChartFormModel getTrafficChartFormModel(TimeUnit timeUnit, EbMSMessageTrafficChartOption ebMSMessageTrafficChartOption)
	{
		DateTime from = timeUnit.getFrom();
		DateTime to = from.plus(timeUnit.getPeriod());
		return new TrafficChartFormModel(timeUnit,from.toDate(),to.toDate(),ebMSMessageTrafficChartOption);
	}

	private List<Number> getMessages(List<Date> dates, TrafficChartFormModel model, EbMSMessageStatus...status)
	{
		List<Number> result = new ArrayList<Number>();
		HashMap<Date,Number> messageTraffic = ebMSDAO.selectMessageTraffic(model.from,new DateTime(model.from.getTime()).plus(model.timeUnit.getPeriod()).toDate(),model.timeUnit,status);
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
		return getLocalizer().getString("trafficChart",this);
	}
	
	public class TrafficChartForm extends Form<TrafficChartFormModel>
	{
		private static final long serialVersionUID = 1L;
		
		public TrafficChartForm(String id, TrafficChartFormModel model)
		{
			super(id,new CompoundPropertyModel<TrafficChartFormModel>(model));
			add(createTimeUnitsChoice("timeUnits"));
			add(createMinusLink("minus"));
			add(createPlusLink("plus"));
	    add(new Chart("chart",createOptions(model)));
			add(createEbMSMessageTrafficChartOptions("ebMSMessageTrafficChartOptions"));
		}

		private DropDownChoice<TimeUnit> createTimeUnitsChoice(String id)
		{
			DropDownChoice<TimeUnit> result = new DropDownChoice<TimeUnit>(id,new PropertyModel<TimeUnit>(this.getModelObject(),"timeUnit"),new PropertyModel<List<TimeUnit>>(this.getModelObject(),"timeUnits"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.timeUnit",TrafficChartForm.this));
				}
				
				@Override
				protected boolean localizeDisplayValues()
				{
					return true;
				}
			};
			result.setRequired(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					TrafficChartFormModel model = TrafficChartForm.this.getModelObject();
					model.setFrom(model.getTimeUnit().getFrom().toDate());
					chart.setOptions(createOptions(model));
					target.add(chart);
				}
			});
			return result;
		}

		private AjaxLink<Void> createMinusLink(String id)
		{
			return new AjaxLink<Void>(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					TrafficChartFormModel model = TrafficChartForm.this.getModelObject();
					model.setFrom(new DateTime(model.getFrom().getTime()).minus(model.getTimeUnit().getPeriod()).toDate());
					chart.setOptions(createOptions(model));
					target.add(chart);
				}
			};
		}

		private AjaxLink<Void> createPlusLink(String id)
		{
			return new AjaxLink<Void>(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					TrafficChartFormModel model = TrafficChartForm.this.getModelObject();
					model.setFrom(new DateTime(model.getFrom().getTime()).plus(model.getTimeUnit().getPeriod()).toDate());
					chart.setOptions(createOptions(model));
					target.add(chart);
				}
			};
		}

		private DropDownChoice<EbMSMessageTrafficChartOption> createEbMSMessageTrafficChartOptions(String id)
		{
			DropDownChoice<EbMSMessageTrafficChartOption> ebMSMessageTrafficChartOptions = new DropDownChoice<EbMSMessageTrafficChartOption>(id,new PropertyModel<EbMSMessageTrafficChartOption>(this.getModelObject(),"ebMSMessageTrafficChartOption"),new PropertyModel<List<EbMSMessageTrafficChartOption>>(this.getModelObject(),"ebMSMessageTrafficChartOptions"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public IModel<String> getLabel()
				{
					return Model.of(getLocalizer().getString("lbl.ebMSMessageTrafficChartOption",TrafficChartForm.this));
				}
				
				@Override
				protected boolean localizeDisplayValues()
				{
					return true;
				}
			};
			ebMSMessageTrafficChartOptions.setRequired(true);
			ebMSMessageTrafficChartOptions.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					TrafficChartFormModel model = TrafficChartForm.this.getModelObject();
					chart.setOptions(createOptions(model));
					target.add(chart);
				}
			});
			return ebMSMessageTrafficChartOptions;
		}
	}
	
	public static class TrafficChartFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private TimeUnit timeUnit;
		private Date from;
		private EbMSMessageTrafficChartOption ebMSMessageTrafficChartOption;
		
		public TrafficChartFormModel(TimeUnit timeUnit, Date from, Date to, EbMSMessageTrafficChartOption ebMSMessageTrafficChartOption)
		{
			this.timeUnit = timeUnit;
			this.from = from;
			this.ebMSMessageTrafficChartOption = ebMSMessageTrafficChartOption;
		}
		public List<TimeUnit> getTimeUnits()
		{
			return Arrays.asList(TimeUnit.values());
		}
		public TimeUnit getTimeUnit()
		{
			return timeUnit;
		}
		public void setTimeUnit(TimeUnit timeUnit)
		{
			this.timeUnit = timeUnit;
		}
		public Date getFrom()
		{
			return from;
		}
		public void setFrom(Date from)
		{
			this.from = from;
		}
		public List<EbMSMessageTrafficChartOption> getEbMSMessageTrafficChartOptions()
		{
			return Arrays.asList(EbMSMessageTrafficChartOption.values());
		}
		public EbMSMessageTrafficChartOption getEbMSMessageTrafficChartOption()
		{
			return ebMSMessageTrafficChartOption;
		}
		public void setEbMSMessageTrafficChartOption(EbMSMessageTrafficChartOption ebMSMessageTrafficChartOption)
		{
			this.ebMSMessageTrafficChartOption = ebMSMessageTrafficChartOption;
		}
	}

}
