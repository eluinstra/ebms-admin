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

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

import de.adesso.wickedcharts.chartjs.ChartConfiguration;
import de.adesso.wickedcharts.chartjs.chartoptions.AxesScale;
import de.adesso.wickedcharts.chartjs.chartoptions.ChartType;
import de.adesso.wickedcharts.chartjs.chartoptions.Data;
import de.adesso.wickedcharts.chartjs.chartoptions.Dataset;
import de.adesso.wickedcharts.chartjs.chartoptions.Hover;
import de.adesso.wickedcharts.chartjs.chartoptions.HoverMode;
import de.adesso.wickedcharts.chartjs.chartoptions.Options;
import de.adesso.wickedcharts.chartjs.chartoptions.ScaleLabel;
import de.adesso.wickedcharts.chartjs.chartoptions.Scales;
import de.adesso.wickedcharts.chartjs.chartoptions.Ticks;
import de.adesso.wickedcharts.chartjs.chartoptions.Title;
import de.adesso.wickedcharts.chartjs.chartoptions.TooltipMode;
import de.adesso.wickedcharts.chartjs.chartoptions.Tooltips;
import de.adesso.wickedcharts.chartjs.chartoptions.label.TextLabel;
import de.adesso.wickedcharts.chartjs.chartoptions.valueType.IntegerValue;
import de.adesso.wickedcharts.wicket8.chartjs.Chart;
import lombok.AccessLevel;
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
public class TrafficChartPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="ebMSAdminDAO")
	EbMSDAO ebMSDAO;
	Chart chart;

	public TrafficChartPage()
	{
		this(Model.of(getTrafficChartFormData(TimeUnit.DAY,EbMSMessageTrafficChartOption.ALL)));
	}
	
	public TrafficChartPage(IModel<TrafficChartFormData> model)
	{
		add(new BootstrapFeedbackPanel("feedback"));
    add(new TrafficChartForm("form",model));
  }
	
	private static TrafficChartFormData getTrafficChartFormData(TimeUnit timeUnit, EbMSMessageTrafficChartOption ebMSMessageTrafficChartOption)
	{
		val from = timeUnit.getFrom();
		return new TrafficChartFormData(timeUnit,from,ebMSMessageTrafficChartOption);
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("trafficChart",this);
	}
	
	public class TrafficChartForm extends Form<TrafficChartFormData>
	{
		private static final long serialVersionUID = 1L;
		
		public TrafficChartForm(String id, IModel<TrafficChartFormData> model)
		{
			super(id,new CompoundPropertyModel<>(model));
			add(createTimeUnitChoice("timeUnit"));
			add(createMinusLink("minus"));
			add(createPlusLink("plus"));
	    chart = new Chart("chart",createChartConfiguration());
			add(chart);
			add(createEbMSMessageTrafficChartOptions("ebMSMessageTrafficChartOptions"));
		}

		private DropDownChoice<TimeUnit> createTimeUnitChoice(String id)
		{
			val result = DropDownChoice.<TimeUnit>builder()
					.id(id)
					.choices(new PropertyModel<List<TimeUnit>>(getModel(),"timeUnits"))
					.localizeDisplayValues(() -> true)
					.build();
			result.setLabel(new ResourceModel("lbl.timeUnit"));
			result.setRequired(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				val o = getModelObject();
				o.setFrom(o.getTimeUnit().getFrom());
				chart.setChartConfiguration(createChartConfiguration());
				t.add(chart);
			};
			result.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return result;
		}

		private AjaxLink<Void> createMinusLink(String id)
		{
			Consumer<AjaxRequestTarget> onClick = t ->
			{
				val o = getModelObject();
				o.setFrom(o.getFrom().minus(o.getTimeUnit().getPeriod()));
				chart.setChartConfiguration(createChartConfiguration());
				t.add(chart);
			};
			return new AjaxLink<Void>(id,onClick);
		}

		private AjaxLink<Void> createPlusLink(String id)
		{
			Consumer<AjaxRequestTarget> onClick = t ->
			{
				val o = getModelObject();
				o.setFrom(o.getFrom().plus(o.getTimeUnit().getPeriod()));
				chart.setChartConfiguration(createChartConfiguration());
				t.add(chart);
			};
			return new AjaxLink<Void>(id,onClick);
		}

		private DropDownChoice<EbMSMessageTrafficChartOption> createEbMSMessageTrafficChartOptions(String id)
		{
			val o = getModelObject();
			val ebMSMessageTrafficChartOptions = DropDownChoice.<EbMSMessageTrafficChartOption>builder()
					.id(id)
					.model(new PropertyModel<EbMSMessageTrafficChartOption>(o,"ebMSMessageTrafficChartOption"))
					.choices(new PropertyModel<List<EbMSMessageTrafficChartOption>>(o,"ebMSMessageTrafficChartOptions"))
					.localizeDisplayValues(() -> true)
					.build();
			ebMSMessageTrafficChartOptions.setLabel(new ResourceModel("lbl.ebMSMessageTrafficChartOption"));
			ebMSMessageTrafficChartOptions.setRequired(true);
			Consumer<AjaxRequestTarget> onUpdate = t ->
			{
				chart.setChartConfiguration(createChartConfiguration());
				t.add(chart);
			};
			ebMSMessageTrafficChartOptions.add(new AjaxFormComponentUpdatingBehavior("change",onUpdate));
			return ebMSMessageTrafficChartOptions;
		}

		private ChartConfiguration createChartConfiguration()
		{
			val result = new ChartConfiguration();
			result.setType(ChartType.LINE);
			result.setOptions(createOptions());
			result.setData(createData());
			return result;
		}

		private Options createOptions()
		{
			val o = getModelObject();
			val options = new Options()
					.setResponsive(true)
					.setTitle(new Title()
						.setDisplay(true)
						.setText(o.getEbMSMessageTrafficChartOption().getTitle() + " " + o.timeUnit.getDateFormatter().format(o.getFrom())))
					.setTooltips(new Tooltips()
						.setMode(TooltipMode.INDEX)
						.setIntersect(false))
					.setHover(new Hover()
						.setMode(HoverMode.NEAREST)
						.setIntersect(true))
					.setScales(new Scales()
						.setXAxes(new AxesScale()
							.setDisplay(true)
							.setScaleLabel(new ScaleLabel()
								.setDisplay(true)
								.setLabelString(o.timeUnit.getUnits())))
						.setYAxes(new AxesScale()
							.setTicks(new Ticks()
								.setMaxTicksLimit(Integer.MAX_VALUE)
								.setMin(0))
							.setDisplay(true)
							.setScaleLabel(new ScaleLabel()
								.setDisplay(true)
								.setLabelString("Messages"))));
			return options;
		}

		private Data createData()
		{
			val o = getModelObject();
			val dates = calculateDates(o.timeUnit.getTimeUnit(),o.from,o.getTo());
			val dateStrings = dates.stream().map(d -> o.timeUnit.getTimeUnitDateFormat().format(d)).collect(Collectors.toList());
			return new Data().setLabels(TextLabel.of(dateStrings))
					.setDatasets(Arrays.stream(o.getEbMSMessageTrafficChartOption().getEbMSMessageTrafficChartSeries())
						.map(ds -> new Dataset()
								.setLabel(ds.getName())
								.setBackgroundColor(ds.getColor())
								.setBorderColor(ds.getColor())
								.setData(IntegerValue.of(getMessages(dateStrings,ds.getEbMSMessageStatuses())))
								.setFill(false))
						.collect(Collectors.toList()));
		}

		private List<Integer> getMessages(List<String> dates, EbMSMessageStatus...status)
		{
			val o = getModelObject();
			val messageTraffic = ebMSDAO.selectMessageTraffic(o.from,o.getTo(),o.timeUnit,status);
			return dates.stream().map(d -> messageTraffic.containsKey(Integer.parseInt(d)) ? messageTraffic.get(Integer.parseInt(d)) : 0).collect(Collectors.toList());
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

	@lombok.Data
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
