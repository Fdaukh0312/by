import request from './request'

export const userApi = {
  login(data) {
    return request.post('/user/login', data)
  },
  register(data) {
    return request.post('/user/register', data)
  },
  getUserInfo() {
    return request.get('/user/info')
  },
  updateUserInfo(data) {
    return request.put('/user/info', data)
  },
  updatePassword(oldPassword, newPassword) {
    return request.put('/user/password', null, {
      params: { oldPassword, newPassword }
    })
  }
}
