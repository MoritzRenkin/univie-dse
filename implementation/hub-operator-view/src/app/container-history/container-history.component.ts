import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {ContainersService} from '../service/containers.service';
import * as uniqolor from 'uniqolor';
import {Subscription} from 'rxjs';


@Component({
  selector: 'app-container-history',
  templateUrl: './container-history.component.html',
  styleUrls: ['./container-history.component.css']
})
export class ContainerHistoryComponent implements OnInit, OnDestroy {
  public singleContainerHistories: SingleContainerHistory[] = [];
  public selectedContainerInput: FormControl;
  public multiContainerHistory: MultiContainerHistory;
  private containersSubscriptions;
  private containerSubscription;

  constructor(private dataService: ContainersService) {
    this.selectedContainerInput = new FormControl('');
  }

  getAll(): void {
    this.containersSubscriptions = this.dataService.getContainerLocationHistory().subscribe((results) => {
      this.singleContainerHistories = results;
    });
  }

  getByUUID(uuid: string): void {
    this.containerSubscription = this.dataService.getContainerLocationHistoryById(uuid).subscribe((result) => {
      this.multiContainerHistory = result;
    });
  }

  ngOnInit(): void {
    this.getAll();
  }

  onClick(): void {
    this.getByUUID(this.selectedContainerInput.value);
    console.log(this.multiContainerHistory);
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

export class SingleContainerHistory {
  containerId: string;
  locationId: string;
}

export class MultiContainerHistory {
  containerId: string;
  visitedLocation: string[] = [];
}
