import auth from '../middleware/auth'
const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/Index.vue')},      
      { path: 'products', component: () => import('pages/Products.vue')},
      { path: 'inventory', component: () => import('pages/Inventory.vue')}
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/Error404.vue')
  }
]

export default routes
