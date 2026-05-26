import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { UserProfileRequest, StylistRecommendationResponse, Recommendation, ApiResponse } from '../models/stylist.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class StylistService {
  private apiUrl = `${environment.apiBaseUrl}/stylist`;

  constructor(private http: HttpClient) {}

  getStylistRecommendation(profileRequest: UserProfileRequest): Observable<StylistRecommendationResponse> {
    return this.http.post<ApiResponse<StylistRecommendationResponse>>(`${this.apiUrl}/recommend`, profileRequest).pipe(
      map(response => response.data),
      catchError(this.handleError)
    );
  }

  getUserRecommendations(): Observable<Recommendation[]> {
    return this.http.get<ApiResponse<Recommendation[]>>(`${this.apiUrl}/recommendations`).pipe(
      map(response => response.data),
      catchError(this.handleError)
    );
  }

  getRecommendationById(id: number): Observable<Recommendation> {
    return this.http.get<ApiResponse<Recommendation>>(`${this.apiUrl}/recommendation/${id}`).pipe(
      map(response => response.data),
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      errorMessage = error.error?.message || error.message || errorMessage;
    }
    return throwError(() => new Error(errorMessage));
  }
}
