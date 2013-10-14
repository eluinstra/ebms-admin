package nl.clockwork.ebms.admin.web.message;

import java.awt.Color;
import java.util.Arrays;

import nl.clockwork.ebms.admin.web.BasePage;

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

	public TrafficChartPage()
	{
		Options options = new Options();
    options.setChartOptions(new ChartOptions().setType(SeriesType.LINE));
    options.setTitle(new Title("Message Traffic"));
    options.setxAxis(new Axis().setCategories(Arrays.asList(new String[] {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"})));
    options.setyAxis(new Axis().setTitle(new Title("Messages")));
    options.setLegend(new Legend().setLayout(LegendLayout.VERTICAL).setAlign(HorizontalAlignment.RIGHT).setVerticalAlign(VerticalAlignment.TOP).setX(0).setY(1000).setBorderWidth(0));
    options.addSeries(new SimpleSeries().setName("Total Send").setColor(Color.BLACK).setData(Arrays.asList(new Number[] {75,69,95,150,182,215,252,265,335,483,539,960})));
    options.addSeries(new SimpleSeries().setName("Succesful Send").setColor(Color.GREEN).setData(Arrays.asList(new Number[] {70,69,95,145,182,215,252,265,333,483,539,960})));
    options.addSeries(new SimpleSeries().setName("Failed Send").setColor(Color.RED).setData(Arrays.asList(new Number[] {5,0,0,5,0,0,0,0,2,0,0,0})));
    options.addSeries(new SimpleSeries().setName("Total Received").setColor(Color.BLUE).setData(Arrays.asList(new Number[] {2,8,57,113,170,220,248,241,301,441,586,725})));
    add(new Chart("chart", options));
  }
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("traffic",this);
	}

}
