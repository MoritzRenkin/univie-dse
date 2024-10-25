import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {HubsService} from '../service/hubs.service';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-hubs',
  templateUrl: './hubs.component.html',
  styleUrls: ['./hubs.component.css']
})
export class HubsComponent implements OnInit, OnDestroy {

  @Input()
  public hubs: Hub[] = [];
  public selectedHubInput: FormControl;
  public selectedHub: Hub;
  private hubsSubscriptions;
  private hubSubscription;


  constructor(private dataService: HubsService) {
    this.selectedHubInput = new FormControl('');
  }

  getAll(): void {
    this.hubsSubscriptions = this.dataService.getHubOccupation().subscribe((results) => {
      this.hubs = results;
    });
  }

  getByUUID(uuid: string): void {
    this.hubSubscription = this.dataService.getHubOccupationById(uuid).subscribe((result) => {
      this.selectedHub = result;
    });
  }

  ngOnInit(): void {
    this.getAll();
  }

  onClick(): void {
    this.getByUUID(this.selectedHubInput.value);
  }

  ngOnDestroy(): void {
    if (this.hubSubscription) {
      this.hubSubscription.unsubscribe();
    }
    this.hubsSubscriptions.unsubscribe();
  }
}

export class Hub {
  hubId: string;
  fillingLevel: number;
}
