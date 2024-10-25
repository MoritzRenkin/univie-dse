import {Component, OnInit} from '@angular/core';
import {SourcesService} from '../service/sources.service';
import {FormControl} from '@angular/forms';
import * as uniqolor from 'uniqolor';

@Component({
  selector: 'app-sources',
  templateUrl: './sources.component.html',
  styleUrls: ['./sources.component.css']
})
export class SourcesComponent implements OnInit {

  public selectedStation = 1;
  private stationSubscriptions;
  public containers: Container[];
  public bulkContainerInput: FormControl;
  public destinationId: FormControl;
  public containerWeight: FormControl;


  constructor(private dataService: SourcesService) {
    this.bulkContainerInput = new FormControl(0);
    this.destinationId = new FormControl('');
    this.containerWeight = new FormControl(0);
  }

  ngOnInit(): void {
    this.getAll();
  }

  getAll(): void {
    this.stationSubscriptions = this.dataService.getAllContainers(this.selectedStation).subscribe((results) => {
      this.containers = results;
    });
  }

  onStationClick(stationId: number): void {
    this.selectedStation = stationId;
    this.getAll();
  }

  onBulkInsertInputClick(): void {
    this.dataService.insertBulkContainer(this.selectedStation, this.bulkContainerInput.value).subscribe((results) => {
      console.log(results);
    });
  }

  onInsertClick(): void {
    this.dataService.insertSingleContainer(this.selectedStation, this.destinationId.value, this.containerWeight.value)
      .subscribe((results) => {
        console.log(results);
      });
  }

  getColorOf(value: string): string {
    return uniqolor(value).color;
  }
}

export class Container {
  id: string;
  currentLocation: string;
  destinationLocation: string;
  weight: number;
}
