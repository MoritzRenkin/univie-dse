import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, retry} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class HubsService {

  private REST_API_SERVER = 'http://localhost:10001';

  constructor(private httpClient: HttpClient) {
  }

  handleError = (error: HttpErrorResponse): Observable<never> => {
    let errorMessage;
    if (error.error instanceof ErrorEvent) {
      errorMessage = 'Error: ${error.error.message}'; // Client Error
    } else {
      errorMessage = 'Error Code: ${error.status}\nMessage: ${error.message}'; // Server Error
    }
    return throwError(errorMessage);
  }

  getHubOccupation = (): Observable<any> => {
    return this.httpClient.get(this.REST_API_SERVER + '/hub/occupation/').pipe(retry(3), catchError(this.handleError));
  }

  getHubOccupationById = (uuid: string): Observable<any> => {
    return this.httpClient.get(this.REST_API_SERVER + '/hub/occupation/' + uuid).pipe(retry(3), catchError(this.handleError));
  }
}
