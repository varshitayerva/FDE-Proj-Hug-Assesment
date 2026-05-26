export interface UserProfileRequest {
  gender: string;
  ageGroup: string;
  bodyType: string;
  skinTone: string;
  budgetRange: string;
  occasionType: string;
  preferredColors: string;
  stylePreference: string;
  weather: string;
  confidenceLevel: string;
  favoriteBrands: string;
  fitPreference: string;
  wardrobePreferences: string;
}

export interface StyleAnalysisResponse {
  fashionPersona: string;
  bodyFitRecommendation: string;
  suitableColors: string[];
  styleMatchScore: number;
  confidenceScore: number;
  recommendedCategories: string[];
}

export interface StylistRecommendationResponse {
  recommendationId: number;
  styleAnalysis: StyleAnalysisResponse;
  outfitRecommendations: string;
  summaryReport: string;
  numberOfLlmCalls: number;
  isAdvancedRecommendation: boolean;
  message: string;
}

export interface Recommendation {
  id: number;
  userId: number;
  styleAnalysis: string;
  outfitRecommendations: string;
  summaryReport: string;
  styleMatchScore: number;
  confidenceScore: number;
  numberOfLlmCalls: number;
  isAdvancedRecommendation: boolean;
  createdAt: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  statusCode: number;
  error?: string;
}
