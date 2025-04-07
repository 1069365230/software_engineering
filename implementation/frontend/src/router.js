import { createRouter, createWebHistory } from 'vue-router'
import DashBoardLogin from '@/components/DashBoardLogin.vue'
import DashBoard from '@/components/DashBoard.vue'
import DashBoardOrganizer from '@/components/DashBoardOrganizer.vue'
import DashBoardAdmin from '@/components/DashBoardAdmin.vue'

const routes = [
    { name: 'DashBoardLogin', path: '/', component: DashBoardLogin },
    { name: 'DashBoard', path: '/dash-board', component: DashBoard },
    { name: 'DashBoardOrganizer', path: '/dash-board-organizer', component: DashBoardOrganizer },
    { name: 'DashBoardAdmin', path: '/dash-board-admin', component: DashBoardAdmin },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router