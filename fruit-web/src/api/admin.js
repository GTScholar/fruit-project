import request from './request'

/* ============ 管理端：商品 ============ */
export const adminProductPage = (params) => request.get('/admin/product/page', { params })
export const adminAddProduct = (data) => request.post('/admin/product', data)
export const adminUpdateProduct = (data) => request.put('/admin/product', data)
export const adminDeleteProduct = (id) => request.delete(`/admin/product/${id}`)
export const adminToggleProductStatus = (id, status) =>
  request.put(`/admin/product/${id}/status`, null, { params: { status } })
export const adminToggleProductHot = (id, hot) =>
  request.put(`/admin/product/${id}/hot`, null, { params: { hot } })

/* ============ 管理端：分类 ============ */
export const adminCategoryPage = (params) => request.get('/admin/category/page', { params })
export const adminAddCategory = (data) => request.post('/admin/category', data)
export const adminUpdateCategory = (data) => request.put('/admin/category', data)
export const adminDeleteCategory = (id) => request.delete(`/admin/category/${id}`)
export const adminToggleCategoryStatus = (id, status) =>
  request.put(`/admin/category/${id}/status`, null, { params: { status } })

/* ============ 管理端：订单 ============ */
export const adminOrderPage = (params) => request.get('/admin/order/page', { params })
export const adminOrderDetail = (id) => request.get(`/admin/order/${id}`)
export const adminDeliverOrder = (id) => request.put(`/admin/order/${id}/deliver`)

/* ============ 管理端：用户 ============ */
export const adminUserPage = (params) => request.get('/admin/user/page', { params })
export const adminToggleUserStatus = (id, status) =>
  request.put(`/admin/user/${id}/status`, null, { params: { status } })

/* ============ 管理端：轮播图 ============ */
export const adminBannerPage = (params) => request.get('/admin/banner/page', { params })
export const adminAddBanner = (data) => request.post('/admin/banner', data)
export const adminUpdateBanner = (data) => request.put('/admin/banner', data)
export const adminDeleteBanner = (id) => request.delete(`/admin/banner/${id}`)
export const adminToggleBannerStatus = (id, status) =>
  request.put(`/admin/banner/${id}/status`, null, { params: { status } })

/* ============ 管理端：统计 ============ */
export const adminStatsOverview = () => request.get('/admin/stats/overview')
export const adminStatsSalesTrend = () => request.get('/admin/stats/sales-trend')
export const adminStatsCategorySales = () => request.get('/admin/stats/category-sales')
export const adminStatsSystemStatus = () => request.get('/admin/stats/system-status')
