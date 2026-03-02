import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/views/front/Layout.vue'),
    children: [
      { path: '', component: () => import('@/views/front/Home.vue') },
      { path: 'article/:id', component: () => import('@/views/front/ArticleDetail.vue') },
      { path: 'category/:id', component: () => import('@/views/front/Category.vue') },
      { path: 'tag/:id', component: () => import('@/views/front/Tag.vue') },
      { path: 'tags/:ids', component: () => import('@/views/front/Tags.vue') },
      { path: 'archives', component: () => import('@/views/front/Archives.vue') }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/Layout.vue'),
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'article', component: () => import('@/views/admin/ArticleList.vue') },
      { path: 'article/edit/:id?', component: () => import('@/views/admin/ArticleEdit.vue') },
      { path: 'category', component: () => import('@/views/admin/Category.vue') },
      { path: 'tag', component: () => import('@/views/admin/Tag.vue') },
      { path: 'user', component: () => import('@/views/admin/User.vue') }
    ]
  },
  { path: '/admin/login', component: () => import('@/views/admin/Login.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path.startsWith('/admin') && to.path !== '/admin/login' && !token) {
    next('/admin/login')
  } else {
    next()
  }
})

export default router
