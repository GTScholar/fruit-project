import request from './request'

/** 购物车列表 */
export const getCartList = () => request.get('/cart/list')

/** 加入购物车 */
export const addToCart = (data) => request.post('/cart/add', data)

/** 修改数量 */
export const updateCartQuantity = (cartId, quantity) =>
  request.put(`/cart/quantity/${cartId}`, null, { params: { quantity } })

/** 勾选/取消勾选 */
export const updateCartChecked = (cartId, checked) =>
  request.put(`/cart/checked/${cartId}`, null, { params: { checked } })

/** 删除购物车记录 */
export const deleteCart = (cartId) => request.delete(`/cart/${cartId}`)
