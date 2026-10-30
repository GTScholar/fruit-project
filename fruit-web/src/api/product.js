import request from './request'

/** 商品分页查询 */
export const getProductPage = (params) => request.get('/product/page', { params })

/** 热销商品 */
export const getHotProducts = () => request.get('/product/hot')

/** 商品详情 */
export const getProductDetail = (id) => request.get(`/product/${id}`)

/** 分类列表 */
export const getCategoryList = () => request.get('/category/list')

/** 轮播图列表 */
export const getBannerList = () => request.get('/banner/list')
