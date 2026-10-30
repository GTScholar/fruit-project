/**
 * 通用工具：格式化
 */

/** 金额格式化：保留两位小数 */
export function formatPrice(price) {
  return Number(price || 0).toFixed(2)
}

/** 订单状态映射 */
export const ORDER_STATUS = {
  0: { text: '待付款', type: 'warning' },
  1: { text: '待发货', type: 'primary' },
  2: { text: '待收货', type: 'success' },
  3: { text: '已完成', type: 'info' },
  4: { text: '已取消', type: 'danger' }
}

export function orderStatusText(status) {
  return ORDER_STATUS[status]?.text || '未知'
}

export function orderStatusType(status) {
  return ORDER_STATUS[status]?.type || 'info'
}

/** 商品图片兜底：加载失败或为空时显示水果色块 + emoji */
export function productFallbackImage(event) {
  event.target.src = ''
  event.target.style.background = 'linear-gradient(135deg, #fdba74, #86efac)'
  event.target.style.display = 'none'
  const parent = event.target.parentElement
  if (parent && !parent.querySelector('.img-fallback')) {
    const span = document.createElement('span')
    span.className = 'img-fallback'
    span.style.cssText =
      'position:absolute;inset:0;display:flex;align-items:center;justify-content:center;font-size:56px;' +
      'background:linear-gradient(135deg,#fff7ed,#ecfdf5);'
    span.textContent = '🍉'
    parent.appendChild(span)
  }
}
