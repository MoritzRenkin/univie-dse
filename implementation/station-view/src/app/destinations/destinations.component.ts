import {Component, OnInit} from '@angular/core';
import {Container} from '../sources/sources.component';
import {SourcesService} from '../service/sources.service';
import {DestinationsService} from '../service/destinations.service';

@Component({
  selector: 'app-destinations',
  templateUrl: './destinations.component.html',
  styleUrls: ['./destinations.component.css']
})
export class DestinationsComponent implements OnInit {
  public selectedStation = 1;
  private stationSubscriptions;
  public containers: SimpleContainer[];

  constructor(private dataService: DestinationsService) {
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

  onDeleteButtonClick(container: SimpleContainer): void {
    this.stationSubscriptions = this.dataService.pickUpContainer(this.selectedStation, container.id).subscribe((results) => {
      console.log(results);
    });
  }
}

export class SimpleContainer {
  id: string;
  currentLocation: string;
  destinationLocation: string;
  weight: number;
}
