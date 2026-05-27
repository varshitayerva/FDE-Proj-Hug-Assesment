import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { StylistService } from '../../services/stylist.service';
import { UserProfileRequest, StylistRecommendationResponse } from '../../models/stylist.model';

@Component({
  selector: 'app-stylist',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './stylist.component.html',
  styleUrls: ['./stylist.component.css']
})
export class StylistComponent implements OnInit {
  profileForm!: FormGroup;
  loading = false;
  submitted = false;
  error = '';
  success = false;
  recommendation: StylistRecommendationResponse | null = null;
  expandedOutfitIndex: number | null = null;

  genderOptions = ['MALE', 'FEMALE', 'NON_BINARY', 'PREFER_NOT_TO_SAY'];
  ageGroupOptions = ['TEENS', 'TWENTIES', 'THIRTIES', 'FORTIES', 'FIFTIES_PLUS'];
  bodyTypeOptions = ['PETITE', 'PEAR', 'HOURGLASS', 'RECTANGLE', 'APPLE', 'INVERTED_TRIANGLE'];
  skinToneOptions = ['FAIR', 'LIGHT', 'MEDIUM', 'OLIVE', 'DEEP', 'DARK'];
  budgetOptions = ['BUDGET', 'MODERATE', 'PREMIUM', 'LUXURY'];
  occasionOptions = ['CASUAL', 'BUSINESS', 'PARTY', 'EVENING', 'WEEKEND', 'VACATION'];
  styleOptions = ['CLASSIC', 'TRENDY', 'SPORTY', 'BOHEMIAN', 'MINIMALIST', 'VINTAGE', 'EDGY', 'ROMANTIC'];
  weatherOptions = ['HOT', 'WARM', 'COOL', 'COLD', 'RAINY', 'SNOWY'];
  confidenceOptions = ['VERY_LOW', 'LOW', 'MEDIUM', 'HIGH', 'VERY_HIGH'];
  fitOptions = ['SLIM', 'REGULAR', 'LOOSE', 'OVERSIZED'];

  constructor(
    private formBuilder: FormBuilder,
    private stylistService: StylistService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    this.profileForm = this.formBuilder.group({
      gender: ['', Validators.required],
      ageGroup: ['', Validators.required],
      bodyType: ['', Validators.required],
      skinTone: ['', Validators.required],
      budgetRange: ['', Validators.required],
      occasionType: ['', Validators.required],
      preferredColors: ['', Validators.required],
      stylePreference: ['', Validators.required],
      weather: ['', Validators.required],
      confidenceLevel: ['', Validators.required],
      favoriteBrands: [''],
      fitPreference: ['', Validators.required],
      wardrobePreferences: ['']
    });
  }

  get f() { return this.profileForm.controls; }

  onSubmit(): void {
    this.submitted = true;
    this.error = '';
    this.success = false;

    if (this.profileForm.invalid) {
      return;
    }

    this.loading = true;
    const request: UserProfileRequest = this.profileForm.value;

    this.stylistService.getStylistRecommendation(request).subscribe({
      next: (response) => {
        this.recommendation = response;
        this.success = true;
        this.loading = false;
        window.scrollTo(0, document.getElementById('results')?.offsetTop || 0);
      },
      error: (error) => {
        this.error = error.message || 'Failed to get recommendations. Please try again.';
        this.loading = false;
      }
    });
  }

  formatLabel(label: string): string {
    return label.replace(/_/g, ' ').toLowerCase()
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }

  isSummaryTruncated(): boolean {
    if (!this.recommendation || !this.recommendation.summaryReport) {
      return false;
    }
    const lineCount = (this.recommendation.summaryReport.match(/\n/g) || []).length + 1;
    return lineCount > 7;
  }

  toggleOutfitExpand(index: number): void {
    this.expandedOutfitIndex = this.expandedOutfitIndex === index ? null : index;
  }
}
