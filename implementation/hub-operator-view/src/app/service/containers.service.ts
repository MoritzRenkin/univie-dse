import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, retry} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ContainersService {


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

  getContainerLocation = (): Observable<any> => {
    return this.httpClient.get(this.REST_API_SERVER + '/container/location/').pipe(retry(3), catchError(this.handleError));
  }

  getContainerLocationById = (uuid: string): Observable<any> => {
    return this.httpClient.get(this.REST_API_SERVER + '/container/location/' + uuid).pipe(retry(3), catchError(this.handleError));
  }

  getContainerLocationHistory = (): Observable<any> => {
    return this.httpClient.get(this.REST_API_SERVER + '/container/history/').pipe(retry(3), catchError(this.handleError));
  }

  getContainerLocationHistoryById = (uuid: string): Observable<any> => {
    return this.httpClient.get(this.REST_API_SERVER + '/container/history/' + uuid).pipe(retry(3), catchError(this.handleError));
  }
}
