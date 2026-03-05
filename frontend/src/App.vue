<template>
  <div id="app">
    <nav class="navbar">
      <div class="container nav-inner">
        <router-link to="/" class="nav-brand">
          <span class="brand-icon">⚖️</span>
          <span>Digital Courtroom AI</span>
        </router-link>
        <div class="nav-links">
          <router-link to="/query" class="nav-link">Legal Query</router-link>
          <router-link to="/review" class="nav-link">HITL Review</router-link>
          <router-link to="/trial" class="nav-link">Mock Trial</router-link>
        </div>
        <button class="nav-toggle" @click="menuOpen = !menuOpen">☰</button>
      </div>
      <div class="nav-mobile" v-if="menuOpen">
        <router-link to="/query" @click="menuOpen=false">Legal Query</router-link>
        <router-link to="/review" @click="menuOpen=false">HITL Review</router-link>
        <router-link to="/trial" @click="menuOpen=false">Mock Trial</router-link>
      </div>
    </nav>
    <router-view />
    <footer class="footer">
      <div class="container">
        <p>© 2024 Digital Courtroom AI · AI-powered legal assistance · Not a substitute for professional legal advice</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const menuOpen = ref(false)
</script>

<style>
.navbar {
  background: var(--primary);
  color: white;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}
.nav-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}
.nav-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.1rem;
  font-weight: 700;
  color: white;
}
.brand-icon { font-size: 1.4rem; }
.nav-links { display: flex; gap: 4px; }
.nav-link {
  color: rgba(255,255,255,0.8);
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s;
}
.nav-link:hover, .router-link-active.nav-link {
  color: white;
  background: rgba(255,255,255,0.15);
}
.nav-toggle {
  display: none;
  background: none;
  border: none;
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
}
.nav-mobile {
  display: flex;
  flex-direction: column;
  padding: 8px 24px 16px;
  gap: 4px;
}
.nav-mobile a {
  color: rgba(255,255,255,0.85);
  padding: 10px 12px;
  border-radius: 6px;
  font-weight: 500;
}
.nav-mobile a:hover { background: rgba(255,255,255,0.1); color: white; }
@media (max-width: 768px) {
  .nav-links { display: none; }
  .nav-toggle { display: block; }
}
.footer {
  background: var(--primary);
  color: rgba(255,255,255,0.6);
  text-align: center;
  padding: 20px 0;
  font-size: 12px;
  margin-top: 60px;
}
</style>
