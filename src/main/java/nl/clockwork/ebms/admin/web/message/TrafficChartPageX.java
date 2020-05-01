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
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.joda.time.DateTime;

import de.adesso.wickedcharts.highcharts.options.Axis;
import de.adesso.wickedcharts.highcharts.options.ChartOptions;
import de.adesso.wickedcharts.highcharts.options.HorizontalAlignment;
import de.adesso.wickedcharts.highcharts.options.Legend;
import de.adesso.wickedcharts.highcharts.options.LegendLayout;
import de.adesso.wickedcharts.highcharts.options.Options;
import de.adesso.wickedcharts.highcharts.options.SeriesType;
import de.adesso.wickedcharts.highcharts.options.Title;
import de.adesso.wickedcharts.highcharts.options.VerticalAlignment;
import de.adesso.wickedcharts.highcharts.options.series.SimpleSeries;
import de.adesso.wickedcharts.wicket8.highcharts.Chart;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.AjaxFormComponentUpdatingBehavior;
import nl.clockwork.ebms.admin.web.AjaxLink;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.admin.web.DropDownChoice;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrafficChartPageX extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	EbMSDAO ebMSDAO;
	Chart chart;

	public TrafficChartPageX()
	{
		this(getTrafficChartFormModel(TimeUnit.DAY,EbMSMessageTrafficChartOption.ALL));
	}
	
	public TrafficChartPageX(TrafficChartFormModel model)
	{
		add(new BootstrapFeedbackPanel("feedback"));
    add(new TrafficChartForm("form",model));
  }
	
	private Options createOptions(TrafficChartFormModel model)
	{
		val from = new DateTime(model.from.getTime());
		val to = from.plus(model.timeUnit.getPeriod());
		val dates = calculateDates(model,from,to);
		val dateString = dates.stream().map(d -> new SimpleDateFormat(model.timeUnit.getTimeUnitDateFormat()).format(d)).collect(Collectors.toList());
		val result = new Options();
		result.setChartOptions(new ChartOptions().setType(SeriesType.LINE));
		result.setTitle(new Title(model.getEbMSMessageTrafficChartOption().getTitle() + " " + new SimpleDateFormat(model.timeUnit.getDateFormat()).format(model.getFrom())));
		result.setxAxis(new Axis().setCategories(dateString));
		result.setyAxis(new Axis().setTitle(new Title("Messages")));
		result.setLegend(new Legend().setLayout(LegendLayout.VERTICAL).setAlign(HorizontalAlignment.RIGHT).setVerticalAlign(VerticalAlignment.TOP).setX(0).setY(1000).setBorderWidth(0));
		result.setSeries(Arrays.stream(model.getEbMSMessageTrafficChartOption().getEbMSMessageTrafficChartSeries())
				.map(s -> new SimpleSeries().setName(s.getName()).setColor(s.getColorX()).setData(getMessages(dates,model,s.getEbMSMessageStatuses())))
				.collect(Collectors.toList()));
		return result;
	}

	private static TrafficChartFormModel getTrafficChartFormModel(TimeUnit timeUnit, EbMSMessageTrafficChartOption ebMSMessageTrafficChartOption)
	{
		val from = timeUnit.getFrom();
		val to = from.plus(timeUnit.getPeriod());
		return new TrafficChartFormModel(timeUnit,from.toDate(),to.toDate(),ebMSMessageTrafficChartOption);
	}

	private List<Number> getMessages(List<Date> dates, TrafficChartFormModel model, EbMSMessageStatus...status)
	{
		val messageTraffic = ebMSDAO.selectMessageTraffic(model.from,new DateTime(model.from.getTime()).plus(model.timeUnit.getPeriod()).toDate(),model.timeUnit,status);
		return dates.stream().map(d -> messageTraffic.containsKey(d) ? messageTraffic.get(d) : 0).collect(Collectors.toList());
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
			super(id,new CompoundPropertyModel<>(model));
			add(createTimeUnitChoice("timeUnit"));
			add(createMinusLink("minus"));
			add(createPlusLink("plus"));
	    chart = new Chart("chart",createOptions(model));
			add(chart);
			add(createEbMSMessageTrafficChartOptions("ebMSMessageTrafficChartOptions"));
		}

		private DropDownChoice<TimeUnit> createTimeUnitChoice(String id)
		{
			val result = DropDownChoice.<TimeUnit>builder()
					.id(id)
					.choices(new PropertyModel<List<TimeUnit>>(this.getModelObject(),"timeUnits"))
					.localizeDisplayValues(() -> true)
					.build();
			result.setLabel(new ResourceModel("lbl.timeUnit"));
			result.setRequired(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				val model = TrafficChartForm.this.getModelObject();
				model.setFrom(model.getTimeUnit().getFrom().toDate());
				chart.setOptions(createOptions(model));
				t.add(chart);
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private AjaxLink<Void> createMinusLink(String id)
		{
			Consumer<AjaxRequestTarget> onClick = t ->
			{
				TrafficChartFormModel model = TrafficChartForm.this.getModelObject();
				model.setFrom(new DateTime(model.getFrom().getTime()).minus(model.getTimeUnit().getPeriod()).toDate());
				chart.setOptions(createOptions(model));
				t.add(chart);
			};
			return new AjaxLink<Void>(id,onClick);
		}

		private AjaxLink<Void> createPlusLink(String id)
		{
			Consumer<AjaxRequestTarget> onClick = t ->
			{
				TrafficChartFormModel model = TrafficChartForm.this.getModelObject();
				model.setFrom(new DateTime(model.getFrom().getTime()).plus(model.getTimeUnit().getPeriod()).toDate());
				chart.setOptions(createOptions(model));
				t.add(chart);
			};
			return new AjaxLink<Void>(id,onClick);
		}

		private DropDownChoice<EbMSMessageTrafficChartOption> createEbMSMessageTrafficChartOptions(String id)
		{
			val ebMSMessageTrafficChartOptions = DropDownChoice.<EbMSMessageTrafficChartOption>builder()
					.id(id)
					.model(new PropertyModel<EbMSMessageTrafficChartOption>(this.getModelObject(),"ebMSMessageTrafficChartOption"))
					.choices(new PropertyModel<List<EbMSMessageTrafficChartOption>>(this.getModelObject(),"ebMSMessageTrafficChartOptions"))
					.localizeDisplayValues(() -> true)
					.build();
			ebMSMessageTrafficChartOptions.setLabel(new ResourceModel("lbl.ebMSMessageTrafficChartOption"));
			ebMSMessageTrafficChartOptions.setRequired(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				TrafficChartFormModel model = TrafficChartForm.this.getModelObject();
				chart.setOptions(createOptions(model));
				t.add(chart);
			};
			ebMSMessageTrafficChartOptions.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return ebMSMessageTrafficChartOptions;
		}
	}
	
	private List<Date> calculateDates(TrafficChartFormModel model, DateTime from, DateTime to)
	{
		val dates = new ArrayList<Date>();
		while (from.isBefore(to))
		{
			dates.add(from.toDate());
			from = from.plus(model.timeUnit.getTimeUnit());
		}
		return dates;
	}

	@Data
	public static class TrafficChartFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		TimeUnit timeUnit;
		@NonNull
		Date from;
		@NonNull
		Date to;
		@NonNull
		EbMSMessageTrafficChartOption ebMSMessageTrafficChartOption;
		
		public List<TimeUnit> getTimeUnits()
		{
			return Arrays.asList(TimeUnit.values());
		}
	}

}
