import { v4 as uuidv4 } from 'uuid';
import { db } from '../database/connection';
import { Creation, GenerationHistory } from '../database/models';

class CreationsService {
  async getAll(filters?: { type?: string; favorite?: boolean }): Promise<Creation[]> {
    let creations: Creation[] = db.get('creations') || [];
    
    if (filters?.type) {
      creations = creations.filter(c => c.type === filters.type);
    }
    if (filters?.favorite !== undefined) {
      creations = creations.filter(c => c.favorite === filters.favorite);
    }
    
    return creations;
  }

  async getById(id: string): Promise<Creation | null> {
    const creations: Creation[] = db.get('creations') || [];
    return creations.find(c => c.id === id) || null;
  }

  async create(data: Partial<Creation>): Promise<Creation> {
    const creations: Creation[] = db.get('creations') || [];
    
    const newCreation: Creation = {
      id: uuidv4(),
      title: data.title || 'Untitled',
      type: data.type || 'other',
      content: data.content || '',
      metadata: data.metadata,
      genreMix: data.genreMix,
      wordCount: data.content ? data.content.split(/\s+/).length : 0,
      tags: data.tags,
      favorite: data.favorite || false,
      synced: false,
      cloudId: data.cloudId,
      createdAt: new Date(),
      updatedAt: new Date()
    };
    
    creations.push(newCreation);
    db.set('creations', creations);
    
    return newCreation;
  }

  async update(id: string, data: Partial<Creation>): Promise<Creation | null> {
    const creations: Creation[] = db.get('creations') || [];
    const index = creations.findIndex(c => c.id === id);
    
    if (index === -1) return null;
    
    const updated: Creation = {
      ...creations[index],
      ...data,
      wordCount: data.content ? data.content.split(/\s+/).length : creations[index].wordCount,
      updatedAt: new Date()
    };
    
    creations[index] = updated;
    db.set('creations', creations);
    
    return updated;
  }

  async delete(id: string): Promise<boolean> {
    const creations: Creation[] = db.get('creations') || [];
    const filtered = creations.filter(c => c.id !== id);
    
    if (filtered.length === creations.length) return false;
    
    db.set('creations', filtered);
    return true;
  }

  async search(query: string): Promise<Creation[]> {
    const creations: Creation[] = db.get('creations') || [];
    const lowerQuery = query.toLowerCase();
    
    return creations.filter(c =>
      c.title.toLowerCase().includes(lowerQuery) ||
      (c.content && c.content.toLowerCase().includes(lowerQuery)) ||
      (c.tags && c.tags.toLowerCase().includes(lowerQuery))
    );
  }

  async exportCreation(id: string, format: 'txt' | 'md' | 'pdf' | 'epub' | 'docx'): Promise<string> {
    const creation = await this.getById(id);
    if (!creation) throw new Error('Creation not found');
    
    // TODO: Implement actual export formats
    // For now, return simple text
    return `${creation.title}\n\n${creation.content || ''}`;
  }

  async getStats(): Promise<any> {
    const creations: Creation[] = db.get('creations') || [];
    
    const totalCreations = creations.length;
    const totalWords = creations.reduce((sum, c) => sum + c.wordCount, 0);
    const favoriteCount = creations.filter(c => c.favorite).length;
    
    // Count by type
    const byType: Record<string, number> = {};
    creations.forEach(c => {
      byType[c.type] = (byType[c.type] || 0) + 1;
    });
    
    return {
      totalCreations,
      totalWords,
      favoriteCount,
      byType
    };
  }

  async saveHistory(data: Partial<GenerationHistory>): Promise<GenerationHistory> {
    const history: GenerationHistory[] = db.get('generationHistory') || [];
    
    const newHistory: GenerationHistory = {
      id: uuidv4(),
      creationId: data.creationId || '',
      generatorType: data.generatorType || '',
      prompt: data.prompt,
      result: data.result,
      tokensUsed: data.tokensUsed,
      generationTimeMs: data.generationTimeMs,
      createdAt: new Date()
    };
    
    history.push(newHistory);
    db.set('generationHistory', history);
    
    return newHistory;
  }

  async getHistory(creationId: string): Promise<GenerationHistory[]> {
    const history: GenerationHistory[] = db.get('generationHistory') || [];
    return history.filter(h => h.creationId === creationId);
  }
}

export default new CreationsService();
