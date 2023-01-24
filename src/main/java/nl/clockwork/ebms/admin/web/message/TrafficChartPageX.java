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
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nl.clockwork.ebms.EbMSMessageStatus;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.AjaxFormComponentUpdatingBehavior;
import nl.clockwork.ebms.admin.web.AjaxLink;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.Consumer;
import nl.clockwork.ebms.admin.web.DropDownChoice;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrafficChartPageX extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "ebMSAdminDAO")
	EbMSDAO ebMSDAO;
	Chart chart;

	public TrafficChartPageX()
	{
		this(Model.of(getTrafficChartFormData(TimeUnit.DAY, EbMSMessageTrafficChartOption.ALL)));
	}

	public TrafficChartPageX(IModel<TrafficChartFormData> model)
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new TrafficChartForm("form", model));
	}

	private static TrafficChartFormData getTrafficChartFormData(TimeUnit timeUnit, EbMSMessageTrafficChartOption ebMSMessageTrafficChartOption)
	{
		val from = timeUnit.getFrom();
		return TrafficChartFormData.of(timeUnit, from, ebMSMessageTrafficChartOption);
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("trafficChart", this);
	}

	public class TrafficChartForm extends Form<TrafficChartFormData>
	{
		private static final long serialVersionUID = 1L;

		public TrafficChartForm(String id, IModel<TrafficChartFormData> model)
		{
			super(id, new CompoundPropertyModel<>(model));
			add(createTimeUnitChoice("timeUnit"));
			add(createMinusLink("minus"));
			add(createPlusLink("plus"));
			chart = new Chart("chart", createOptions());
			add(chart);
			add(createEbMSMessageTrafficChartOptions("ebMSMessageTrafficChartOptions"));
		}

		private DropDownChoice<TimeUnit> createTimeUnitChoice(String id)
		{
			val result = DropDownChoice.<TimeUnit>builder()
					.id(id)
					.choices(new PropertyModel<List<TimeUnit>>(getModel(), "timeUnits"))
					.localizeDisplayValues(() -> true)
					.build();
			result.setLabel(new ResourceModel("lbl.timeUnit"));
			result.setRequired(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				val o = getModelObject();
				o.setFrom(o.getTimeUnit().getFrom());
				chart.setOptions(createOptions());
				t.add(chart);
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change", onUpdate));
			return result;
		}

		private AjaxLink<Void> createMinusLink(String id)
		{
			Consumer<AjaxRequestTarget> onClick = t ->
			{
				val o = getModelObject();
				o.setFrom(o.getFrom().minus(o.getTimeUnit().getPeriod()));
				chart.setOptions(createOptions());
				t.add(chart);
			};
			return new AjaxLink<Void>(id, onClick);
		}

		private AjaxLink<Void> createPlusLink(String id)
		{
			Consumer<AjaxRequestTarget> onClick = t ->
			{
				val o = getModelObject();
				o.setFrom(o.getFrom().plus(o.getTimeUnit().getPeriod()));
				chart.setOptions(createOptions());
				t.add(chart);
			};
			return new AjaxLink<Void>(id, onClick);
		}

		private DropDownChoice<EbMSMessageTrafficChartOption> createEbMSMessageTrafficChartOptions(String id)
		{
			val o = getModelObject();
			val ebMSMessageTrafficChartOptions = DropDownChoice.<EbMSMessageTrafficChartOption>builder()
					.id(id)
					.model(new PropertyModel<EbMSMessageTrafficChartOption>(o, "ebMSMessageTrafficChartOption"))
					.choices(new PropertyModel<List<EbMSMessageTrafficChartOption>>(o, "ebMSMessageTrafficChartOptions"))
					.localizeDisplayValues(() -> true)
					.build();
			ebMSMessageTrafficChartOptions.setLabel(new ResourceModel("lbl.ebMSMessageTrafficChartOption"));
			ebMSMessageTrafficChartOptions.setRequired(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				chart.setOptions(createOptions());
				t.add(chart);
			};
			ebMSMessageTrafficChartOptions.add(new AjaxFormComponentUpdatingBehavior("change", onUpdate));
			return ebMSMessageTrafficChartOptions;
		}

		private Options createOptions()
		{
			val o = getModelObject();
			val dates = calculateDates(o.timeUnit.getTimeUnit(), o.from, o.getTo());
			val dateStrings = dates.stream().map(d -> o.timeUnit.getTimeUnitDateFormat().format(d)).collect(Collectors.toList());
			val result = new Options();
			result.setChartOptions(new ChartOptions().setType(SeriesType.LINE));
			result.setTitle(new Title(o.getEbMSMessageTrafficChartOption().getTitle() + " " + o.timeUnit.getDateFormatter().format(o.getFrom())));
			result.setxAxis(new Axis().setCategories(dateStrings));
			result.setyAxis(new Axis().setTitle(new Title("Messages")));
			result.setLegend(
					new Legend().setLayout(LegendLayout.VERTICAL)
							.setAlign(HorizontalAlignment.RIGHT)
							.setVerticalAlign(VerticalAlignment.TOP)
							.setX(0)
							.setY(1000)
							.setBorderWidth(0));
			result.setSeries(
					Arrays.stream(o.getEbMSMessageTrafficChartOption().getEbMSMessageTrafficChartSeries())
							.map(s -> new SimpleSeries().setName(s.getName()).setColor(s.getColorX()).setData(getMessages(dateStrings, s.getEbMSMessageStatuses())))
							.collect(Collectors.toList()));
			return result;
		}

		private List<Number> getMessages(List<String> dates, EbMSMessageStatus...status)
		{
			val o = getModelObject();
			val messageTraffic = ebMSDAO.selectMessageTraffic(o.from, o.getTo(), o.timeUnit, status);
			return dates.stream()
					.map(d -> messageTraffic.containsKey(Integer.parseInt(d)) ? messageTraffic.get(Integer.parseInt(d)) : 0)
					.collect(Collectors.toList());
		}

	}

	private List<LocalDateTime> calculateDates(TemporalAmount period, LocalDateTime from, LocalDateTime to)
	{
		val dates = new ArrayList<LocalDateTime>();
		while (from.isBefore(to))
		{
			dates.add(from);
			from = from.plus(period);
		}
		return dates;
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@NoArgsConstructor
	@AllArgsConstructor(staticName = "of")
	public static class TrafficChartFormData implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		@NonNull
		TimeUnit timeUnit;
		@NonNull
		LocalDateTime from;
		@NonNull
		EbMSMessageTrafficChartOption ebMSMessageTrafficChartOption;

		public List<TimeUnit> getTimeUnits()
		{
			return Arrays.asList(TimeUnit.values());
		}

		public LocalDateTime getTo()
		{
			return from.plus(timeUnit.getPeriod());
		}

		public List<EbMSMessageTrafficChartOption> getEbMSMessageTrafficChartOptions()
		{
			return Arrays.asList(EbMSMessageTrafficChartOption.values());
		}
	}

}
