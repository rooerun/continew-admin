import type { RouteRecordRaw } from 'vue-router'

/** 默认布局 */
const Layout = () => import('@/layout/index.vue')

/** 系统路由 */
export const systemRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true },
  },
  {
    path: '/',
    name: 'Dashboard',
    component: Layout,
    redirect: '/dashboard/workplace',
    meta: { title: '仪表盘', icon: 'dashboard', hidden: false },
    children: [
      {
        path: '/dashboard/workplace',
        name: 'Workplace',
        component: () => import('@/views/dashboard/workplace/index.vue'),
        meta: { title: '工作台', icon: 'desktop', hidden: false, affix: true },
      },
      {
        path: '/dashboard/analysis',
        name: 'Analysis',
        component: () => import('@/views/dashboard/analysis/index.vue'),
        meta: { title: '分析页', icon: 'insert-chart', hidden: false },
      },
    ],
  },
  {
    path: '/social/callback',
    component: () => import('@/views/login/social/index.vue'),
    meta: { hidden: true },
  },
  {
    path: '/pwdExpired',
    component: () => import('@/views/login/pwdExpired/index.vue'),
    meta: { hidden: true },
  },
  {
    path: '/user',
    name: 'User',
    component: Layout,
    meta: { hidden: true },
    children: [
      {
        path: '/user/profile',
        name: 'UserProfile',
        component: () => import('@/views/user/profile/index.vue'),
        meta: { title: '个人中心', showInTabs: false },
      },
      {
        path: '/user/message',
        name: 'UserMessage',
        component: () => import('@/views/user/message/index.vue'),
        meta: { title: '消息中心', showInTabs: false },
      },
      {
        path: '/user/notice',
        name: 'UserNotice',
        component: () => import('@/views/user/message/components/view/index.vue'),
        meta: { title: '查看公告' },
      },
    ],
  }
]

// 固定路由（默认路由）
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/redirect',
    component: Layout,
    meta: { hidden: true },
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/default/redirect/index.vue'),
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    component: () => import('@/views/default/error/404.vue'),
    meta: { hidden: true },
  },
  {
    path: '/403',
    component: () => import('@/views/default/error/403.vue'),
    meta: { hidden: true },
  },
]
