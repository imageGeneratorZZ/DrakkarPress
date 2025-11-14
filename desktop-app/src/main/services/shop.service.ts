import { v4 as uuidv4 } from 'uuid';
import { db } from '../database/connection';
import { ShopProduct } from '../database/models';

class ShopService {
  async getAllProducts(): Promise<ShopProduct[]> {
    return db.get('shopProducts') || [];
  }

  async createProduct(data: Partial<ShopProduct>): Promise<ShopProduct> {
    const products: ShopProduct[] = db.get('shopProducts') || [];
    
    const newProduct: ShopProduct = {
      id: uuidv4(),
      creationId: data.creationId,
      title: data.title || 'Untitled Product',
      description: data.description,
      price: data.price || 0,
      currency: data.currency || 'USD',
      status: data.status || 'draft',
      format: data.format,
      salesCount: 0,
      publishedAt: data.publishedAt,
      createdAt: new Date(),
      updatedAt: new Date()
    };
    
    products.push(newProduct);
    db.set('shopProducts', products);
    
    return newProduct;
  }

  async updateProduct(id: string, data: Partial<ShopProduct>): Promise<ShopProduct | null> {
    const products: ShopProduct[] = db.get('shopProducts') || [];
    const index = products.findIndex(p => p.id === id);
    
    if (index === -1) return null;
    
    const updated: ShopProduct = {
      ...products[index],
      ...data,
      updatedAt: new Date()
    };
    
    products[index] = updated;
    db.set('shopProducts', products);
    
    return updated;
  }

  async deleteProduct(id: string): Promise<boolean> {
    const products: ShopProduct[] = db.get('shopProducts') || [];
    const filtered = products.filter(p => p.id !== id);
    
    if (filtered.length === products.length) return false;
    
    db.set('shopProducts', filtered);
    return true;
  }

  async publishProduct(id: string): Promise<ShopProduct | null> {
    const products: ShopProduct[] = db.get('shopProducts') || [];
    const index = products.findIndex(p => p.id === id);
    
    if (index === -1) return null;
    
    products[index] = {
      ...products[index],
      status: 'active',
      publishedAt: new Date(),
      updatedAt: new Date()
    };
    
    db.set('shopProducts', products);
    return products[index];
  }

  async getStats(): Promise<any> {
    const products: ShopProduct[] = db.get('shopProducts') || [];
    
    const totalProducts = products.length;
    const activeProducts = products.filter(p => p.status === 'active').length;
    const totalSales = products.reduce((sum, p) => sum + p.salesCount, 0);
    const totalRevenue = products.reduce((sum, p) => sum + (p.price * p.salesCount), 0);
    
    return {
      totalProducts,
      activeProducts,
      totalSales,
      totalRevenue
    };
  }
}

export default new ShopService();
