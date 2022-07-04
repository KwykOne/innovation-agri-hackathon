import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

import data from "../assets/data.json";

@Injectable({
    providedIn:"root"
})
export class AppServices{

    cropData;
    timeLineData;

    constructor(public httpClient: HttpClient){
      console.log(data);
      this.cropData = data.crop;
      
      this.timeLineData = data.timeLine
    }


    fetchData(data){
      return this.httpClient.post("http://127.0.0.1:8000/multicropdata",data)
    }
}