// Interfaces TypeScript para las entidades de datos (usando electron-store)

export interface Creation {
  id: string;
  title: string;
  type: 'book' | 'recipe' | 'report' | 'chapter' | 'other';
  content?: string;
  metadata?: string;
  genreMix?: string;
  wordCount: number;
  tags?: string;
  favorite: boolean;
  synced: boolean;
  cloudId?: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface GenerationHistory {
  id: string;
  creationId: string;
  generatorType: string;
  prompt?: string;
  result?: string;
  tokensUsed?: number;
  generationTimeMs?: number;
  createdAt: Date;
}

export interface UserSetting {
  key: string;
  value: string;
  type: 'string' | 'number' | 'boolean' | 'json';
  updatedAt: Date;
}

export interface ShopProduct {
  id: string;
  creationId?: string;
  title: string;
  description?: string;
  price: number;
  currency: string;
  status: 'draft' | 'active' | 'sold';
  format?: string;
  salesCount: number;
  publishedAt?: Date;
  createdAt: Date;
  updatedAt: Date;
}

export interface CommunityMessage {
  id: string;
  messageId: string;
  userId: string;
  username: string;
  message: string;
  channel: string;
  isRead: boolean;
  timestamp: Date;
  createdAt: Date;
}
