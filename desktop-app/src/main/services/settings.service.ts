import { db } from '../database/connection';
import { UserSetting } from '../database/models';

class SettingsService {
  async get(key: string): Promise<any> {
    const settings: Record<string, UserSetting> = db.get('userSettings') || {};
    const setting = settings[key];
    
    if (!setting) return null;
    
    // Parse value based on type
    switch (setting.type) {
      case 'number':
        return parseFloat(setting.value);
      case 'boolean':
        return setting.value === 'true';
      case 'json':
        try {
          return JSON.parse(setting.value);
        } catch {
          return null;
        }
      default:
        return setting.value;
    }
  }

  async set(key: string, value: any, type: UserSetting['type'] = 'string'): Promise<UserSetting> {
    const settings: Record<string, UserSetting> = db.get('userSettings') || {};
    
    const stringValue = type === 'json' ? JSON.stringify(value) : String(value);
    
    const setting: UserSetting = {
      key,
      value: stringValue,
      type,
      updatedAt: new Date()
    };
    
    settings[key] = setting;
    db.set('userSettings', settings);
    
    return setting;
  }

  async getAll(): Promise<Record<string, any>> {
    const settings: Record<string, UserSetting> = db.get('userSettings') || {};
    const result: Record<string, any> = {};
    
    for (const key in settings) {
      result[key] = await this.get(key);
    }
    
    return result;
  }

  async delete(key: string): Promise<boolean> {
    const settings: Record<string, UserSetting> = db.get('userSettings') || {};
    
    if (!settings[key]) return false;
    
    delete settings[key];
    db.set('userSettings', settings);
    
    return true;
  }

  async initDefaults(): Promise<void> {
    const defaults = {
      theme: { value: 'dark', type: 'string' as const },
      language: { value: 'es', type: 'string' as const },
      autoSync: { value: true, type: 'boolean' as const },
      aiModel: { value: 'gpt-4', type: 'string' as const },
      aiTemperature: { value: 0.7, type: 'number' as const },
      voiceLanguage: { value: 'es-ES', type: 'string' as const },
      voiceEnabled: { value: false, type: 'boolean' as const }
    };
    
    for (const [key, config] of Object.entries(defaults)) {
      const exists = await this.get(key);
      if (exists === null) {
        await this.set(key, config.value, config.type);
      }
    }
  }
}

export default new SettingsService();
