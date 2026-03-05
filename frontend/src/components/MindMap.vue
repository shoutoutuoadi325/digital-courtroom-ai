<template>
  <div class="mindmap-wrapper">
    <svg ref="svgEl" :width="width" :height="height" class="mindmap-svg">
      <g ref="gEl" :transform="`translate(${centerX}, ${centerY})`">
        <g class="links">
          <line v-for="link in links" :key="link.id"
            :x1="link.x1" :y1="link.y1" :x2="link.x2" :y2="link.y2"
            :stroke="getLinkColor(link.type)" stroke-width="1.5" opacity="0.5" />
        </g>
        <g class="nodes">
          <g v-for="node in layoutNodes" :key="node.id"
            :transform="`translate(${node.x}, ${node.y})`"
            class="node-group" @click="selectedNode = node.id">
            <circle :r="node.radius"
              :fill="getNodeColor(node.type)"
              :stroke="selectedNode === node.id ? '#c9a84c' : 'white'"
              :stroke-width="selectedNode === node.id ? 3 : 1.5"
              opacity="0.9" />
            <text class="node-label" :y="node.radius + 14" text-anchor="middle"
              font-size="10" fill="#333" font-weight="600">
              {{ truncate(node.label, 16) }}
            </text>
            <text v-if="node.type !== 'root'" :y="4" text-anchor="middle"
              font-size="9" fill="white" font-weight="500">
              {{ getNodeIcon(node.type) }}
            </text>
          </g>
        </g>
      </g>
    </svg>
    <div class="mindmap-legend">
      <span v-for="item in legend" :key="item.type" class="legend-item">
        <span class="legend-dot" :style="{background: item.color}"></span>
        {{ item.label }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({ data: Object })

const width = 500
const height = 380
const centerX = width / 2
const centerY = height / 2 - 20
const selectedNode = ref(null)

const legend = [
  { type: 'root', color: '#1a2b4a', label: 'Root' },
  { type: 'input', color: '#1565c0', label: 'Input' },
  { type: 'method', color: '#2e7d32', label: 'Method' },
  { type: 'issues', color: '#e65100', label: 'Issues' },
  { type: 'remedies', color: '#6a1b9a', label: 'Remedies' },
  { type: 'steps', color: '#c62828', label: 'Steps' },
]

function getNodeColor(type) {
  const colors = {
    root: '#1a2b4a', input: '#1565c0', method: '#2e7d32',
    issues: '#e65100', remedies: '#6a1b9a', steps: '#c62828',
    law: '#00695c', element: '#0277bd', rule: '#558b2f',
    agent: '#4527a0', issue: '#bf360c', right: '#1b5e20',
    remedy: '#4a148c', action: '#b71c1c'
  }
  return colors[type] || '#546e7a'
}

function getLinkColor(type) {
  return getNodeColor(type)
}

function getNodeIcon(type) {
  const icons = {
    input: '📥', method: '⚙', issues: '⚖', remedies: '🔧', steps: '→',
    law: '📖', element: '•', rule: '📋', agent: '🤖',
    issue: '!', right: '✓', remedy: '💊', action: '▶'
  }
  return icons[type] || ''
}

function truncate(text, max) {
  if (!text) return ''
  return text.length > max ? text.substring(0, max - 1) + '…' : text
}

const layoutNodes = computed(() => {
  if (!props.data) return []
  const nodes = []
  const root = props.data

  nodes.push({ id: root.id, label: root.label, type: root.type || 'root', x: 0, y: 0, radius: 30 })

  const children = root.children || []
  const angleStep = (2 * Math.PI) / Math.max(children.length, 1)
  const r1 = 110

  children.forEach((child, i) => {
    const angle = i * angleStep - Math.PI / 2
    const cx = Math.cos(angle) * r1
    const cy = Math.sin(angle) * r1
    nodes.push({ id: child.id, label: child.label, type: child.type, x: cx, y: cy, radius: 20 })

    const subChildren = child.children || []
    const subAngleSpan = Math.PI / 3
    const subAngleStart = angle - subAngleSpan / 2
    const r2 = 185

    subChildren.forEach((sub, j) => {
      const subAngle = subChildren.length > 1
        ? subAngleStart + j * (subAngleSpan / (subChildren.length - 1))
        : angle
      const sx = Math.cos(subAngle) * r2
      const sy = Math.sin(subAngle) * r2
      nodes.push({ id: sub.id, label: sub.label, type: sub.type, x: sx, y: sy, radius: 13 })
    })
  })

  return nodes
})

const links = computed(() => {
  if (!props.data) return []
  const result = []
  const nodeMap = Object.fromEntries(layoutNodes.value.map(n => [n.id, n]))
  const root = props.data

  function addLinks(parent, children) {
    children.forEach(child => {
      const p = nodeMap[parent.id]
      const c = nodeMap[child.id]
      if (p && c) {
        result.push({ id: parent.id + '-' + child.id, x1: p.x, y1: p.y, x2: c.x, y2: c.y, type: child.type })
      }
      if (child.children) addLinks(child, child.children)
    })
  }

  if (root.children) addLinks(root, root.children)
  return result
})
</script>

<style scoped>
.mindmap-wrapper { display: flex; flex-direction: column; align-items: center; }
.mindmap-svg { overflow: visible; cursor: pointer; }
.node-group { cursor: pointer; transition: transform 0.15s; }
.node-group:hover { transform: scale(1.1); }
.node-label { font-family: system-ui, sans-serif; }
.mindmap-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  margin-top: 8px;
  font-size: 11px;
  color: var(--text-muted);
}
.legend-item { display: flex; align-items: center; gap: 4px; }
.legend-dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; }
</style>
