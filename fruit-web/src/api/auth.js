import request from './request'

/** 登录 */
export const login = (data) => request.post('/auth/login', data)

/** 注册 */
export const register = (data) => request.post('/auth/register', data)

/** 获取当前用户信息 */
export const getInfo = () => request.get('/user/info')

/** 更新个人信息 */
export const updateInfo = (data) => request.put('/user/update', data)

/** 修改密码 */
export const updatePassword = (data) => request.put('/user/password', data)
