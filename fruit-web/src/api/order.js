import request from './request'

/** 创建订单 */
export const createOrder = (data) => request.post('/order/create', data)

/** 我的订单列表 */
export const getOrderList = (params) => request.get('/order/list', { params })

/** 订单详情 */
export const getOrderDetail = (id) => request.get(`/order/${id}`)

/** 模拟支付 */
export const payOrder = (id) => request.put(`/order/${id}/pay`)

/** 取消订单 */
export const cancelOrder = (id) => request.put(`/order/${id}/cancel`)

/** 确认收货 */
export const confirmOrder = (id) => request.put(`/order/${id}/confirm`)

/** 地址列表 */
export const getAddressList = () => request.get('/address/list')

/** 新增地址 */
export const addAddress = (data) => request.post('/address', data)

/** 修改地址 */
export const updateAddress = (data) => request.put('/address', data)

/** 删除地址 */
export const deleteAddress = (id) => request.delete(`/address/${id}`)

/** 设为默认 */
export const setDefaultAddress = (id) => request.put(`/address/${id}/default`)
