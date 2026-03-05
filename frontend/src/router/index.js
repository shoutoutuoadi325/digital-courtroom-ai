import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import QueryView from '../views/QueryView.vue'
import ResultView from '../views/ResultView.vue'
import ReviewView from '../views/ReviewView.vue'
import TrialView from '../views/TrialView.vue'
import TrialRoomView from '../views/TrialRoomView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/query', component: QueryView },
  { path: '/query/:id', component: ResultView },
  { path: '/review', component: ReviewView },
  { path: '/trial', component: TrialView },
  { path: '/trial/:id', component: TrialRoomView }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
