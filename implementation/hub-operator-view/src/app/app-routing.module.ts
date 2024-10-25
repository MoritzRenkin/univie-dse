import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HubsComponent} from './hubs/hubs.component';
import {RouterModule, Routes} from '@angular/router';
import {ContainersComponent} from './containers/containers.component';
import {ContainerHistoryComponent} from './container-history/container-history.component';

const routes: Routes = [
  {
    path: 'hubs',
    component: HubsComponent
  },
  {
    path: 'containers',
    component: ContainersComponent
  },
  {
    path: 'containerHistory',
    component: ContainerHistoryComponent
  },
  {
    path: '',
    redirectTo: '/hubs',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/hubs',
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
