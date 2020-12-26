package ddwu.mobile.finalproject.ma01_20181028;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Statistics extends AppCompatActivity {
    PieChart pieChart;
    PieChart pieChart2;
    MyBookDBManager myBookDBManager;
    ArrayList<MyBook> resultList;

     int a=0;
     int b=0;
     int c=0;
     int r1=0,r2=0,r3=0,r4=0,r5=0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        myBookDBManager = new MyBookDBManager(this);
        resultList = new ArrayList();


        pieChart = (PieChart)findViewById(R.id.piechart);
        pieChart2 = (PieChart)findViewById(R.id.piechart2);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);


        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.BLACK);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        setPieChart();

        yValues.add(new PieEntry(a,"읽기 전"));
        yValues.add(new PieEntry(b,"읽는 중"));
        yValues.add(new PieEntry(c,"완료"));


        Description description = new Description();
        description.setText("독서 상태"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"State");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(19f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);



        pieChart2.setUsePercentValues(true);
        pieChart2.getDescription().setEnabled(false);
        pieChart2.setExtraOffsets(5,10,5,5);

        pieChart2.setDragDecelerationFrictionCoef(0.95f);

        pieChart2.setDrawHoleEnabled(false);
        pieChart2.setHoleColor(Color.BLACK);
        pieChart2.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues2 = new ArrayList<PieEntry>();

        yValues2.add(new PieEntry(r1,"1점"));
        yValues2.add(new PieEntry(r2,"2점"));
        yValues2.add(new PieEntry(r3,"3점"));
        yValues2.add(new PieEntry(r4,"4점"));
        yValues2.add(new PieEntry(r5,"5점"));



        Description description2 = new Description();
        description2.setText("별점"); //라벨
        description2.setTextSize(15);
        pieChart2.setDescription(description2);

        pieChart2.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet2 = new PieDataSet(yValues2,"별점");
        dataSet2.setSliceSpace(3f);
        dataSet2.setSelectionShift(5f);
        dataSet2.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data2 = new PieData((dataSet2));
        data2.setValueTextSize(19f);
        data2.setValueTextColor(Color.BLACK);

        pieChart2.setData(data2);

    }
    public void setPieChart(){
        resultList = myBookDBManager.getAllProBook();
        for (int i = 0; i< resultList.size(); i++){
            switch(resultList.get(i).getBookState()){
                case "읽기전":
                   a += 1;
                    break;
                case "읽는중":
                    b += 1;
                    break;
                case "완료":
                    c += 1;
                    break;
            }

            switch (resultList.get(i).getRating()){
                case "1.0":
                    r1 += 1;
                    break;
                case "2.0":
                    r2 += 1;
                    break;
                case "3.0":
                    r3 += 1;
                    break;
                case "4.0":
                    r4 += 1;
                    break;
                case "5.0":
                    r5 += 1;
                    break;

            }
        }

    }

}
