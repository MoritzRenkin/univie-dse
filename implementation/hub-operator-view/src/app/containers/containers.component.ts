import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {ContainersService} from '../service/containers.service';
import * as uniqolor from 'uniqolor';

@Component({
  selector: 'app-containers',
  templateUrl: './containers.component.html',
  styleUrls: ['./containers.component.css']
})
export class ContainersComponent implements OnInit, OnDestroy {
  public containers: Container[] = [];
  public selectedContainerInput: FormControl;
  public selectedContainer: Container;
  private containersSubscriptions;
  private containerSubscription;

  constructor(private dataService: ContainersService) {
    this.selectedContainerInput = new FormControl('');
  }

  getAll(): void {
    this.containersSubscriptions = this.dataService.getContainerLocation().subscribe((results) => {
      this.containers = results;
    });
  }

  getByUUID(uuid: string): void {
    this.containerSubscription = this.dataService.getContainerLocationById(uuid).subscribe((result) => {
      this.selectedContainer = result;
    });
  }

  ngOnInit(): void {
    this.getAll();
  }

  onClick(): void {
    this.getByUUID(this.selectedContainerInput.value);
  }

  ngOnDestroy(): void {
    if (this.containerSubscription) {
      this.containerSubscription.unsubscribe();
    }
    this.containersSubscriptions.unsubscribe();
  }

  getColorOf(value: string): string {
    return uniqolor(value).color;
  }
}

export class Container {
  containerId: string;
  currentLocation: string;
  distanceToGo: number;
}
