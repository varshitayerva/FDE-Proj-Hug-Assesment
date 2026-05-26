import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { StylistService } from '../../services/stylist.service';
import { Recommendation } from '../../models/stylist.model';

@Component({
  selector: 'app-recommendation-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './recommendation-detail.component.html',
  styleUrls: ['./recommendation-detail.component.css']
})
export class RecommendationDetailComponent implements OnInit {
  recommendation: Recommendation | null = null;
  loading = true;
  error = '';
  recommendationId: number = 0;

  constructor(
    private route: ActivatedRoute,
    private stylistService: StylistService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.recommendationId = params['id'];
      this.loadRecommendation();
    });
  }

  loadRecommendation(): void {
    this.stylistService.getRecommendationById(this.recommendationId).subscribe({
      next: (data) => {
        this.recommendation = data;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.message || 'Failed to load recommendation';
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/recommendations']);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
