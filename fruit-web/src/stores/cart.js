import { defineStore } from 'pinia'

/**
 * 购物车数量共享状态（导航栏角标）
 * 购物车页删除/修改商品后调用 setCount 同步，角标即时更新无需刷新
 */
export const useCartStore = defineStore('cart', {
  state: () => ({
    count: 0
  }),
  actions: {
    /** 设置角标数量 */
    setCount(n) {
      this.count = n
    },
    /** 清空（退出登录时调用） */
    reset() {
      this.count = 0
    }
  }
})
