import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SourcesComponent} from './sources/sources.component';
import {DestinationsComponent} from './destinations/destinations.component';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
  {
    path: 'source',
    component: SourcesComponent
  },
  {
    path: 'destination',
    component: DestinationsComponent
  },
  {
    path: '',
    redirectTo: '/source',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/source',
    pathMatch: 'full'
  }
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
