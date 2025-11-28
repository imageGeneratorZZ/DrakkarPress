'use strict';

// Demo dataset (replace with API calls later)
const DEMO_ITEMS = [
  { id: 1, title: 'Sombras del Norte', author: 'I. Torres', price: 14.99, cover: 'https://picsum.photos/seed/1/400/600', category: 'Fantasía', premium: true, sales: 892, trend: 0.92, sellerSince: daysAgo(2) },
  { id: 2, title: 'Corazones en Llamas', author: 'M. González', price: 12.99, cover: 'https://picsum.photos/seed/2/400/600', category: 'Romance', premium: false, sales: 712, trend: 0.88, sellerSince: daysAgo(35) },
  { id: 3, title: 'El Último Caso', author: 'A. Martínez', price: 16.99, cover: 'https://picsum.photos/seed/3/400/600', category: 'Thriller', premium: true, sales: 963, trend: 0.95, sellerSince: daysAgo(5) },
  { id: 4, title: 'Estación Orbital', author: 'R. Silva', price: 13.49, cover: 'https://picsum.photos/seed/4/400/600', category: 'Ciencia Ficción', premium: false, sales: 540, trend: 0.90, sellerSince: daysAgo(1) },
  { id: 5, title: 'Bajo la Niebla', author: 'L. Fernández', price: 11.99, cover: 'https://picsum.photos/seed/5/400/600', category: 'Misterio', premium: false, sales: 480, trend: 0.81, sellerSince: daysAgo(8) },
  { id: 6, title: 'Fronteras del Saber', author: 'P. López', price: 17.99, cover: 'https://picsum.photos/seed/6/400/600', category: 'No Ficción', premium: true, sales: 430, trend: 0.76, sellerSince: daysAgo(60) },
  { id: 7, title: 'Cantos de Acero', author: 'E. Valdés', price: 15.49, cover: 'https://picsum.photos/seed/7/400/600', category: 'Fantasía', premium: false, sales: 650, trend: 0.86, sellerSince: daysAgo(3) },
  { id: 8, title: 'Laberinto Rojo', author: 'C. Díaz', price: 14.49, cover: 'https://picsum.photos/seed/8/400/600', category: 'Thriller', premium: true, sales: 720, trend: 0.89, sellerSince: daysAgo(12) },
  { id: 9, title: 'Órbita Cero', author: 'N. Henríquez', price: 13.99, cover: 'https://picsum.photos/seed/9/400/600', category: 'Ciencia Ficción', premium: true, sales: 380, trend: 0.80, sellerSince: daysAgo(0) },
  { id: 10, title: 'Ruta del Viento', author: 'S. Prado', price: 10.99, cover: 'https://picsum.photos/seed/10/400/600', category: 'Romance', premium: false, sales: 300, trend: 0.70, sellerSince: daysAgo(0) },
];

const CATEGORIES = ['Todo', 'Fantasía', 'Romance', 'Thriller', 'Ciencia Ficción', 'Misterio', 'No Ficción'];

function daysAgo(n){ const d = new Date(); d.setDate(d.getDate()-n); return d; }

function sortBestsellers(items){
  return [...items].sort((a,b)=> (b.sales - a.sales) || (b.premium - a.premium));
}
function sortTrending(items){
  return [...items].sort((a,b)=> (b.trend - a.trend) || (b.premium - a.premium));
}
function filterNewSellers(items){
  const THRESH = 14; // días
  const now = new Date();
  return [...items]
    .filter(i => (now - i.sellerSince) / (1000*60*60*24) <= THRESH)
    .sort((a,b)=> (b.premium - a.premium) || (b.sales - a.sales));
}
function filterPremium(items){
  return [...items].filter(i=>i.premium).sort((a,b)=> b.sales - a.sales);
}

function renderRow(containerId, items){
  const row = document.getElementById(containerId);
  row.innerHTML = '';
  items.forEach(item => row.appendChild(card(item)));
}

function card(item){
  const el = document.createElement('article');
  el.className = 'card';
  el.innerHTML = `
    <span class="badge ${item.premium ? 'premium':''}">${item.premium ? 'Premium' : 'No Premium'}</span>
    <img class="cover" src="${item.cover}" alt="${escapeHtml(item.title)}" />
    <div class="card-body">
      <h3 class="title">${escapeHtml(item.title)}</h3>
      <div class="author">${escapeHtml(item.author)} • ${escapeHtml(item.category)}</div>
      <div class="price">$${item.price.toFixed(2)}</div>
      <div class="meta">🛒 ${item.sales} • 📈 ${(item.trend*100|0)}%</div>
    </div>
  `;
  el.addEventListener('click', ()=> {
    window.location.href = `libro.html?id=${encodeURIComponent(item.id)}`;
  });
  return el;
}

function escapeHtml(s){ return String(s).replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[m])); }

function setActiveTab(tab){
  document.querySelectorAll('.tab').forEach(t=>t.classList.remove('active'));
  document.querySelector(`.tab[data-tab="${tab}"]`)?.classList.add('active');
  document.getElementById('tab-general').style.display = (tab==='general')?'block':'none';
  document.getElementById('tab-categorias').style.display = (tab==='categorias')?'block':'none';
}

function renderGeneral(){
  renderRow('row-bestsellers', sortBestsellers(DEMO_ITEMS).slice(0,10));
  renderRow('row-trending',   sortTrending(DEMO_ITEMS).slice(0,10));
  renderRow('row-new-sellers', filterNewSellers(DEMO_ITEMS).slice(0,10));
  renderRow('row-premium',     filterPremium(DEMO_ITEMS).slice(0,10));
}

function renderCategoryChips(){
  const box = document.getElementById('category-chips');
  box.innerHTML = '';
  CATEGORIES.forEach(cat=>{
    const c = document.createElement('button');
    c.type='button'; c.className='chip'+(cat==='Todo'?' active':'');
    c.textContent = cat; c.dataset.cat = cat;
    c.addEventListener('click', ()=>{
      document.querySelectorAll('.chip').forEach(x=>x.classList.remove('active'));
      c.classList.add('active');
      renderCategory(cat);
    });
    box.appendChild(c);
  });
}

function byCategory(cat){ return cat==='Todo' ? DEMO_ITEMS : DEMO_ITEMS.filter(i=>i.category===cat); }
function renderCategory(cat){
  const items = byCategory(cat);
  renderRow('cat-bestsellers', sortBestsellers(items).slice(0,10));
  renderRow('cat-trending',   sortTrending(items).slice(0,10));
  renderRow('cat-new-sellers', filterNewSellers(items).slice(0,10));
  renderRow('cat-premium',     filterPremium(items).slice(0,10));
}

function initTabs(){
  document.querySelectorAll('.tab').forEach(tab=>{
    tab.addEventListener('click', ()=> setActiveTab(tab.dataset.tab));
  });
}

function initSearch(){
  const q = document.getElementById('q');
  q?.addEventListener('keypress', (e)=>{
    if(e.key==='Enter'){
      const query = q.value.trim();
      if(!query) return;
      // For now, redirect to catalog with query param; later, use API
      window.location.href = `catalogo.html?q=${encodeURIComponent(query)}`;
    }
  });
}

(function main(){
  setActiveTab('general');
  renderGeneral();
  renderCategoryChips();
  renderCategory('Todo');
  initTabs();
  initSearch();
})();
