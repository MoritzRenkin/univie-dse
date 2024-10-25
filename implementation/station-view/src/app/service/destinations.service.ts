import {Injectable} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {catchError, retry} from 'rxjs/operators';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class DestinationsService {

  private REST_API_SERVERS = [
    'http://localhost:10002',
    'http://localhost:10003',
    'http://localhost:10004',
    'http://localhost:10005',
    'http://localhost:10006',
    'http://localhost:10007',
    'http://localhost:10008',
    'http://localhost:10009',
    'http://localhost:10010',
    'http://localhost:10011',
    'http://localhost:10012',
  ];

  constructor(private httpClient: HttpClient) {
  }

  getAllContainers = (serverId: number): Observable<any> => {
    return this.httpClient.get(this.REST_API_SERVERS[serverId - 1] + '/destination/all-containers')
      .pipe(retry(3), catchError(this.handleError));
  }

  getContainer = (serverId: number, uuid: string): Observable<any> => {
    return this.httpClient.get(this.REST_API_SERVERS[serverId - 1] + '/destination/' + uuid)
      .pipe(retry(3), catchError(this.handleError));
  }

  pickUpContainer = (serverId: number, uuid: string): Observable<any> => {
    return this.httpClient.delete(this.REST_API_SERVERS[serverId - 1] + '/destination/pickup/' + uuid)
      .pipe(retry(3), catchError(this.handleError));
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
}
