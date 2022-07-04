import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { GraphdataComponent } from './graphdata/graphdata.component';


const routes: Routes = [
  {
    path:'',
    component: GraphdataComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
