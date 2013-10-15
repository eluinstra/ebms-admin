package nl.clockwork.ebms.admin.web.message;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nl.clockwork.ebms.admin.Constants.TimeUnit;
import nl.clockwork.ebms.admin.dao.EbMSDAO;
import nl.clockwork.ebms.admin.web.BasePage;

import org.apache.wicket.spring.injection.annot.SpringBean;

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
	@SpringBean(name="ebMSDAO")
	private EbMSDAO ebMSDAO;

	public TrafficChartPage()
	{
		Calendar now = Calendar.getInstance();
		now.set(Calendar.MILLISECOND,0);
		now.set(Calendar.SECOND,0);
		now.set(Calendar.MINUTE,0);
		//now.add(Calendar.DAY_OF_YEAR,-5);
		Calendar to = (Calendar)now.clone();
		now.add(Calendar.DAY_OF_YEAR,-1);
		Calendar from = (Calendar)now.clone();
		List<Date> dates = new ArrayList<Date>();
		List<String> dateString = new ArrayList<String>();
		Calendar from_ = (Calendar)from.clone();
		while (from.before(to))
		{
			dates.add(from.getTime());
			dateString.add(new SimpleDateFormat("HH").format(from.getTime()));
			from.add(Calendar.HOUR,1);
		}
		from = from_;
		HashMap<Date,Number> chartItems = ebMSDAO.selectMessageTraffic(from.getTime(),to.getTime(),TimeUnit.HOURS);
		List<Number> data = new ArrayList<Number>();
		for (Date date : dates)
			if (chartItems.containsKey(date))
				data.add(chartItems.get(date));
			else
				data.add(0);

		Options options = new Options();
    options.setChartOptions(new ChartOptions().setType(SeriesType.LINE));
    options.setTitle(new Title("Message Traffic"));
    options.setxAxis(new Axis().setCategories(dateString));
    options.setyAxis(new Axis().setTitle(new Title("Messages")));
    options.setLegend(new Legend().setLayout(LegendLayout.VERTICAL).setAlign(HorizontalAlignment.RIGHT).setVerticalAlign(VerticalAlignment.TOP).setX(0).setY(1000).setBorderWidth(0));
    options.addSeries(new SimpleSeries().setName("Total").setColor(Color.BLACK).setData(data));
    add(new Chart("chart", options));
  }
	
	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("traffic",this);
	}

}
