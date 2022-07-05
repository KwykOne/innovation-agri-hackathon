import { Component, NgZone, OnInit } from '@angular/core';


import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_dark from "@amcharts/amcharts4/themes/amchartsdark"
import am4themes_animated from "@amcharts/amcharts4/themes/animated";
import { AppServices } from '../app.services';

@Component({
  selector: 'app-graphdata',
  templateUrl: './graphdata.component.html',
  styleUrls: ['./graphdata.component.scss']
})
export class GraphdataComponent implements OnInit {


  fetchFilter:{
    cropData:any,
    timeLine:any
  } =  {
    cropData:"",
    timeLine:""
  }

  cropValue:{
    name:any,
    Date:any,
    currentPrice:any,
    predictionprice:any,
    inc_dec:any,
  } = <{
    name:any,
    Date:any,
    currentPrice:any,
    predictionprice:any,
    inc_dec:any,
  }>{};


  private chart: am4charts.XYChart;
  private chart1: am4charts.XYChart;
  constructor(private zone: NgZone,
    public appService: AppServices) {

      console.log(appService.cropData);
      
     }

  ngOnInit(): void {
    
  }

data;
  GetData(){
    if(this.fetchFilter.cropData != "" ){
        this.appService.fetchData(this.fetchFilter).subscribe((obj:any)=>{
        // this.data = JSON.parse('{ "currentDate": "Thu, 30 Jan 2020 18:30:00 GMT", "currentPrice": 1400.0, "history": { "Day1": 1400.0, "Day2": 1400.0, "Day3": 1400.0, "Day4": 1400.0, "Day5": 1400.0 }, "max_forcast": { "Day1": 3025, "Day10": 2856, "Day11": 2841, "Day12": 2824, "Day2": 2997, "Day3": 2980, "Day4": 2967, "Day5": 2956, "Day6": 2942, "Day7": 2930, "Day8": 2904, "Day9": 2878 }, "min_forcast": { "Day1": 2694, "Day10": 2543, "Day11": 2529, "Day12": 2516, "Day2": 2669, "Day3": 2656, "Day4": 2645, "Day5": 2636, "Day6": 2624, "Day7": 2612, "Day8": 2587, "Day9": 2563 }, "model_forcast": { "Day1": 3329, "Day10": 3150, "Day11": 3133, "Day12": 3112, "Day2": 3299, "Day3": 3278, "Day4": 3262, "Day5": 3250, "Day6": 3235, "Day7": 3224, "Day8": 3199, "Day9": 3173 }, "name": "beans" }');
        this.data = obj
        this.cropValue.name = this.data.name;
        this.cropValue.Date = this.data.currentDate;
        this.cropValue.currentPrice = this.data.currentPrice;
        this.cropValue.predictionprice = this.data.model_forcast.Day1;
        this.cropValue.inc_dec = (this.cropValue.predictionprice - this.cropValue.currentPrice)
        this.getPredicationGraph(this.data);
        this.getHistoryGraph(this.data);
        })
    }
  }

  getPredicationGraph(prediactionData:any){
    this.zone.runOutsideAngular(() => {
      am4core.useTheme(am4themes_dark);
      am4core.useTheme(am4themes_animated);
      let chart = am4core.create("chartdiv", am4charts.XYChart);

      chart.paddingRight = 20;

      let data = [];
      let visits = 10;
      // for (let i = 1; i < data.length; i++) {
      //     visits += Math.round((Math.random() < 0.5 ? 1 : -1) * Math.random() * 10);
      //     data.push({ date: new Date(2018, 0, i), name: "name" + i, value: visits ,value1: (visits+100)});
      // }
      let max_predict = prediactionData.max_forcast;
      let min_predict = prediactionData.min_forcast;
      let actual_predict = prediactionData.model_forcast;
      console.log("max_predict   ",max_predict);
      let i =0;
      for (var prop in max_predict){
        console.log(prop);
        
        data.push({
          "day":prop,
          "name": prop,
          "value":max_predict[prop],
          "value1":min_predict[prop],
          "value2":actual_predict[prop]
        });
      }

      console.log(data);
      
      chart.data = data;

      let dateAxis = chart.xAxes.push(new am4charts.CategoryAxis());
      dateAxis.dataFields.category = "day";
      // dateAxis.renderer.minWidth = 35;

      let valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
      // valueAxis.tooltip.disabled = true;
      valueAxis.renderer.minWidth = 35;

      let series = chart.series.push(new am4charts.LineSeries());
      series.dataFields.categoryX = "day";
      series.stroke = am4core.color("#0505f5");
      series.dataFields.valueY = "value";

      let series2 = chart.series.push(new am4charts.LineSeries());
      series2.dataFields.categoryX = "day";
      series2.stroke = am4core.color("#05f545");
      series2.dataFields.valueY = "value1";

      let series3 = chart.series.push(new am4charts.LineSeries());
      series3.dataFields.categoryX = "day";
      series3.stroke = am4core.color("#f50d05");
      series3.dataFields.valueY = "value2";

      series.tooltipText = "{valueY.value, valueY.value1, valueY.value2}";
      
      chart.cursor = new am4charts.XYCursor();

      let scrollbarX = new am4charts.XYChartScrollbar();
      scrollbarX.series.push(series);
      scrollbarX.series.push(series2);
      scrollbarX.series.push(series3);
      // chart.scrollbarX = scrollbarX;

     setTimeout(() => {
      this.chart = chart;
     }, 100);
  });
  }

  getHistoryGraph(prediactionData:any){
    this.zone.runOutsideAngular(() => {
      am4core.useTheme(am4themes_dark);
      am4core.useTheme(am4themes_animated);
      let chart = am4core.create("HistoryPrice", am4charts.XYChart);

      chart.paddingRight = 20;

      let data = [];
      let visits = 10;
      let history = prediactionData.history;
      console.log("max_predict   ",history);
      let i =0;
      for (var prop in history){
        console.log(prop);
        
        data.push({
          "day":prop,
          "name": prop,
          "value":history[prop]
        });
      }

      console.log(data);
      
      chart.data = data;

      let dateAxis = chart.xAxes.push(new am4charts.CategoryAxis());
      dateAxis.dataFields.category = "day";
      // dateAxis.tooltip.disabled = true;
      // dateAxis.renderer.minWidth = 35;
      

      let valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
      // valueAxis.tooltip.disabled = true;
      valueAxis.renderer.minWidth = 35;

      let series = chart.series.push(new am4charts.LineSeries());
      series.dataFields.categoryX = "day";
      series.stroke = am4core.color("#0505f5");
      series.dataFields.valueY = "value";

      series.tooltipText = "{valueY.value}";
      
      chart.cursor = new am4charts.XYCursor();

      let scrollbarX = new am4charts.XYChartScrollbar();
      scrollbarX.series.push(series);

     setTimeout(() => {
      this.chart1 = chart;
     }, 100);
  });
  }
}
