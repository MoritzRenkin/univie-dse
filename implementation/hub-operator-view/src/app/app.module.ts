import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { HubsComponent } from './hubs/hubs.component';
import { AppRoutingModule } from './app-routing.module';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { ContainersComponent } from './containers/containers.component';
import { ContainerHistoryComponent } from './container-history/container-history.component';

@NgModule({
  declarations: [
    AppComponent,
    HubsComponent,
    ContainersComponent,
    ContainerHistoryComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
