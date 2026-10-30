<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stat-grid">
      <div class="stat-card fruit-card stat-orange">
        <div class="stat-icon">👥</div>
        <div>
          <div class="stat-value">{{ stats.userCount ?? '-' }}</div>
          <div class="stat-label">注册用户</div>
        </div>
      </div>
      <div class="stat-card fruit-card stat-green">
        <div class="stat-icon">🍎</div>
        <div>
          <div class="stat-value">{{ stats.productCount ?? '-' }}</div>
          <div class="stat-label">在售商品</div>
        </div>
      </div>
      <div class="stat-card fruit-card stat-yellow">
        <div class="stat-icon">🧾</div>
        <div>
          <div class="stat-value">{{ stats.orderCount ?? '-' }}</div>
          <div class="stat-label">累计订单</div>
        </div>
      </div>
      <div class="stat-card fruit-card stat-red">
        <div class="stat-icon">💰</div>
        <div>
          <div class="stat-value">¥{{ formatPrice(stats.totalSales) }}</div>
          <div class="stat-label">销售总额</div>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="chart-grid">
      <div class="chart-card fruit-card">
        <div class="chart-title">📈 近7天销售趋势</div>
        <div ref="trendRef" class="chart-box"></div>
      </div>
      <div class="chart-card fruit-card">
        <div class="chart-title">🍇 分类销售占比</div>
        <div ref="pieRef" class="chart-box"></div>
      </div>
    </div>

    <!-- 今日动态 -->
    <div class="chart-card fruit-card today-card">
      <div class="chart-title">📌 今日动态</div>
      <div class="today-row">
        <span>今日新增订单：<b>{{ stats.todayOrderCount ?? '-' }}</b> 单</span>
        <span>系统状态：<el-tag :type="statusTag(systemStatus.mysql)" effect="light" round>{{ systemStatus.mysql ? '运行正常' : '连接异常' }}</el-tag></span>
        <span>缓存服务：<el-tag :type="statusTag(systemStatus.redis)" effect="light" round>{{ systemStatus.redis ? 'Redis 已连接' : 'Redis 未连接' }}</el-tag></span>
        <span>消息队列：<el-tag :type="statusTag(systemStatus.rabbitmq)" effect="light" round>{{ systemStatus.rabbitmq ? 'RabbitMQ 已连接' : 'RabbitMQ 未连接' }}</el-tag></span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { adminStatsOverview, adminStatsSalesTrend, adminStatsCategorySales, adminStatsSystemStatus } from '@/api/admin'
import { formatPrice } from '@/utils/format'

const stats = ref({})
const systemStatus = ref({ mysql: false, redis: false, rabbitmq: false })
const trendRef = ref()
const pieRef = ref()
let trendChart = null
let pieChart = null

function statusTag(ok) {
  return ok ? 'success' : 'danger'
}

async function loadData() {
  try {
    const [overviewRes, trendRes, categoryRes, statusRes] = await Promise.all([
      adminStatsOverview(),
      adminStatsSalesTrend(),
      adminStatsCategorySales(),
      adminStatsSystemStatus()
    ])
    stats.value = overviewRes.data
    renderTrend(trendRes.data)
    renderPie(categoryRes.data)
    systemStatus.value = statusRes.data
  } catch {
    /* 错误已提示 */
  }
}

function renderTrend(data) {
  const days = Array.from({ length: 7 }, (_, i) => {
    const d = new Date()
    d.setDate(d.getDate() - (6 - i))
    return `${d.getMonth() + 1}/${d.getDate()}`
  })
  const map = {}
  data.forEach((item) => (map[item.date] = Number(item.amount)))
  const values = days.map((label, i) => {
    const d = new Date()
    d.setDate(d.getDate() - (6 - i))
    const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    return map[key] || 0
  })

  trendChart = echarts.init(trendRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: days, axisLine: { lineStyle: { color: '#e5d9c8' } } },
    yAxis: {
      type: 'value',
      name: '销售额(元)',
      splitLine: { lineStyle: { color: '#f5eee4' } }
    },
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: values,
        symbolSize: 8,
        lineStyle: { width: 3, color: '#fb923c' },
        itemStyle: { color: '#f97316' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(251,146,60,0.35)' },
            { offset: 1, color: 'rgba(251,146,60,0.02)' }
          ])
        }
      }
    ]
  })
}

function renderPie(data) {
  if (!data || !data.length) {
    pieChart = echarts.init(pieRef.value)
    pieChart.setOption({ title: { text: '暂无销售数据', left: 'center', top: 'middle', textStyle: { color: '#b9a999' } } })
    return
  }
  pieChart = echarts.init(pieRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} 件 ({d}%)' },
    legend: { bottom: 0, textStyle: { color: '#8a7a6a' } },
    color: ['#fb923c', '#f97316', '#22c55e', '#86efac', '#facc15', '#f59e0b', '#34d399', '#fdba74'],
    series: [
      {
        name: '分类销量',
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '44%'],
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
        label: { color: '#6b5a48' },
        data: data.map((item) => ({
          name: item.categoryName || '未分类',
          value: Number(item.salesCount)
        }))
      }
    ]
  })
}

function handleResize() {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  pieChart?.dispose()
})
</script>

<style lang="scss" scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 22px 24px;
  transition: transform 0.25s;

  &:hover {
    transform: translateY(-4px);
  }

  .stat-icon {
    font-size: 40px;
  }

  .stat-value {
    font-size: 26px;
    font-weight: 900;
    color: $text-main;
  }

  .stat-label {
    margin-top: 4px;
    color: $text-sub;
    font-size: 13px;
  }
}

.stat-orange { background: linear-gradient(135deg, #fff7ed, #ffedd5); }
.stat-green { background: linear-gradient(135deg, #f0fdf4, #dcfce7); }
.stat-yellow { background: linear-gradient(135deg, #fefce8, #fef9c3); }
.stat-red { background: linear-gradient(135deg, #fff1f2, #ffe4e6); }

.chart-grid {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 18px;
  margin-top: 18px;
}

.chart-card {
  padding: 20px 24px;

  .chart-title {
    font-size: 15px;
    font-weight: 800;
    color: $text-main;
    margin-bottom: 12px;
  }

  .chart-box {
    height: 320px;
  }
}

.today-card {
  margin-top: 18px;

  .today-row {
    display: flex;
    align-items: center;
    gap: 32px;
    flex-wrap: wrap;
    color: $text-sub;
    font-size: 14px;

    b {
      color: $primary;
    }
  }
}

@media (max-width: 900px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
  .chart-grid { grid-template-columns: 1fr; }
}
</style>
